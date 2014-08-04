package com.weidongjian.com.selfdestructingmessage.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.parse.ParseUser;
import com.weidongjian.com.selfdestructingmessage.ParseConstant;
import com.weidongjian.com.selfdestructingmessage.R;
import com.weidongjian.com.selfdestructingmessage.adapter.SectionsPagerAdapter;

public class MainActivity extends FragmentActivity  {

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
	protected Menu optionMenu;
	protected Uri mediaUri;
	public static final String TAG = MainActivity.class.getSimpleName();
	public static final int REQUEST_CODE_TAKE_PICTURE = 0;
	public static final int REQUEST_CODE_CHOOSE_PICTURE = 1;
	public static final int REQUEST_CODE_TAKE_VIDEO = 2;
	public static final int REQUEST_CODE_CHOOSE_VIDEO = 3;
	public static final int MEDIA_TYPE_IMAGE = 4;
	public static final int MEDIA_TYPE_VIDEO = 5;
	public static final int REQUEST_CODE_DOODLE = 6;
	private PagerSlidingTabStrip tabs;
	private DisplayMetrics dm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser == null) {
			navigateToLogin();
		}
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_main);

		try {
			Uri test = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
//		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(this,
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		
//		PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);  
//		final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()  
//                .getDisplayMetrics());  
//        mViewPager.setPageMargin(pageMargin);  
//  
//        tabs.setViewPager(mViewPager);  
//		PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
//		 tabs.setViewPager(mViewPager);
  

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
//		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
//			// Create a tab with text corresponding to the page title defined by
//			// the adapter. Also specify this Activity object, which implements
//			// the TabListener interface, as the callback (listener) for when
//			// this tab is selected.
//			actionBar.addTab(actionBar.newTab()
//					.setText(mSectionsPagerAdapter.getPageTitle(i))
//					.setTabListener(this));
//		}
		
		dm = getResources().getDisplayMetrics();
		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		tabs.setViewPager(mViewPager);
		setTabsValue();
	}
	
	private void setTabsValue() {
		// 设置Tab是自动填充满屏幕的
		tabs.setShouldExpand(true);
		// 设置Tab的分割线是透明的
		tabs.setDividerColor(Color.TRANSPARENT);
		// 设置Tab底部线的高度
		tabs.setUnderlineHeight((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 0, dm));
		// 设置Tab Indicator的高度
		tabs.setIndicatorHeight((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 3, dm));
		// 设置Tab标题文字的大小
		tabs.setTextSize((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_SP, 16, dm));
		// 设置Tab Indicator的颜色
		tabs.setIndicatorColor(Color.parseColor("#45c01a"));
		// 设置选中Tab文字的颜色 (这是我自定义的一个方法)
		tabs.setSelectedTextColor(Color.parseColor("#45c01a"));
		// 取消点击Tab时的背景色
		tabs.setTabBackground(0);
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
		this.optionMenu = menu;
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
			new AlertDialog.Builder(MainActivity.this)
					.setTitle("Create message")
					.setItems(R.array.action_choice, mListener)
					.setInverseBackgroundForced(true)
					.setNegativeButton(android.R.string.cancel, null).show();
			break;
		case R.id.action_edit_friends:
			Intent editFriendsIntent = new Intent(MainActivity.this,
					EditFriends.class);
			startActivity(editFriendsIntent);
			break;
		}

		return super.onOptionsItemSelected(item);
	}

//	@Override
//	public void onTabSelected(ActionBar.Tab tab,
//			FragmentTransaction fragmentTransaction) {
//		// When the given tab is selected, switch to the corresponding page in
//		// the ViewPager.
//		mViewPager.setCurrentItem(tab.getPosition());
//	}

//	@Override
//	public void onTabUnselected(ActionBar.Tab tab,
//			FragmentTransaction fragmentTransaction) {
//	}
//
//	@Override
//	public void onTabReselected(ActionBar.Tab tab,
//			FragmentTransaction fragmentTransaction) {
//	}

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
			case 4:
				//run doodle activity
				Intent doodleIntent = new Intent(MainActivity.this, Doodle.class);
				startActivityForResult(doodleIntent, REQUEST_CODE_DOODLE);
				break;
			}

		}
	};

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		String fileType;
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_CODE_CHOOSE_PICTURE
					|| requestCode == REQUEST_CODE_CHOOSE_VIDEO) {
				if (data == null) {
					Toast.makeText(MainActivity.this, "Gengral error",
							Toast.LENGTH_LONG).show();
				} else {
					mediaUri = data.getData();
				}

				fileType = ParseConstant.KEY_FILE_TYPE_PHOTO;
				if (requestCode == REQUEST_CODE_CHOOSE_VIDEO) {
					int fileSize = 0;
					InputStream inputStream = null;

					try {
						inputStream = getContentResolver().openInputStream(
								mediaUri);
						fileSize = inputStream.available();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						try {
							inputStream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

					if (fileSize > 1024 * 1024 * 10) {
						Toast.makeText(this, "content too large",
								Toast.LENGTH_LONG).show();
						return;
					}
				}
			}

			Intent recipientIntent = new Intent(MainActivity.this,
					RecipientActivity.class);
			recipientIntent.setData(mediaUri);

			if (requestCode == REQUEST_CODE_CHOOSE_PICTURE
					|| requestCode == REQUEST_CODE_TAKE_PICTURE) {
				fileType = ParseConstant.KEY_FILE_TYPE_PHOTO;
			} else {
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
