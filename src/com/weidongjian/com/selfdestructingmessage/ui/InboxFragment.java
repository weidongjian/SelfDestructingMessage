package com.weidongjian.com.selfdestructingmessage.ui;

import java.util.ArrayList;
import java.util.List;

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

public class InboxFragment extends ListFragment {
	
	protected List<ParseObject> mMessage;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);
		return rootView;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		getActivity().setProgressBarIndeterminateVisibility(true);
		retrieveMessages();
	}

	public void retrieveMessages() {
		
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstant.CLASS_MESSAGE);
		query.whereEqualTo(ParseConstant.KEY_RECEIVE_ID, ParseUser.getCurrentUser().getObjectId());
		query.orderByDescending(ParseConstant.KEY_CREATED_AT);
		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> messages, ParseException e) {
				getActivity().setProgressBarIndeterminateVisibility(false);
				if (e == null) {
					mMessage = messages;
					
					if (getListView().getAdapter() == null) {
						MessageAdapter adapter = new MessageAdapter(getListView().getContext(), mMessage);
						setListAdapter(adapter);
					}
					else {
						((MessageAdapter)(getListView().getAdapter())).refill(mMessage);
					}
				}
			}
		});
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
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
			startActivity(viewImageIntent);
		}
	}
	
	private void deleteMessage(ParseObject message) {
		List<String> ids = message.getList(ParseConstant.KEY_RECEIVE_ID);
		if (ids.size() == 1) {
			message.deleteInBackground();
		}
		else {
			ArrayList<String> idToRemove = new ArrayList<>();
			idToRemove.add(ParseUser.getCurrentUser().getObjectId());
			message.removeAll(ParseConstant.KEY_RECEIVE_ID, idToRemove);
			message.saveInBackground();
		}
	}

}















