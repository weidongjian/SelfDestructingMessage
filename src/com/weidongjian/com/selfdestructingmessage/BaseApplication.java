package com.weidongjian.com.selfdestructingmessage;


import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.PushService;
import com.weidongjian.com.selfdestructingmessage.ui.MainActivity;

import android.app.Application;

public class BaseApplication extends Application {

	@Override
	public void onCreate() {
		Parse.initialize(this, "NkSX5F1euzX7EiiaJvKgHkbMWD7WCsBo0LrH7HGC", "B0jJ11ALr9ZBJfwJSF56fZ46XNmQ4wL6rkGQgFHJ");
//		PushService.setDefaultPushCallback(this, MainActivity.class);
		ParseInstallation installation = ParseInstallation.getCurrentInstallation();
		installation.saveInBackground();
		PushService.setDefaultPushCallback(this, MainActivity.class);
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).build();
		ImageLoader.getInstance().init(config);
	}
	
	public static void updataInstallation(ParseUser user) {
		ParseInstallation installation = ParseInstallation.getCurrentInstallation();
		installation.put(ParseConstant.KEY_USER_ID, user.getObjectId());
		installation.saveInBackground();
	}
	
}
