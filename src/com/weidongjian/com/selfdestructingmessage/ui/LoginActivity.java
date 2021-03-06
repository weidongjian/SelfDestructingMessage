package com.weidongjian.com.selfdestructingmessage.ui;

import android.R.animator;
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.weidongjian.com.selfdestructingmessage.BaseApplication;
import com.weidongjian.com.selfdestructingmessage.R;
import com.weidongjian.com.selfdestructingmessage.R.id;
import com.weidongjian.com.selfdestructingmessage.R.layout;
import com.weidongjian.com.selfdestructingmessage.R.menu;

public class LoginActivity extends Activity {
	
	protected EditText mUsername;
	protected EditText mPassword;
	protected CheckBox mShowPasswordCheckBox;
	protected Button mLoginButton;
	protected TextView mSingupTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		
		mUsername = (EditText) findViewById(R.id.et_username);
		mPassword = (EditText) findViewById(R.id.et_password);
		mShowPasswordCheckBox = (CheckBox) findViewById(R.id.cb_show_password);
		mLoginButton = (Button) findViewById(R.id.bt_login);
		mSingupTextView = (TextView) findViewById(R.id.tv_singup);

		mLoginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				v.startAnimation(AnimationUtils.loadAnimation(LoginActivity.this, R.anim.aa_btn));
				Animator anim = AnimatorInflater.loadAnimator(LoginActivity.this, R.anim.button_anim);
				anim.setDuration(1000);
				anim.setTarget(v);
				anim.addListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						super.onAnimationEnd(animation);
						
						String username = mUsername.getText().toString().trim();
						String password = mPassword.getText().toString().trim();
						
						if (username.isEmpty() || password.isEmpty()) {
							new AlertDialog.Builder(LoginActivity.this)
							.setTitle("Oops!")
							.setMessage("Please make sure you have inputed the username and password.")
							.setInverseBackgroundForced(true)
							.setPositiveButton(android.R.string.ok, null)
							.show();
							
							showButton();
						}
						else {
							ParseUser.logInInBackground(username, password, new LogInCallback() {
								  public void done(ParseUser user, ParseException e) {
								    if (e == null) {
								    	BaseApplication.updataInstallation(user);
								      navigateToMainAcvivity();
								    } else {
								      // Signup failed. Look at the ParseException to see what happened.
								    	new AlertDialog.Builder(LoginActivity.this)
										.setTitle("Oops!")
										.setMessage(e.getMessage())
										.setInverseBackgroundForced(true)
										.setPositiveButton(android.R.string.ok, null)
										.show();
								    	
								    	showButton();
								    }
								  }
								});
						}
					}
				});
				anim.start();
			}
		});
		
		mShowPasswordCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					mPassword.setTransformationMethod(null);
					mPassword.setSelection(mPassword.getText().length());
				}
				else {
					mPassword.setTransformationMethod(new PasswordTransformationMethod());
					mPassword.setSelection(mPassword.getText().length());
				}
			}
		});
		
		mSingupTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
				startActivity(intent);
				if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
				overridePendingTransition(R.anim.right_in, R.anim.left_out);
				}
			}
		});

	}
	
	private void navigateToMainAcvivity() {
		Intent intent = new Intent(LoginActivity.this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void showButton() {
		mLoginButton.setAlpha(1);
		mLoginButton.setScaleX(1);
		mLoginButton.setScaleY(1);
	}
}














