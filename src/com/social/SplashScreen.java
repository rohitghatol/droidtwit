package com.social;

import java.net.URI;

import winterwell.jtwitter.OAuthSignpostClient;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.social.services.managers.OAuthAuthenticatonMgr;

public class SplashScreen extends Activity {

	private static final int TWITTER = 1;
	private static final String TWITTER_KEY = "QFgKeMtBipewO4IG0rCNvw";
	private static final String TWITTER_SECRET = "OLUqNsO5oSRrv8MgjAHZ0zgHx60tHzfQ7P3dbzzZPoI";
	private static final String CALLBACK_URL = "ScribeTwit://twitt";
	

	private OAuthSignpostClient client;
	private ImageButton twitterButton;
	private OAuthAuthenticatonMgr authMgr = null;

	private String verifier;
	private String[] accessTokenAndSecret;

	/**
	 * Called when the activity is first created. Here we will setup oAuth
	 * related implementations.
	 * 
	 * */

	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 100,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (5000), 60000,pendingIntent);
		
//		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
//				System.currentTimeMillis(), (5 * 1000), pendingIntent);
//		
		System.out.println("Set Repearting alarm");

		setContentView(R.layout.main);
		authMgr = new OAuthAuthenticatonMgr(getApplicationContext());
		twitterButton = (ImageButton) findViewById(R.id.twitter);

		if (authMgr.isAuthenticationRequired()) {
			twitterButton.setVisibility(View.VISIBLE);
			createAuthorizationRequests(twitterButton);
		} else {
			twitterButton.setVisibility(View.INVISIBLE);
			navigateToSocialFeed();
		}
	}

	protected void onResume() {
		super.onResume();
		Log.e("ScribeTwit", "ON RESUME");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */

	protected void onPause() {
		super.onPause();
		Log.e("ScribeTwit", "ON PAUSE");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStart()
	 */

	protected void onStart() {
		super.onStart();
		Log.e("ScribeTwit", "ON START");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */

	protected void onDestroy() {
		super.onDestroy();
		Log.e("ScribeTwit", "ON DESTROY");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStop()
	 */

	protected void onStop() {
		super.onStop();
		Log.e("ScribeTwit", "ON STOP");
	}

	/**
	 * Create authorization request and launch login url in the browser
	 * 
	 * @param button
	 */
	void createAuthorizationRequests(final ImageButton button) {
		// Set on click listener
		button.setOnClickListener(new OnClickListener() {

			public void onClick(final View view) {
				String authUrl = null;
				try {
					authUrl = getAuthorizationUrl(TWITTER);

					startActivity(new Intent(Intent.ACTION_VIEW, Uri
							.parse(authUrl)));

				} catch (final Exception e) {
					Log.d("ScribeTwit",
							"Caught exception in createAuthorizationRequests "
									+ e.getMessage());
				}
			}
		});
	}

	/**
	 * When we get callback from browser after authentication, get the twits and
	 * Launch ListActivity - SocialFeed to display these twits
	 */
	protected void onNewIntent(final Intent intent) {
		super.onNewIntent(intent);
		try {
			saveAccessToken(intent);
			navigateToSocialFeed();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void navigateToSocialFeed() {
		Intent navIntent = new Intent(getApplicationContext(), SocialFeed.class);
		finish();
		startActivity(navIntent);
	}

	/**
	 * Get authorization url according social service selected
	 * 
	 * @param socialId
	 * @return
	 * @throws Exception
	 */
	private String getAuthorizationUrl(final int socialId) throws Exception {
		String authUrl = null;

		client = new OAuthSignpostClient(TWITTER_KEY, TWITTER_SECRET,
				CALLBACK_URL);

		final URI twitterUrl = client.authorizeUrl();
		authUrl = twitterUrl.toString();
		Log.e("Main", authUrl);

		return authUrl;
	}

	/**
	 * Get access token from verifier received in callback URL
	 * 
	 * @param intent
	 * @throws Exception
	 */
	private void saveAccessToken(final Intent intent) throws Exception {

		Log.e("OnResume", "Fetching access token ...");
		final Uri uri = intent.getData();
		if ((uri != null) && uri.toString().startsWith(CALLBACK_URL)) {
			verifier = uri.getQueryParameter("oauth_verifier");
			Log.e("OnResume", verifier);

			client.setAuthorizationCode(verifier);

			accessTokenAndSecret = client.getAccessToken();

			Log.e("NewIntent", "Access token: " + accessTokenAndSecret[0]);
			Log.e("NewIntent", "Token secret: " + accessTokenAndSecret[1]);

			authMgr.saveAuthTokens(accessTokenAndSecret[0],
					accessTokenAndSecret[1]);
		}

	}
}