package com.weidongjian.com.selfdestructingmessage.adapter;

import java.util.List;
import java.util.zip.Inflater;

import com.parse.ParseUser;
import com.squareup.picasso.Picasso;
import com.weidongjian.com.selfdestructingmessage.MD5Util;
import com.weidongjian.com.selfdestructingmessage.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class userAdapter extends ArrayAdapter<ParseUser> {
	
	private Context mContext;
	private List<ParseUser> mUsers;
	
	static class ViewHolder {
		public ImageView background;
		public ImageView check;
		public TextView friendName;
	}

	public userAdapter(Context context, List<ParseUser> objects) {
		super(context, R.layout.user_item, objects);
		mContext = context;
		mUsers = objects;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		ViewHolder holder;
		
		if (rowView == null) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			rowView = inflater.inflate(R.layout.user_item, null);
			
			holder = new ViewHolder();
			
			holder.background = (ImageView) rowView.findViewById(R.id.iv_background_grid);
			holder.check = (ImageView) rowView.findViewById(R.id.iv_check_grid);
			holder.friendName = (TextView) rowView.findViewById(R.id.tv_friend_name);
			
			rowView.setTag(holder);
		}
		else {
			holder = (ViewHolder) rowView.getTag();
		}
		
		ParseUser user = mUsers.get(position);
		String email = user.getEmail().toLowerCase();
		String hash = MD5Util.md5Hex(email);
		String avatarAddress = "http://www.gravatar.com/avatar/" + hash + "?s=200&r=pg&d=404";
		Picasso.with(mContext).load(avatarAddress).placeholder(R.drawable.avatar_empty).into(holder.background);
		
		holder.friendName.setText(user.getUsername());
		
		GridView gridView = (GridView) parent;
		if (gridView.isItemChecked(position)) {
			holder.check.setVisibility(View.VISIBLE);
		}
		else
			holder.check.setVisibility(View.INVISIBLE);
 
		return rowView;
	}
	
	public void refill(List<ParseUser> users) {
		mUsers.clear();
		mUsers.addAll(users);
		notifyDataSetChanged();
	}

}
