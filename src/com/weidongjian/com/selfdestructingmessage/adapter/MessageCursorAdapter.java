package com.weidongjian.com.selfdestructingmessage.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.weidongjian.com.selfdestructingmessage.ParseConstant;
import com.weidongjian.com.selfdestructingmessage.R;

import android.content.Context;
import android.database.Cursor;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MessageCursorAdapter extends CursorAdapter{
	private LayoutInflater inflater;
	private Context context;
	private Cursor cursor;
	SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	public MessageCursorAdapter(Context context, Cursor c) {
		super(context, c, 0);
		this.context = context;
		cursor = c;
		inflater = LayoutInflater.from(context);
	}


	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		int senderColumn = cursor.getColumnIndex("senderName");
		int fileTypeColumn = cursor.getColumnIndex("fileType");
		int createdColumn = cursor.getColumnIndex("createdAt");
		String senderName = cursor.getString(senderColumn);
		String fileType = cursor.getString(fileTypeColumn);
		long createdDate = cursor.getLong(createdColumn);
		
		TextView senderTextView = (TextView) view.findViewById(R.id.tv_sender_name);
		TextView createdDateTextView = (TextView) view.findViewById(R.id.tv_created_at);
		ImageView fileTypeImageView = (ImageView) view.findViewById(R.id.iv_file_type);
		
		senderTextView.setText(senderName);
		
		if (fileType.equals(ParseConstant.KEY_FILE_TYPE_PHOTO)) {
			fileTypeImageView.setImageResource(R.drawable.ic_content_picture);
		}
		else {
			fileTypeImageView.setImageResource(R.drawable.ic_av_play);
		}
		
		long now = new Date().getTime();
		String date = DateUtils.getRelativeTimeSpanString(
				createdDate, now, DateUtils.SECOND_IN_MILLIS).toString();
		createdDateTextView.setText(date);
	}


	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View v = inflater.inflate(R.layout.message_item, parent, false);
		return v;
	}

}
