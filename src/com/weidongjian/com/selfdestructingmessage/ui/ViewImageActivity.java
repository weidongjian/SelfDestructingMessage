package com.weidongjian.com.selfdestructingmessage.ui;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.weidongjian.com.selfdestructingmessage.R;

public class ViewImageActivity extends Activity {
	protected ImageView mImageView;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_view_image);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		Uri fileUri = getIntent().getData();
		mImageView = (ImageView) findViewById(R.id.iv_view_image);
		setProgressBarIndeterminateVisibility(true);
		Picasso.with(this).load(fileUri).into(mImageView, new Callback() {
			@Override
			public void onSuccess() {
				setProgressBarIndeterminateVisibility(false);
			}
			
			@Override
			public void onError() {
				
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.navigate_up_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
	        return true; 
		}
		return super.onOptionsItemSelected(item);
	}
}
