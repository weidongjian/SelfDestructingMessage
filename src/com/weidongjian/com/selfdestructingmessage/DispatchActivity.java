package com.weidongjian.com.selfdestructingmessage;

import com.parse.ParseAnalytics;
import com.parse.ParseUser;
import com.parse.PushService;
import com.weidongjian.com.selfdestructingmessage.ui.LoginActivity;
import com.weidongjian.com.selfdestructingmessage.ui.MainActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class DispatchActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ParseAnalytics.trackAppOpened(getIntent());
		
		if (ParseUser.getCurrentUser() != null) {
			Intent i = new Intent(this, MainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(i);
//			System.out.println("new Intent(this, MainActivity.class)");
		}
		else {
			Intent i = new Intent(this, LoginActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(i);
//			System.out.println("new Intent(this, LoginActivity.class)");
		}
		
	}

}
