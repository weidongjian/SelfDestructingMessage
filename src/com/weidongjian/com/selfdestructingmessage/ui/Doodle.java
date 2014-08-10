package com.weidongjian.com.selfdestructingmessage.ui;

import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import com.devadvance.circularseekbar.CircularSeekBar;
import com.devadvance.circularseekbar.CircularSeekBar.OnCircularSeekBarChangeListener;
import com.weidongjian.com.selfdestructingmessage.ParseConstant;
import com.weidongjian.com.selfdestructingmessage.R;

public class Doodle extends Activity {

	private SensorManager sensorManager;
	private float acceleration;
	private float currentAcceleration;
	private float lastAcceleration;
	private boolean doubleBackToExitPressedOnce;
	private AtomicBoolean dialogIsDisplay = new AtomicBoolean();
	private RelativeLayout relativeLayout;

	private static final int ACCELERATION_THRESHOLD = 15000;
	private static final String COLOR = "color";
	private static final String WIDTH = "width";
	private Dialog currentDialog;
	private DoodleView doodleView;
	private SharedPreferences sharedData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_doodle);
		sharedData = getSharedPreferences("data", 0);
		System.out.println("sharedData");
		int color = sharedData.getInt(COLOR, 0);
		System.out.println("color" + color);
		int width = sharedData.getInt(WIDTH, 0);
		System.out.println("width" + width);
		
		
//		relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
//		doodleView = new DoodleView(context, attrs)
		
		doodleView = (DoodleView) findViewById(R.id.doodleView);
		if (color != 0) {
			doodleView.setDrawingColor(color);
		}
		if (width != 0) {
			doodleView.setDrawingWidth(width);
		}
		acceleration = 0.00f;
		currentAcceleration = sensorManager.GRAVITY_EARTH;
		lastAcceleration = sensorManager.GRAVITY_EARTH;

		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		enableAccelerometerListener();
	}

	private void enableAccelerometerListener() {
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		sensorManager.registerListener(sensorEventListener,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				sensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onPause() {
		super.onPause();
		disableAccelerometerListener();
	}

	private void disableAccelerometerListener() {
		if (sensorManager != null) {
			sensorManager.unregisterListener(sensorEventListener);
			sensorManager = null;
		}
	}

	private SensorEventListener sensorEventListener = new SensorEventListener() {
		@Override
		public void onSensorChanged(SensorEvent event) {
			if (!dialogIsDisplay.get()) {
				float x = event.values[0];
				float y = event.values[1];
				float z = event.values[2];

				lastAcceleration = currentAcceleration;
				currentAcceleration = x * x + y * y + z * z;
				acceleration = currentAcceleration
						* (currentAcceleration - lastAcceleration);

				if (acceleration >= ACCELERATION_THRESHOLD) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							Doodle.this);
					builder.setTitle(R.string.message_erase);
					builder.setCancelable(true);
					builder.setPositiveButton(R.string.button_erase,
							new OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									dialogIsDisplay.set(false);
									doodleView.clear();
								}
							});
					builder.setNegativeButton(R.string.button_cancel,
							new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialogIsDisplay.set(false);
									dialog.cancel();
								}
							});
					dialogIsDisplay.set(true);
					builder.show();
				}
			}
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {

		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.doodle_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menuItemColor:
			showColorDialog();
			return true;
		case R.id.menuItemWidth:
			showLineWidthDialog();
			return true;
		case R.id.menuItemErase:
			doodleView.setDrawingColor(Color.WHITE);
			return true;
		case R.id.menuItemClear:
			doodleView.clear();
			return true;
		case R.id.menuItemSave:
			doodleView.sendImage();
			galleryAddPic(doodleView.getUri());
			Intent recipientIntent = new Intent(Doodle.this,
					RecipientActivity.class);
			recipientIntent.putExtra(ParseConstant.KEY_FILE_TYPE, ParseConstant.KEY_FILE_TYPE_PHOTO);
			recipientIntent.setData(doodleView.getUri());
			startActivity(recipientIntent);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void showColorDialog() {
		currentDialog = new Dialog(this);
		currentDialog.setContentView(R.layout.color_dialog);
		currentDialog.setTitle(R.string.title_color_dialog);
		currentDialog.setCancelable(true);
		
		final SeekBar alphaSeekbar = (SeekBar) currentDialog.findViewById(R.id.alphaSeekbar);
		final SeekBar redSeekbar = (SeekBar) currentDialog.findViewById(R.id.redSeekbar);
		final SeekBar greenSeekbar = (SeekBar) currentDialog.findViewById(R.id.greenSeekbar);
		final SeekBar blueSeekBar = (SeekBar) currentDialog.findViewById(R.id.blueSeekbar);
		
		alphaSeekbar.setOnSeekBarChangeListener(colorSeekbarChanged);
		redSeekbar.setOnSeekBarChangeListener(colorSeekbarChanged);
		greenSeekbar.setOnSeekBarChangeListener(colorSeekbarChanged);
		blueSeekBar.setOnSeekBarChangeListener(colorSeekbarChanged);
		
		final int color = doodleView.getDrawingColor();
		alphaSeekbar.setProgress(Color.alpha(color));
		redSeekbar.setProgress(Color.red(color));
		greenSeekbar.setProgress(Color.green(color));
		blueSeekBar.setProgress(Color.blue(color));
		
		Button setColorButton = (Button) currentDialog.findViewById(R.id.setColorButton);
		setColorButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				int drawingColor = Color.argb(alphaSeekbar.getProgress(), redSeekbar.getProgress(),
						greenSeekbar.getProgress(), blueSeekBar.getProgress());
				doodleView.setDrawingColor(drawingColor);
				SharedPreferences.Editor editor = sharedData.edit();
				editor.putInt(COLOR, drawingColor);
				editor.commit();
				dialogIsDisplay.set(false);
				currentDialog.dismiss();
				currentDialog = null;
			}
		});
		
		dialogIsDisplay.set(true);
		currentDialog.show();
	}
	
	private OnSeekBarChangeListener colorSeekbarChanged = new OnSeekBarChangeListener() {
		
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
		}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			final SeekBar alpahSeekbar = (SeekBar) currentDialog.findViewById(R.id.alphaSeekbar);
			final SeekBar redSeekbar = (SeekBar) currentDialog.findViewById(R.id.redSeekbar);
			final SeekBar greenSeekbar = (SeekBar) currentDialog.findViewById(R.id.greenSeekbar);
			final SeekBar blueSeekBar = (SeekBar) currentDialog.findViewById(R.id.blueSeekbar);
			final View colorView = (View) currentDialog.findViewById(R.id.colorView);
			
			colorView.setBackgroundColor(Color.argb(alpahSeekbar.getProgress(), 
					redSeekbar.getProgress(), greenSeekbar.getProgress(), blueSeekBar.getProgress()));
		}
	};
	
	private class CircleSeekBarListener implements OnCircularSeekBarChangeListener {
		private Bitmap bitmap = Bitmap.createBitmap(400, 100, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		ImageView widthImageView = (ImageView) currentDialog.findViewById(R.id.widthImageView);
	    @Override
	    public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
			Paint p = new Paint();
			p.setColor(doodleView.getDrawingColor());
			p.setStrokeCap(Paint.Cap.ROUND);
			p.setStrokeWidth(progress);
			
			bitmap.eraseColor(Color.WHITE);
			canvas.drawLine(30, 50, 370, 50, p);
			widthImageView.setImageBitmap(bitmap);
	    }
		@Override
		public void onStopTrackingTouch(CircularSeekBar seekBar) {
		}

		@Override
		public void onStartTrackingTouch(CircularSeekBar seekBar) {
		}
	}
	
