package com.weidongjian.com.selfdestructingmessage.ui;

import java.util.List;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.weidongjian.com.selfdestructingmessage.ParseConstant;
import com.weidongjian.com.selfdestructingmessage.R;
import com.weidongjian.com.selfdestructingmessage.R.layout;
import com.weidongjian.com.selfdestructingmessage.adapter.userAdapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

public class FriendFragment extends Fragment {
	
	protected GridView mGridview;
	protected List<ParseUser> mUsers;
	protected ParseRelation<ParseUser> mParseRelation;
	protected TextView emptyTextView;
	protected ParseUser mCurrentUser;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.gv_user, container, false);
		
		mGridview = (GridView) rootView.findViewById(R.id.gv_friends);
		emptyTextView = (TextView) rootView.findViewById(android.R.id.empty);
		mGridview.setEmptyView(emptyTextView);
		mGridview.setChoiceMode(GridView.CHOICE_MODE_NONE);
		mCurrentUser = ParseUser.getCurrentUser();
		
		return rootView;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mParseRelation = mCurrentUser.getRelation(ParseConstant.KEY_FRIEND_RELATION);
		ParseQuery<ParseUser> query = ParseUser.getQuery();
		query.setLimit(100);
		query.orderByAscending(ParseConstant.KEY_USERNAME);
		query.findInBackground(new FindCallback<ParseUser>() {
			@Override
			public void done(List<ParseUser> users, ParseException e) {
				if (e == null) {
					mUsers = users;
					if (mGridview.getAdapter() == null) {
						userAdapter adapter = new userAdapter(getActivity().getApplicationContext(), mUsers);
						mGridview.setAdapter(adapter);
					}
					else {
						((userAdapter)(mGridview.getAdapter())).refill(mUsers);
					}
				}
			}
		});
	}
	
}
