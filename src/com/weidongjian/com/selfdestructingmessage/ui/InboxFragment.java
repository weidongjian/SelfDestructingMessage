package com.weidongjian.com.selfdestructingmessage.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.weidongjian.com.selfdestructingmessage.ParseConstant;
import com.weidongjian.com.selfdestructingmessage.R;
import com.weidongjian.com.selfdestructingmessage.adapter.MessageAdapter;
import com.weidongjian.com.selfdestructingmessage.adapter.MessageCursorAdapter;
import com.weidongjian.com.selfdestructingmessage.models.Message;
import com.weidongjian.com.selfdestructingmessage.util.DatabaseHandler;
import com.weidongjian.com.selfdestructingmessage.util.MessageService;
import com.weidongjian.com.selfdestructingmessage.util.ServiceReceiver;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

public class InboxFragment extends ListFragment {
	public static final int REQUEST_CODE_VIEW_IMAGE = 10;
	protected List<ParseObject> mMessage;
	protected PullToRefreshListView listview;
	protected int location;
	private MessageAdapter adapter;
	protected ProgressDialog pd;
	private Gson gson = new Gson();
	private SharedPreferences sp;
	private long refreshDate;
	private DatabaseHandler databaseHandler;
	private MessageCursorAdapter messageAdapter;
	private long messageID;
//	private String messageObjectId;
	private static final String USERNAME = ParseUser.getCurrentUser()
			.getUsername();
	private ServiceReceiver serviceReceiver;