//	private OnSeekBarChangeListener widthSeekbarChanged = new OnSeekBarChangeListener() {
//		private Bitmap bitmap = Bitmap.createBitmap(400, 100, Bitmap.Config.ARGB_8888);
//		Canvas canvas = new Canvas(bitmap);
//		
//		@Override
//		public void onStopTrackingTouch(SeekBar seekBar) {
//		}
//		
//		@Override
//		public void onStartTrackingTouch(SeekBar seekBar) {
//		}
//		
//		@Override
//		public void onProgressChanged(SeekBar seekBar, int progress,
//				boolean fromUser) {
//			ImageView widthImageView = (ImageView) currentDialog.findViewById(R.id.widthImageView);
//			Paint p = new Paint();
//			p.setStrokeCap(Paint.Cap.ROUND);
//			p.setStrokeWidth(progress);
//			
//			bitmap.eraseColor(Color.WHITE);
//			canvas.drawLine(30, 50, 370, 50, p);
//			widthImageView.setImageBitmap(bitmap);
//		}
//	};
	
	private void showLineWidthDialog() {
		currentDialog = new Dialog(this);
		currentDialog.setContentView(R.layout.width_dialog);
		currentDialog.setTitle(R.string.title_line_width_dialog);
		currentDialog.setCancelable(true);
		
		final CircularSeekBar  widthSeekbar = (CircularSeekBar ) currentDialog.findViewById(R.id.widthSeekbar);
		widthSeekbar.setCircleColor(Color.GRAY);
		widthSeekbar.setCircleProgressColor(doodleView.getDrawingColor());
		widthSeekbar.setOnSeekBarChangeListener(new CircleSeekBarListener());
		
		int width = doodleView.getDrawingWidth();
		widthSeekbar.setProgress(width);
		
		Button setLineWidthButton = (Button) currentDialog.findViewById(R.id.widthDialogDoneButton);
		setLineWidthButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int drawingWidth = widthSeekbar.getProgress();
				doodleView.setDrawingWidth(drawingWidth);
				SharedPreferences.Editor editor = sharedData.edit();
				editor.putInt(WIDTH, drawingWidth);
				editor.commit();
				dialogIsDisplay.set(false);
				currentDialog.dismiss();
				currentDialog = null;
			}
		});
		
		dialogIsDisplay.set(true);
		currentDialog.show();
	}
	
	private void galleryAddPic(Uri contentUri) {
		Intent mediaScanIntent = new Intent(
				Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		mediaScanIntent.setData(contentUri);
		this.sendBroadcast(mediaScanIntent);
	}
	
	@Override
	public void onBackPressed() {
		if (doubleBackToExitPressedOnce) {
			super.onBackPressed();
			return;
		}
		
		this.doubleBackToExitPressedOnce = true;
		Toast.makeText(this, "Please click BACK again to exit Doodle ", Toast.LENGTH_SHORT).show();
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				doubleBackToExitPressedOnce = false;
			}
		}, 1000 * 2);
	}
}
