package com.weidongjian.com.selfdestructingmessage.adapter;

import java.util.Locale;

import com.weidongjian.com.selfdestructingmessage.R;
import com.weidongjian.com.selfdestructingmessage.R.string;
import com.weidongjian.com.selfdestructingmessage.ui.FriendFragment;
import com.weidongjian.com.selfdestructingmessage.ui.InboxFragment;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SectionsPagerAdapter extends FragmentPagerAdapter {
	
	protected Context mContext;

	public SectionsPagerAdapter(Context context, FragmentManager fm) {
		super(fm);
		mContext = context;
		// TODO Auto-generated constructor stub
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {
		case 0:
			return new InboxFragment();
		case 1:
			return new FriendFragment(); 
		}
		return null;
	}

	@Override
	public int getCount() {
		// Show 3 total pages.
		return 2;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		switch (position) {
		case 0:
			return mContext.getString(R.string.title_section1).toUpperCase();
		case 1:
			return mContext.getString(R.string.title_section2).toUpperCase();
		}
		return null;
	}

}