	// protected MenuItem progressBar = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = getActivity().getSharedPreferences("refreshDate", 0);
		refreshDate = sp.getLong(USERNAME, 0);
		System.out.println("oncreate SP " + USERNAME + " " + refreshDate);
		databaseHandler = new DatabaseHandler(getActivity());
		setupServiceReceiver();
		// pd.show(getActivity(), "loading", "please wait");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_inbox, container,
				false);
		listview = (PullToRefreshListView) rootView
				.findViewById(android.R.id.list);
		listview.setOnRefreshListener(refreshlistener);
		listview.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int position, long id) {
				final long itemId = id;
				new AlertDialog.Builder(getActivity())
						.setTitle("Confirm")
						.setMessage("The selected message will be deleted")
						.setPositiveButton(android.R.string.ok,
								new OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										Cursor c = databaseHandler.getOneMessage(itemId);
										c.moveToFirst();
										int objectIdColumn = c.getColumnIndex("objectId");
										String messageObjectId = c.getString(objectIdColumn);
										c.close();
										startDeleteService(itemId, messageObjectId);
									}
								})
						.setNegativeButton(android.R.string.cancel, null)
						.show();
				return false;
			}

		});
		return rootView;
	}

	@Override
	public void onStart() {
		super.onStart();
		System.out.println("call onStart");
		getActivity().setProgressBarIndeterminateVisibility(true);
		databaseHandler.open();
		new Handler().post(new Runnable() {
			@Override
			public void run() {
				// System.out.println("start setAdapter");
				Cursor c = databaseHandler.getAllMessage();
				messageAdapter = new MessageCursorAdapter(getActivity(), c);
				listview.setAdapter(messageAdapter);
				// System.out.println("finish setAdapter");
			}
		});
		retrieveMessageService();
		// retrieveMessages();
	}

	@Override
	public void onResume() {
		super.onResume();
		System.out.println("call onResume");
		// Cursor cursor = databaseHandler.getAllMessage();
		// if (cursor.moveToFirst()) {
		// do {
		// int index = cursor.getColumnIndex("objectId");
		// String objectID = cursor.getString(index);
		// System.out.println(objectID);
		// } while (cursor.moveToNext());
		// }
	}

	public void retrieveMessages() {
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
				ParseConstant.CLASS_MESSAGE);
		query.whereEqualTo(ParseConstant.KEY_RECEIVE_ID, ParseUser
				.getCurrentUser().getObjectId());
		query.orderByDescending(ParseConstant.KEY_CREATED_AT);
		query.whereGreaterThan("createdAt", getLastUpdatedDate());
		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> messages, ParseException e) {
				// if (pd != null && pd.isShowing()) {
				// pd.dismiss();
				// }
				refreshDate = new Date().getTime();
				SharedPreferences.Editor editor = sp.edit();
				editor.putLong(USERNAME, refreshDate);
				editor.commit();
				System.out.println("retrievermessage SP " + USERNAME + " "
						+ refreshDate);
				getActivity().setProgressBarIndeterminateVisibility(false);
				listview.onRefreshComplete();
				if (e == null) {
					mMessage = messages;
					if (!mMessage.isEmpty()) {
						addMessageToDatabase(mMessage);
						requery();
					}
					// if (getListView().getAdapter() == null) {
					// adapter = new MessageAdapter(getListView().getContext(),
					// mMessage);
					// setListAdapter(adapter);
					// }
					// else {
					// ((MessageAdapter)(getListView().getAdapter())).refill(mMessage);
					// }
				}
			}
		});
	}

	private void addMessageToDatabase(List<ParseObject> mMessage) {
		for (ParseObject message : mMessage) {
			String objectId = message.getObjectId();
			ParseFile file = message.getParseFile(ParseConstant.KEY_FILE);
			Uri uri = Uri.parse(file.getUrl());
			String fileType = message.getString(ParseConstant.KEY_FILE_TYPE);
			String senderName = message
					.getString(ParseConstant.KEY_SENDER_NAME);
			long createdAt = message.getCreatedAt().getTime();
			Message newMessage = new Message(objectId, uri, fileType,
					senderName, createdAt);
			databaseHandler.addMessage(newMessage);
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		// Cursor c = ((CursorAdapter)l.getAdapter()).getCursor();
		// c.moveToPosition(position);
		messageID = id;
		Cursor c = databaseHandler.getOneMessage(id);
		c.moveToFirst();
		int objectIdColumn = c.getColumnIndex("objectId");
		String messageObjectId = c.getString(objectIdColumn);
//		System.out.println("messageObjectId" + messageObjectId);
		int fileTypeColumn = c.getColumnIndex("fileType");
		int uriColumn = c.getColumnIndex("fileUri");
		String fileType = c.getString(fileTypeColumn);
		// ParseFile file = message.getParseFile(ParseConstant.KEY_FILE);
		// Uri fileUri = Uri.parse(file.getUrl());
		Uri URI = Uri.parse(c.getString(uriColumn));
		c.close();
		// start service
		startDeleteService(id, messageObjectId);

		if (fileType.equals(ParseConstant.KEY_FILE_TYPE_VIDEO)) {
			Intent playVideoIntent = new Intent(Intent.ACTION_VIEW, URI);
			playVideoIntent.setDataAndType(URI, "video/*");
			startActivity(playVideoIntent);
		} else {
			Intent viewImageIntent = new Intent(getActivity(),
					ViewImageActivity.class);
			viewImageIntent.setData(URI);
			startActivityForResult(viewImageIntent, REQUEST_CODE_VIEW_IMAGE);
		}

		// databaseHandler.deleteMessage(messageID);
		// requery();
		//
		// deleteParseMessage(messageObjectId);

		// deleteMessage(message);
	}

	private void startDeleteService(long id, String parseObjectId) {
		Intent startServiceIntent = new Intent(getActivity(),
				MessageService.class);
		startServiceIntent.putExtra("receiver", serviceReceiver);
		startServiceIntent.putExtra("type", "delete");
		startServiceIntent.putExtra("_id", id);
		startServiceIntent.putExtra("objectId", parseObjectId);
		getActivity().startService(startServiceIntent);
	}

	private void retrieveMessageService() {
		Intent startServiceIntent = new Intent(getActivity(),
				MessageService.class);
		startServiceIntent.putExtra("receiver", serviceReceiver);
		startServiceIntent.putExtra("type", "retrieveMessage");
		getActivity().startService(startServiceIntent);
		System.out.println("start retrieveMessageService");
	}

	private void deleteMessage(ParseObject message) {

		List<String> ids = message.getList(ParseConstant.KEY_RECEIVE_ID);
		if (ids.size() == 1) {
			message.deleteInBackground();
		} else {
			ArrayList<String> idToRemove = new ArrayList<String>();
			idToRemove.add(ParseUser.getCurrentUser().getObjectId());
			message.removeAll(ParseConstant.KEY_RECEIVE_ID, idToRemove);
			message.saveInBackground();
		}
	}

	private OnRefreshListener refreshlistener = new OnRefreshListener() {
		@Override
		public void onRefresh() {
			retrieveMessageService();
			// retrieveMessages();
		}
	};

	// @Override
	// public void onActivityResult(int requestCode, int resultCode, Intent
	// data) {
	// super.onActivityResult(requestCode, resultCode, data);
	// System.out.println("requestCode" + resultCode);
	// System.out.println("resultCode" + resultCode);
	// if (resultCode == Activity.RESULT_OK && requestCode ==
	// REQUEST_CODE_VIEW_IMAGE) {
	// databaseHandler.deleteMessage(messageID);
	// requery();
	// System.out.println("deleteMessage" + messageID);
	// ParseQuery<ParseObject> query = new
	// ParseQuery<ParseObject>(ParseConstant.CLASS_MESSAGE);
	// query.whereEqualTo(ParseConstant.KEY_OBJECT_ID, messageObjectId);
	// query.findInBackground(new FindCallback<ParseObject>() {
	// @Override
	// public void done(List<ParseObject> arg0, ParseException e) {
	// if (e == null) {
	// if (!arg0.isEmpty()) {
	// ParseObject deleteMessage = arg0.get(0);
	// deleteMessage(deleteMessage);
	// }
	// }
	// }
	// });
	// }
	// };

	private void requery() {
		Cursor cursor = databaseHandler.getAllMessage();
		if (cursor.moveToFirst()) {
			messageAdapter.changeCursor(cursor);
		}
		// printCursor(cursor);
	}

	private void deleteParseMessage(String id) {
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
				ParseConstant.CLASS_MESSAGE);
		query.whereEqualTo(ParseConstant.KEY_OBJECT_ID, id);
		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> arg0, ParseException e) {
				if (e == null) {
					if (!arg0.isEmpty()) {
						ParseObject deleteMessage = arg0.get(0);
						deleteMessage(deleteMessage);
					}
				}
			}
		});
	}

	@Override
	public void onStop() {
		super.onStop();
		System.out.println("call onStop");
		databaseHandler.close();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		System.out.println("call onDestroy");
	}

	public void setupServiceReceiver() {
		serviceReceiver = new ServiceReceiver(new Handler());
		// This is where we specify what happens when data is received from the
		// service
		serviceReceiver.setReceiver(new ServiceReceiver.Receiver() {
			@Override
			public void onReceiveResult(int resultCode, Bundle resultData) {
				if (resultCode == Activity.RESULT_OK) {
					requery();
					if (listview.isRefreshing()) {
						listview.onRefreshComplete();
					}
					getActivity().setProgressBarIndeterminateVisibility(false);
				}
			}
		});
	}

	private Date getLastUpdatedDate() {
		Cursor cursor = databaseHandler.getAllMessage();
		cursor.moveToFirst();
		int index = cursor.getColumnIndex("createdAt");
		long createdAt = cursor.getLong(index);
		Date time = new Date(createdAt);
		System.out.println("requery last time: " + time);
		return time;
	}

	private void printCursor(Cursor c) {
		if (c.moveToFirst()) {
			c.moveToFirst();
			while (!c.isAfterLast()) {
				System.out.print(String.format("%1$-6d", c.getLong(0)));
				System.out.print(String.format("%1$-8s", c.getString(1)));
				System.out.print(String.format("%1$-8s", c.getString(2)));
				String uri = c.getString(3);
				String shorturi = uri.substring(0, 6);
				System.out.print(String.format("%1$-8s", shorturi));
				System.out.print(String.format("%1$-8s", c.getString(4)));
				System.out.print(String.format("%1$-8s", c.getString(5)));
				System.out.print(String.format("%1$-6d", c.getLong(6)));
				System.out.println();
				c.moveToNext();
			}
		}
	}

}
