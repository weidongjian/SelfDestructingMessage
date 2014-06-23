package com.weidongjian.com.selfdestructingmessage.ui;

import java.text.ChoiceFormat;
import java.util.List;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.weidongjian.com.selfdestructingmessage.ParseConstant;
import com.weidongjian.com.selfdestructingmessage.R;
import com.weidongjian.com.selfdestructingmessage.adapter.userAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class EditFriends extends Activity {
	public static final String TAG = EditFriends.class.getSimpleName();

	protected GridView mGridview;
	protected List<ParseUser> mUsers;
	protected ParseRelation<ParseUser> mParseRelation;
	protected TextView emptyTextView;
	protected ParseUser mCurrentUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gv_user);

		mGridview = (GridView) findViewById(R.id.gv_friends);
		emptyTextView = (TextView) findViewById(android.R.id.empty);
		mGridview.setEmptyView(emptyTextView);
		mGridview.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
		mGridview.setOnItemClickListener(mItemClickListener);
		mCurrentUser = ParseUser.getCurrentUser();
	}
	
	@Override
	protected void onResume() {
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
						userAdapter adapter = new userAdapter(EditFriends.this, mUsers);
						mGridview.setAdapter(adapter);
					}
					else {
						((userAdapter)(mGridview.getAdapter())).refill(mUsers);
					}
					
				}
			}
		});
		addFriendsCheck();
	}
	


	private void addFriendsCheck() {
		mParseRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
			@Override
			public void done(List<ParseUser> friends, ParseException e) {
				if (e == null) {
					for (int i = 0; i < mUsers.size(); i++) {
						ParseUser user = mUsers.get(i);
						for (ParseUser friend : friends) {
							if (friend.getObjectId().equals(user.getObjectId())) {
								mGridview.setItemChecked(i, true);
							}
						}
					}
				}
			}
		});
	}

	private OnItemClickListener mItemClickListener = new OnItemClickListener() {
		public void onItemClick(android.widget.AdapterView<?> parent, View view,
				int pisition, long id) {
			ImageView checkImageView = (ImageView) view.findViewById(R.id.iv_check_grid);
			
			if (mGridview.isItemChecked(pisition)) {
				mParseRelation.add(mUsers.get(pisition));
			}
			else {
				mParseRelation.remove(mUsers.remove(pisition));
			}
			
			mCurrentUser.saveInBackground(new SaveCallback() {
				@Override
				public void done(ParseException e) {
					if (e != null) {
						Log.e(TAG, e.getMessage());
					}
				}
			});
		};
	};
	
	
}
