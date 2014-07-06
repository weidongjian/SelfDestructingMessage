package com.weidongjian.com.selfdestructingmessage.ui;

import java.util.ArrayList;
import java.util.List;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.weidongjian.com.selfdestructingmessage.FileHelper;
import com.weidongjian.com.selfdestructingmessage.ImageResizer;
import com.weidongjian.com.selfdestructingmessage.ParseConstant;
import com.weidongjian.com.selfdestructingmessage.R;
import com.weidongjian.com.selfdestructingmessage.adapter.userAdapter;

import android.R.array;
import android.app.Activity;
import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class RecipientActivity extends Activity{
	
	public static final String TAG = RecipientActivity.class.getSimpleName();

	protected GridView mGridview;
	protected List<ParseUser> mUsers;
	protected ParseRelation<ParseUser> mParseRelation;
	protected TextView emptyTextView;
	protected ParseUser mCurrentUser;
	protected MenuItem sendMenuItem;
	protected Uri mediaUri;
	protected String fileType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.gv_user);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setTitle("Select recipients");
		
		mediaUri = getIntent().getData();
		fileType = getIntent().getStringExtra(ParseConstant.KEY_FILE_TYPE);

		mGridview = (GridView) findViewById(R.id.gv_friends);
		emptyTextView = (TextView) findViewById(android.R.id.empty);
		mGridview.setEmptyView(emptyTextView);
		mGridview.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
		mGridview.setOnItemClickListener(mItemClickListener);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		setProgressBarIndeterminateVisibility(true);
		mCurrentUser = ParseUser.getCurrentUser();
		mParseRelation = mCurrentUser.getRelation(ParseConstant.KEY_FRIEND_RELATION);
		ParseQuery<ParseUser> query = mParseRelation.getQuery();
		query.setLimit(100);
		query.orderByAscending(ParseConstant.KEY_USERNAME);
		query.findInBackground(new FindCallback<ParseUser>() {
			@Override
			public void done(List<ParseUser> users, ParseException e) {
				setProgressBarIndeterminateVisibility(false);
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
				else {
					Log.e(TAG, e.getMessage());
				}
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.recipient, menu);
		sendMenuItem = menu.getItem(0);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_send) {
			setProgressBarIndeterminateVisibility(true);
			ParseObject message = createMessage();
			if (message == null) {
				Toast.makeText(this, "can not create message.", Toast.LENGTH_LONG).show();
			}
			else {
				sendMessage(message);
				finish();
			}
			return true;
		}
		else {
			if (id == android.R.id.home) {
				NavUtils.navigateUpFromSameTask(this);
		        return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void sendMessage(ParseObject message) {
		message.saveInBackground(new SaveCallback() {
			@Override
			public void done(ParseException e) {
				setProgressBarIndeterminateVisibility(false);
				if (e == null) {
					Toast.makeText(RecipientActivity.this, "success save message.", Toast.LENGTH_LONG).show();
				}
				else {
					new AlertDialog.Builder(RecipientActivity.this).setTitle("error")
					.setMessage(e.getMessage())
					.setInverseBackgroundForced(true)
					.setPositiveButton(android.R.string.ok, null)
					.show();
				}
			}
		});
	}

	private ParseObject createMessage() {
		ParseObject message = new ParseObject(ParseConstant.CLASS_MESSAGE);
		message.put(ParseConstant.KEY_SENDER_ID, mCurrentUser.getObjectId());
		message.put(ParseConstant.KEY_SENDER_NAME, mCurrentUser.getUsername());
		message.put(ParseConstant.KEY_RECEIVE_ID, getRecipientIDs());
		message.put(ParseConstant.KEY_FILE_TYPE, fileType);
		
		byte[] fileByte = FileHelper.getByteArrayFromFile(this, mediaUri);
		
		if (fileByte == null) {
			return null;
		}
		else {
			if (fileType.equals(ParseConstant.KEY_FILE_TYPE_PHOTO)) {
				fileByte = FileHelper.reduceImageForUpload(fileByte);
			}
		}
		
		String fileName = FileHelper.getFileName(this, mediaUri, fileType);
		ParseFile file = new ParseFile(fileName, fileByte);
		
		message.put(ParseConstant.KEY_FILE, file);
		
		return message;
	}
	
	private ArrayList<String> getRecipientIDs() {
		ArrayList<String> messages = new ArrayList<String>();
		for (int i = 0; i < mGridview.getCount(); i++) {
			if (mGridview.isItemChecked(i)) {
				messages.add(mUsers.get(i).getObjectId());
			}
		}
		return messages;
	}

	private OnItemClickListener mItemClickListener = new OnItemClickListener() {
		public void onItemClick(android.widget.AdapterView<?> parent, View view, int position, long id) {
			ImageView checkImageView = (ImageView) view.findViewById(R.id.iv_check_grid);
			
			if (mGridview.isItemChecked(position)) {
				checkImageView.setVisibility(view.VISIBLE);
			}
			else {
				checkImageView.setVisibility(view.INVISIBLE);
			}
			
			if (mGridview.getCheckedItemCount() > 0) {
				sendMenuItem.setVisible(true);
			}
			else {
				sendMenuItem.setVisible(false);
			}
		};
	};

}
