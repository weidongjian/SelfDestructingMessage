package com.weidongjian.com.selfdestructingmessage.ui;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class RecipientActivity extends Activity{
	
	public static final String TAG = RecipientActivity.class.getSimpleName();

	protected GridView mGridview;
	protected List<ParseUser> mUsers;
	protected ParseRelation<ParseUser> mParseRelation;
	protected TextView emptyTextView;
	protected ParseUser mCurrentUser;
	protected MenuItem sendMenuItem;

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
		ParseQuery<ParseUser> query = mParseRelation.getQuery();
		query.setLimit(100);
		query.orderByAscending(ParseConstant.KEY_USERNAME);
		query.findInBackground(new FindCallback<ParseUser>() {
			@Override
			public void done(List<ParseUser> users, ParseException e) {
				if (e == null) {
					mUsers = users;
					if (mGridview.getAdapter() == null) {
						userAdapter adapter = new userAdapter(RecipientActivity.this, mUsers);
						mGridview.setAdapter(adapter);
					}
					else {
						((userAdapter)(mGridview.getAdapter())).refill(mUsers);
					}
				}
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.recipient, menu);
		sendMenuItem = menu.findItem(R.id.action_send);
		sendMenuItem.setVisible(false);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_send) {
			
		}
		return super.onOptionsItemSelected(item);
	}
	
	private OnItemClickListener mItemClickListener = new OnItemClickListener() {
		public void onItemClick(android.widget.AdapterView<?> parent, View view, int position, long id) {
			if (mGridview.getCheckedItemCount() > 0) {
				sendMenuItem.setVisible(true);
			}
			else {
				sendMenuItem.setVisible(false);
			}
		};
	};

}
