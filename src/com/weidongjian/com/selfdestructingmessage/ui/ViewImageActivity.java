package com.weidongjian.com.selfdestructingmessage.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.weidongjian.com.selfdestructingmessage.R;

public class ViewImageActivity extends Activity {
	protected ImageView mImageView;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	protected TextView mRemainText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_view_image);
		setTitle("View Image");
		getActionBar().setDisplayHomeAsUpEnabled(true);
		Uri fileUri = getIntent().getData();
		mImageView = (ImageView) findViewById(R.id.iv_view_image);
		mRemainText = (TextView) findViewById(R.id.tv_time_conuntDown);
		setProgressBarIndeterminateVisibility(true);
		Picasso.with(this).load(fileUri).into(mImageView, new Callback() {
			@Override
			public void onSuccess() {
				setProgressBarIndeterminateVisibility(false);
				getActionBar().hide();
				setResult(RESULT_OK);
				mRemainText.setVisibility(View.VISIBLE);
				
				new CountDownTimer(10000, 1000) {
					@Override
					public void onTick(long millisUntilFinished) {
						String format = String.format(getResources().getString(R.string.time_countDown), millisUntilFinished/1000);
						mRemainText.setText(format);
					}
					@Override
					public void onFinish() {
						Toast.makeText(ViewImageActivity.this, "The selected message is deleted.", Toast.LENGTH_LONG).show();
						Intent intent = new Intent(ViewImageActivity.this, MainActivity.class);
						startActivity(intent);
						finish();
					}
				}.start();
			}
			
			@Override
			public void onError() {
				
			}
		});
		
		mImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (getActionBar().isShowing()) {
					getActionBar().hide();
				}
				else {
					getActionBar().show();
				}
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
