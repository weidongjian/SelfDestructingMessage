package com.weidongjian.com.selfdestructingmessage.ui;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.weidongjian.com.selfdestructingmessage.BaseApplication;
import com.weidongjian.com.selfdestructingmessage.R;
import com.weidongjian.com.selfdestructingmessage.R.id;
import com.weidongjian.com.selfdestructingmessage.R.layout;
import com.weidongjian.com.selfdestructingmessage.R.menu;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.ViewDebug.FlagToString;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Build;

public class SignupActivity extends Activity {

	protected EditText mUsername;
	protected EditText mPassword;
	protected EditText mEmail;
	protected Button mSignupButton;
	protected CheckBox mShowPasswordCheckBox;
	protected TextView mBackToLogin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_sing_up);

		mUsername = (EditText) findViewById(R.id.et_username);
		mPassword = (EditText) findViewById(R.id.et_password);
		mEmail = (EditText) findViewById(R.id.et_email);
		mShowPasswordCheckBox = (CheckBox) findViewById(R.id.cb_show_password);
		mSignupButton = (Button) findViewById(R.id.bt_singup);
		mBackToLogin = (TextView) findViewById(R.id.tv_back_to_login);
		mBackToLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SignupActivity.this,
						LoginActivity.class);
				startActivity(intent);
				if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
					overridePendingTransition(R.anim.left_in, R.anim.right_out);
				}
				finish();
			}
		});

		mShowPasswordCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							mPassword.setTransformationMethod(null);
							mPassword
									.setSelection(mPassword.getText().length());
						} else {
							mPassword
									.setTransformationMethod(new PasswordTransformationMethod());
							mPassword
									.setSelection(mPassword.getText().length());
						}
					}
				});

		mSignupButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Animator anim = AnimatorInflater.loadAnimator(
						SignupActivity.this, R.anim.button_anim);
				anim.setDuration(1000);
				anim.setTarget(v);
				anim.addListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						super.onAnimationEnd(animation);
						
						String username = mUsername.getText().toString().trim();
						String email = mEmail.getText().toString().trim();
						String password = mPassword.getText().toString().trim();

						if (username.isEmpty() || password.isEmpty()
								|| email.isEmpty()) {
							new AlertDialog.Builder(SignupActivity.this)
									.setTitle("Oops!")
									.setMessage(
											"Please make sure you have inputed the username, password and email.")
									.setInverseBackgroundForced(true)
									.setPositiveButton(android.R.string.ok,
											null).show();
							showSignUpButton();
						} else {
							final ParseUser user = new ParseUser();
							user.setUsername(username);
							user.setEmail(email);
							user.setPassword(password);

							user.signUpInBackground(new SignUpCallback() {
								@Override
								public void done(ParseException e) {
									if (e == null) {
										// sing up successful
										BaseApplication
												.updataInstallation(user);
										Intent intent = new Intent(
												SignupActivity.this,
												MainActivity.class);
										intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
										intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
										startActivity(intent);
									} else {
										// fail to sing up
										new AlertDialog.Builder(
												SignupActivity.this)
												.setTitle("Oops!")
												.setMessage(e.getMessage())
												.setInverseBackgroundForced(
														true)
												.setPositiveButton(
														android.R.string.ok,
														null).show();
										showSignUpButton();
									}
								}
							});
						}
					}
				});
				anim.start();
			}
		});
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
	
	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.right_out);
	}

	private void showSignUpButton() {
		mSignupButton.setAlpha(1);
		mSignupButton.setScaleX(1);
		mSignupButton.setScaleY(1);
	}

}
