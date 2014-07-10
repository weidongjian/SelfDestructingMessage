package com.weidongjian.com.selfdestructingmessage.adapter;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.zip.Inflater;

import com.parse.ParseObject;
import com.parse.ParseUser;
import com.weidongjian.com.selfdestructingmessage.ParseConstant;
import com.weidongjian.com.selfdestructingmessage.R;

import android.content.Context;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MessageAdapter extends ArrayAdapter<ParseObject> {
	protected Context mContext;
	protected List<ParseObject> mMessage;

	public MessageAdapter(Context context, List<ParseObject> messages) {
		super(context, R.layout.message_item, messages);
		mContext = context;
		mMessage = messages;
	}
	
	static class ViewHolder {
		public TextView senderName;
		public TextView sendDate;
		public ImageView imageView;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		ParseObject message = mMessage.get(position);
		ViewHolder holder;
		
		if (rowView == null) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			rowView = inflater.inflate(R.layout.message_item, null);
			
			holder = new ViewHolder();
			holder.imageView = (ImageView) rowView.findViewById(R.id.iv_file_type);
			holder.sendDate = (TextView) rowView.findViewById(R.id.tv_created_at);
			holder.senderName = (TextView) rowView.findViewById(R.id.tv_sender_name);
			rowView.setTag(holder);
		}
		else {
			holder = (ViewHolder) rowView.getTag();
		}
		
		String fileType = message.getString(ParseConstant.KEY_FILE_TYPE);
		if (fileType.equals(ParseConstant.KEY_FILE_TYPE_PHOTO)) {
			holder.imageView.setImageResource(R.drawable.ic_content_picture);
		}
		else {
			holder.imageView.setImageResource(R.drawable.ic_av_play);
		}
		
		Format df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long now = new Date().getTime();
//		String date = df.format(message.getCreatedAt());
		String date = DateUtils.getRelativeTimeSpanString(
				message.getCreatedAt().getTime(), now, DateUtils.SECOND_IN_MILLIS).toString();
		holder.sendDate.setText(date);
		holder.senderName.setText(message.getString(ParseConstant.KEY_SENDER_NAME));
		
		return rowView;
	}
	
	public void refill(List<ParseObject> messages) {
		mMessage.clear();
		mMessage.addAll(messages);
		notifyDataSetChanged();
	}
}













