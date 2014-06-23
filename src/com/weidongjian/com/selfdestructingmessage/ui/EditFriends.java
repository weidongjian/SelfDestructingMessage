package com.weidongjian.com.selfdestructingmessage.ui;

import java.util.List;

import com.parse.ParseUser;
import com.weidongjian.com.selfdestructingmessage.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.TextView;

public class EditFriends extends Activity {
	
	private GridView mGridview;
	private List<ParseUser> users;
	private TextView emptyTextView;
	private ParseUser mCurrentUser;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gv_user);
		
		mGridview = (GridView) findViewById(R.id.gv_friends);
		emptyTextView = (TextView) findViewById(android.R.id.empty);
		
		mGridview.setEmptyView(emptyTextView);
	}

}
