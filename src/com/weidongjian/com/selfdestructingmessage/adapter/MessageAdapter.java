package com.weidongjian.com.selfdestructingmessage.adapter;

import java.util.Date;
import java.util.List;

import com.parse.ParseObject;
import com.parse.ParseUser;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class MessageAdapter extends ArrayAdapter<ParseObject> {
	protected Context mContext;
	List<ParseObject> mMessage;

	public MessageAdapter(Context context, int resource, List<ParseObject> messages) {
		super(context, resource, messages);
		mContext = context;
		mMessage = messages;
	}
	
	private static class ViewHolder {
		String senderName;
		String sendDate;
		ImageView imageView;
	}
}
