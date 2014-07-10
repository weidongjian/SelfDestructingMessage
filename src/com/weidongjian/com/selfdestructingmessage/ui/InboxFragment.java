package com.weidongjian.com.selfdestructingmessage.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.weidongjian.com.selfdestructingmessage.ParseConstant;
import com.weidongjian.com.selfdestructingmessage.R;
import com.weidongjian.com.selfdestructingmessage.adapter.MessageAdapter;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;


public class InboxFragment extends ListFragment {
	public static final int REQUEST_CODE_VIEW_IMAGE = 0;
	protected List<ParseObject> mMessage;
	protected PullToRefreshListView listview;
	protected int location;
//	protected MenuItem progressBar = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setHasOptionsMenu(true);
	}
	
//	@Override
//	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//		inflater.inflate(R.menu.inbox_fragment, menu);
//		if (menu != null) {
//			progressBar = menu.findItem(R.id.progress_bar);
//			progressBar.setActionView(R.layout.indeterminate_progress_bar);
//		}
//		super.onCreateOptionsMenu(menu, inflater);
//	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);
		listview = (PullToRefreshListView) rootView.findViewById(android.R.id.list);
		listview.setOnRefreshListener(refreshlistener);
		return rootView;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		getActivity().setProgressBarIndeterminateVisibility(true);
		retrieveMessages();
	}
	
//	@Override
//	public void onResume() {
//		super.onResume();
//		
//	}

	public void retrieveMessages() {
//		if (progressBar != null) {
//			progressBar.setVisible(true);
//		}
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstant.CLASS_MESSAGE);
		query.whereEqualTo(ParseConstant.KEY_RECEIVE_ID, ParseUser.getCurrentUser().getObjectId());
		query.orderByDescending(ParseConstant.KEY_CREATED_AT);
		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> messages, ParseException e) {
//				if (progressBar != null && progressBar.isVisible()) {
//					progressBar.setVisible(false);
//				}
				getActivity().setProgressBarIndeterminateVisibility(false);
				listview.onRefreshComplete();
				if (e == null) {
					mMessage = messages;
					
//					if (getListView().getAdapter() == null) {
						MessageAdapter adapter = new MessageAdapter(getListView().getContext(), mMessage);
						setListAdapter(adapter);
//					}
//					else {
//						((MessageAdapter)(getListView().getAdapter())).refill(mMessage);
//					}
				}
			}
		});
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		location = position;
		ParseObject message = mMessage.get(position);
		String fileType = message.getString(ParseConstant.KEY_FILE_TYPE);
		ParseFile file = message.getParseFile(ParseConstant.KEY_FILE);
		Uri fileUri = Uri.parse(file.getUrl());
		
		if (fileType.equals(ParseConstant.KEY_FILE_TYPE_VIDEO)) {
			Intent playVideoIntent = new Intent(Intent.ACTION_VIEW, fileUri);
			playVideoIntent.setDataAndType(fileUri, "video/*");
			startActivity(playVideoIntent);
		}
		else {
			Intent viewImageIntent = new Intent(getActivity(), ViewImageActivity.class);
			viewImageIntent.setData(fileUri);
			startActivityForResult(viewImageIntent, 0);
		}
		
//		deleteMessage(message);
	}
	
	private void deleteMessage(ParseObject message) {
		List<String> ids = message.getList(ParseConstant.KEY_RECEIVE_ID);
		if (ids.size() == 1) {
			message.deleteInBackground();
		}
		else {
			ArrayList<String> idToRemove = new ArrayList<String>();
			idToRemove.add(ParseUser.getCurrentUser().getObjectId());
			message.removeAll(ParseConstant.KEY_RECEIVE_ID, idToRemove);
			message.saveInBackground();
		}
	}

	private OnRefreshListener refreshlistener = new OnRefreshListener() {
		@Override
		public void onRefresh() {
			retrieveMessages();
		}
	};
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_VIEW_IMAGE) {
			deleteMessage(mMessage.get(location));
		}
	};
}















