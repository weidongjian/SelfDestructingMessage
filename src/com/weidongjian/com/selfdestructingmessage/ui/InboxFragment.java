package com.weidongjian.com.selfdestructingmessage.ui;

import java.util.List;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.weidongjian.com.selfdestructingmessage.ParseConstant;
import com.weidongjian.com.selfdestructingmessage.R;
import com.weidongjian.com.selfdestructingmessage.R.layout;
import com.weidongjian.com.selfdestructingmessage.adapter.MessageAdapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
		
		retrieveMessages();
	}

	private void retrieveMessages() {
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstant.CLASS_MESSAGE);
		query.whereEqualTo(ParseConstant.KEY_RECEIVE_ID, ParseUser.getCurrentUser().getObjectId());
		query.orderByDescending(ParseConstant.KEY_CREATED_AT);
		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> messages, ParseException e) {
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

}
