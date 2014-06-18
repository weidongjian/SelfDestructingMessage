package com.weidongjian.com.selfdestructingmessage;

import com.parse.Parse;

import android.app.Application;

public class BaseApplication extends Application {

	@Override
	public void onCreate() {
		Parse.initialize(this, "NkSX5F1euzX7EiiaJvKgHkbMWD7WCsBo0LrH7HGC", "B0jJ11ALr9ZBJfwJSF56fZ46XNmQ4wL6rkGQgFHJ");
	}
	
	
}
