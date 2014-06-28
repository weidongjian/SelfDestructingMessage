package com.weidongjian.com.selfdestructingmessage.ui;

import com.squareup.picasso.Picasso;
import com.weidongjian.com.selfdestructingmessage.R;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class ViewImageActivity extends Activity {
	protected ImageView mImageView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_image);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		Uri fileUri = getIntent().getData();
		mImageView = (ImageView) findViewById(R.id.iv_view_image);
		
		Picasso.with(this).load(fileUri).into(mImageView);
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
