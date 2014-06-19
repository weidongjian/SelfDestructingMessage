package com.weidongjian.com.selfdestructingmessage;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends Activity {
	
	protected EditText mUsername;
	protected EditText mPassword;
	protected CheckBox mShowPasswordCheckBox;
	protected Button mLoginButton;
	protected TextView mSingupTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		ActionBar actionBar = getActionBar();
		actionBar.hide();
		
		mUsername = (EditText) findViewById(R.id.et_username);
		mPassword = (EditText) findViewById(R.id.et_password);
		mShowPasswordCheckBox = (CheckBox) findViewById(R.id.cb_show_password);
		mLoginButton = (Button) findViewById(R.id.bt_login);
		mSingupTextView = (TextView) findViewById(R.id.tv_singup);

		mLoginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String username = mUsername.getText().toString().trim();
				String password = mPassword.getText().toString().trim();
				
				if (username.isEmpty() || password.isEmpty()) {
					new AlertDialog.Builder(LoginActivity.this)
					.setTitle("Oops!")
					.setMessage("Please make sure you have inputed the username and password.")
					.setPositiveButton(android.R.string.ok, null)
					.show();
				}
				else {
					ParseUser.logInInBackground(username, password, new LogInCallback() {
						  public void done(ParseUser user, ParseException e) {
						    if (user != null) {
						      navigateToMainAcvivity();
						    } else {
						      // Signup failed. Look at the ParseException to see what happened.
						    	new AlertDialog.Builder(LoginActivity.this)
								.setTitle("Oops!")
								.setMessage(e.getMessage())
								.setPositiveButton(android.R.string.ok, null)
								.show();
						    }
						  }
						});
				}
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
				Intent intent = new Intent(LoginActivity.this, SingupActivity.class);
				startActivity(intent);
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

}
