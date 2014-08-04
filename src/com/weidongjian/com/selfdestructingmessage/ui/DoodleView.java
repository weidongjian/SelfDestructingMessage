package com.weidongjian.com.selfdestructingmessage.ui;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.weidongjian.com.selfdestructingmessage.R;

import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore.Images;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class DoodleView extends View {
	
	private static final float TOUCH_TOLERANCE = 10;
	
	private Bitmap bitmap; 
	private Canvas canvas;
	private Paint linePaint;
	private Paint screenPaint;
	private HashMap<Integer, Path> pathMap;
	private HashMap<Integer, Point> previousPointMap;
	private Uri uri;

	public DoodleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		screenPaint = new Paint();
		linePaint = new Paint();
		linePaint.setAntiAlias(true);
		linePaint.setColor(Color.BLACK);
		linePaint.setStyle(Paint.Style.STROKE);
		linePaint.setStrokeWidth(5);
		linePaint.setStrokeCap(Paint.Cap.ROUND);
		pathMap = new HashMap<Integer, Path>();
		previousPointMap = new HashMap<Integer, Point>();
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
		canvas = new Canvas(bitmap);
		bitmap.eraseColor(Color.WHITE);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(bitmap, 0, 0, screenPaint);
		for (Integer key : pathMap.keySet()) {
			canvas.drawPath(pathMap.get(key), linePaint);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getActionMasked();
		int actionIndex = event.getActionIndex();
		
		if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
			touchStarted(event.getX(actionIndex), event.getY(actionIndex), event.getPointerId(actionIndex));
		}
		else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
			touchEnded(event.getPointerId(actionIndex));
		}
		else {
			touchMoved(event);
		}
		invalidate();
		return true;
	}
	
	private void touchStarted(float x, float y, int lineID) {
		Path path;
		Point point;
		
		if (pathMap.containsKey(lineID)) {
			path = pathMap.get(lineID);
			path.reset();
			point = previousPointMap.get(lineID);
		}
		else {
			path = new Path();
			pathMap.put(lineID, path);
			point = new Point();
			previousPointMap.put(lineID, point);
		}
		path.moveTo(x, y);
		point.x = (int) x;
		point.y = (int) y;
	}
	
	private void touchMoved(MotionEvent event) {
		for (int i = 0; i < event.getPointerCount(); i++) {
			int pointerID = event.getPointerId(i);
			int pointerIndex = event.findPointerIndex(pointerID);
			
			if (pathMap.containsKey(pointerID)) {
				float newX = event.getX(pointerIndex);
				float newY = event.getY(pointerIndex);
				Path path = pathMap.get(pointerID);
				Point point = previousPointMap.get(pointerID);
				
				float deltaX = Math.abs(newX - point.x); 
				float deltaY = Math.abs(newY - point.y);
				
				if (deltaX > TOUCH_TOLERANCE || deltaY > TOUCH_TOLERANCE) {
					path.quadTo(newX, newY, point.x, point.y);
					point.x = (int) newX;
					point.y = (int) newY;
				}
			}
		}
	}
	
	private void touchEnded(int touchID) {
		Path path = pathMap.get(touchID);
		canvas.drawPath(path, linePaint);
		path.reset();
	}
	
	public void clear() {
		pathMap.clear();
		previousPointMap.clear();
		bitmap.eraseColor(Color.WHITE);
		invalidate();
	}
	
	public void setDrawingColor(int color) {
		linePaint.setColor(color);
	}
	
	public int getDrawingColor() {
		return linePaint.getColor();
	}
	
	public void setDrawingWidth(int width) {
		linePaint.setStrokeWidth(width);
	}
	
	public int getDrawingWidth() {
		return (int) linePaint.getStrokeWidth();
	}
	
	public Uri getUri() {
		return uri;
	}
	
	public void sendImage() {
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
		.format(new Date());
		String fileNmae = "DoodleView" + timeStamp;
		ContentValues value = new ContentValues();
		value.put(Images.Media.TITLE, fileNmae);
		value.put(Images.Media.DATE_ADDED, System.currentTimeMillis());
		value.put(Images.Media.MIME_TYPE, "image/jpg");
		
		uri = getContext().getContentResolver().insert(Images.Media.EXTERNAL_CONTENT_URI, value);
		
		try {
			OutputStream outPutStream = getContext().getContentResolver().openOutputStream(uri);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outPutStream);
			outPutStream.flush();
			outPutStream.close();
			Toast.makeText(getContext(), R.string.message_saved, Toast.LENGTH_LONG).show();
		} catch (IOException e) {
			Toast.makeText(getContext(), R.string.message_error_saving, Toast.LENGTH_LONG).show();
		}
	}
	
	

}





















