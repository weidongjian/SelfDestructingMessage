package com.weidongjian.com.selfdestructingmessage.adapter;

import java.util.List;
import java.util.zip.Inflater;

import com.parse.ParseUser;
import com.weidongjian.com.selfdestructingmessage.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class userAdapter extends ArrayAdapter<ParseUser> {
	
	private Context mContext;
	private List<ParseUser> users;
	
	static class ViewHolder {
		public ImageView background;
		public ImageView check;
		public TextView friendName;
	}

	public userAdapter(Context context, List<ParseUser> objects) {
		super(context, R.layout.grid_user, objects);
		mContext = context;
		users = objects;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		ViewHolder holder;
		
		if (rowView == null) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			rowView = inflater.inflate(R.layout.grid_user, null);
			
			holder = new ViewHolder();
			
			holder.background = (ImageView) rowView.findViewById(R.id.iv_background_grid);
			holder.check = (ImageView) rowView.findViewById(R.id.iv_check_grid);
			holder.friendName = (TextView) rowView.findViewById(R.id.tv_friend_name);
			
			rowView.setTag(holder);
		}
		else {
			holder = (ViewHolder) rowView.getTag();
		}
 
		return null;
	}

}
