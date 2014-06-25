package com.weidongjian.com.selfdestructingmessage.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.parse.ParseUser;
import com.weidongjian.com.selfdestructingmessage.ParseConstant;
import com.weidongjian.com.selfdestructingmessage.R;
import com.weidongjian.com.selfdestructingmessage.R.array;
import com.weidongjian.com.selfdestructingmessage.R.id;
import com.weidongjian.com.selfdestructingmessage.R.layout;
import com.weidongjian.com.selfdestructingmessage.R.menu;
import com.weidongjian.com.selfdestructingmessage.R.string;
import com.weidongjian.com.selfdestructingmessage.adapter.SectionsPagerAdapter;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v13.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	protected Uri mediaUri;
	public static final String TAG = MainActivity.class.getSimpleName();
	public static final int REQUEST_CODE_TAKE_PICTURE = 0;
	public static final int REQUEST_CODE_CHOOSE_PICTURE = 1;
	public static final int REQUEST_CODE_TAKE_VIDEO = 2;
	public static final int REQUEST_CODE_CHOOSE_VIDEO = 3;
	public static final int MEDIA_TYPE_IMAGE = 4;
	public static final int MEDIA_TYPE_VIDEO = 5;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ParseUser currentUser = ParseUser.getCurrentUser();

		try {
			Uri test = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (currentUser == null) {
			navigateToLogin();
		}

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(this,
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	private void navigateToLogin() {
		Intent intent = new Intent(MainActivity.this, LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
		case R.id.action_logout:
			ParseUser.logOut();
			navigateToLogin();
			break;
		case R.id.action_choice:
			new AlertDialog.Builder(MainActivity.this).setTitle("choose one")
					.setItems(R.array.action_choice, mListener)
					.setInverseBackgroundForced(true)
					.setPositiveButton(android.R.string.ok, null).show();
			break;
		case R.id.action_edit_friends:
			Intent editFriendsIntent = new Intent(MainActivity.this,
					EditFriends.class);
			startActivity(editFriendsIntent);
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	DialogInterface.OnClickListener mListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int position) {
			switch (position) {
			case 0:
				// take picture
				Intent takePictureIntent = new Intent(
						MediaStore.ACTION_IMAGE_CAPTURE);
				mediaUri = null;
				try {
					mediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
				} catch (IOException e) {
					Log.e(TAG, e.getMessage());
				}
				if (mediaUri != null) {
					takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
							mediaUri);
					startActivityForResult(takePictureIntent,
							REQUEST_CODE_TAKE_PICTURE);
					galleryAddPic(mediaUri);
				}
				break;
			case 1:
				// choose picture
				Intent pickPhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
				pickPhotoIntent.setType("image/*");
				startActivityForResult(pickPhotoIntent,
						REQUEST_CODE_CHOOSE_PICTURE);
				break;
			case 2:
				// take video
				Intent takeVideoIntent = new Intent(
						MediaStore.ACTION_VIDEO_CAPTURE);
				try {
					mediaUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
				} catch (IOException e) {
					e.printStackTrace();
				}
				takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mediaUri);
				takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 5);
				takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
				startActivityForResult(takeVideoIntent, REQUEST_CODE_TAKE_VIDEO);
				galleryAddPic(mediaUri);
				break;
			case 3:
				// choose video
				Intent pickVideoIntent = new Intent(Intent.ACTION_GET_CONTENT);
				pickVideoIntent.setType("video/*");
				Toast.makeText(MainActivity.this,
						"The size of the video should be less than 10M.",
						Toast.LENGTH_LONG).show();
				startActivityForResult(pickVideoIntent,
						REQUEST_CODE_CHOOSE_VIDEO);
				break;
			}

		}
	};

	protected void onActivityResult(int requestCode, int resultCode,
			Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		String fileType;
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_CODE_CHOOSE_PICTURE || requestCode == REQUEST_CODE_CHOOSE_VIDEO) {
				if (data == null) {
					Toast.makeText(MainActivity.this, "Gengral error", Toast.LENGTH_LONG).show();
				}
				else {
					mediaUri = data.getData(); 
				}
				
				fileType = ParseConstant.KEY_FILE_TYPE_PHOTO;
				if (requestCode == REQUEST_CODE_CHOOSE_VIDEO) {
					int fileSize = 0;
					InputStream inputStream = null;
					
					try {
						inputStream = getContentResolver().openInputStream(mediaUri);
						fileSize = inputStream.available();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
					finally {
						try {
							inputStream.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					if (fileSize > 1024*1024*10) {
						Toast.makeText(this, "content too large", Toast.LENGTH_LONG).show();
						return;
					}
				}
			}
			
			Intent recipientIntent = new Intent(MainActivity.this, RecipientActivity.class);
			recipientIntent.setData(mediaUri);
			
			if (requestCode == REQUEST_CODE_CHOOSE_PICTURE || requestCode == REQUEST_CODE_TAKE_PICTURE) {
				fileType = ParseConstant.KEY_FILE_TYPE_PHOTO;
			}
			else {
				fileType = ParseConstant.KEY_FILE_TYPE_VIDEO;
			}
			
			recipientIntent.putExtra(ParseConstant.KEY_FILE_TYPE, fileType);
			startActivity(recipientIntent);
		}
	}

	private Uri getOutputMediaFileUri(int mediaType) throws IOException {

		if (isExternalStorageAvaliable()) {
			String appName = MainActivity.this.getString(R.string.app_name);
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
					.format(new Date());

			File storageDir = new File(
					Environment
							.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
					appName);

			if (!storageDir.exists()) {
				if (!storageDir.mkdirs()) {
					Log.e(TAG, "Fail to make external storage dir.");
				}
			}

			String path = storageDir.getPath() + File.separator;
			File mediaFile = null;

			if (mediaType == MEDIA_TYPE_IMAGE) {
				mediaFile = new File(path + "IMG_" + timeStamp + ".jpg");
			} else if (mediaType == MEDIA_TYPE_VIDEO) {
				mediaFile = new File(path + "VID_" + timeStamp + ".mp4");
			}

			Log.d(TAG, "File: " + Uri.fromFile(mediaFile));

			return Uri.fromFile(mediaFile);
		}

		return null;
	}

	private boolean isExternalStorageAvaliable() {
		String stage = Environment.getExternalStorageState();
		if (stage.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else
			return false;
	}

	private void galleryAddPic(Uri contentUri) {
		Intent mediaScanIntent = new Intent(
				Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		mediaScanIntent.setData(contentUri);
		this.sendBroadcast(mediaScanIntent);
	}

}
