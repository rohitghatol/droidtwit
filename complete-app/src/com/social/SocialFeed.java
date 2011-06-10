/**
 * Copyright 2011 Saurabh Gangarde & Rohit Ghatol (http://code.google.com/p/droidtwit/)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License.
 * 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.social;

import java.util.List;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.social.adapters.TwitAdapter;
import com.social.model.OAuthTokens;
import com.social.model.Twit;
import com.social.services.ISocialService;
import com.social.services.SocialServiceImpl;
import com.social.services.managers.FeedManager;
import com.social.services.managers.OAuthAuthenticatonMgr;

public class SocialFeed extends ListActivity {
	private static final String TAG = "SocialFeed";
	private ProgressDialog dialog = null;
	private OAuthAuthenticatonMgr authMgr;
	private ISocialService socialService = null;
	private ImageButton refreshButton = null;

	private class TwitServiceConnection implements ServiceConnection {
		// Called when the connection with the service is established
		public void onServiceConnected(final ComponentName className,
				final IBinder service) {
			socialService = ISocialService.Stub.asInterface(service);
			final AsyncTask<OAuthTokens, Void, List<Twit>> async = new AsyncTask<OAuthTokens, Void, List<Twit>>() {

				/*
				 * (non-Javadoc)
				 * 
				 * @see android.os.AsyncTask#onPreExecute()
				 */
				@Override
				protected void onPreExecute() {
					super.onPreExecute();

					dialog.setMessage("Loading twits...");
					dialog.show();

				}

				@Override
				protected List<Twit> doInBackground(final OAuthTokens... params) {
					List<Twit> result = null;
					if (null != socialService) {
						try {
							result = socialService.getSocialFeed();
							// Nothing found in database so do a force fetch
							if ((null == result) || (result.size() == 0)) {
								result = socialService.getCurrentSocialFeed();
							}
						} catch (final RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					return result;

				}

				/*
				 * (non-Javadoc)
				 * 
				 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
				 */
				@Override
				protected void onPostExecute(final List<Twit> result) {

					super.onPostExecute(result);
					if (null != result) {
						final TwitAdapter adapter = new TwitAdapter(
								getApplicationContext(), result);
						setListAdapter(adapter);
					}
					if (dialog.isShowing()) {
						dialog.dismiss();
					}
				}

			};

			// Get access tokens and start async task using these tokens
			final OAuthAuthenticatonMgr authMgr = new OAuthAuthenticatonMgr(
					getApplicationContext());
			final OAuthTokens oAuthTokens = authMgr.getAuthTokens();
			async.execute(oAuthTokens);
		}

		// Called when the connection with the service disconnects unexpectedly
		public void onServiceDisconnected(final ComponentName className) {
			socialService = null;
		}
	};

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feed_list);
		refreshButton = (ImageButton) findViewById(R.id.force_refresh);
		dialog = new ProgressDialog(this);

		authMgr = new OAuthAuthenticatonMgr(getApplicationContext());
		final OAuthTokens oAuthTokens = authMgr.getAuthTokens();

		// Make service connection
		final ServiceConnection connection = new TwitServiceConnection();

		if (null != oAuthTokens) {
			bindService(new Intent(getApplicationContext(),
					SocialServiceImpl.class), connection,
					Context.BIND_AUTO_CREATE);
		}

		refreshButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				if (null != socialService) {
					final AsyncTask<OAuthTokens, Void, List<Twit>> async = new AsyncTask<OAuthTokens, Void, List<Twit>>() {

						/*
						 * (non-Javadoc)
						 * 
						 * @see android.os.AsyncTask#onPreExecute()
						 */
						@Override
						protected void onPreExecute() {
							super.onPreExecute();

							dialog.setMessage("Refreshing twits...");
							dialog.show();

						}

						@Override
						protected List<Twit> doInBackground(
								final OAuthTokens... params) {
							List<Twit> result = null;
							if (null != socialService) {
								try {
									result = socialService
											.getCurrentSocialFeed();

								} catch (final RemoteException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}

							return result;

						}

						/*
						 * (non-Javadoc)
						 * 
						 * @see
						 * android.os.AsyncTask#onPostExecute(java.lang.Object)
						 */
						@Override
						protected void onPostExecute(final List<Twit> result) {

							super.onPostExecute(result);
							if (null != result) {
								final TwitAdapter adapter = new TwitAdapter(
										getApplicationContext(), result);
								setListAdapter(adapter);

							}
							if (dialog.isShowing()) {
								dialog.dismiss();
							}
						}

					};
					final OAuthAuthenticatonMgr authMgr = new OAuthAuthenticatonMgr(
							getApplicationContext());
					final OAuthTokens oAuthTokens = authMgr.getAuthTokens();
					async.execute(oAuthTokens);
				}

			}
		});
	}

	/**
	 * Handle list item click
	 */
	@Override
	protected void onListItemClick(final ListView l, final View v,
			final int position, final long id) {
		super.onListItemClick(l, v, position, id);
	}

	/**
	 * Create options menu
	 */
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	/**
	 * Handle menu items
	 */
	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case R.id.settings:
			handleSettings();
			return true;
		case R.id.tweet_live:
			handleTweet();
			return true;
		case R.id.about_us:
			handleAboutUs();
		default:
			return false;
		}
	}

	private void handleAboutUs() {
		final Intent navIntent = new Intent(getApplicationContext(),
				AboutUs.class);
		startActivity(navIntent);
	}

	private void handleTweet() {
		final Dialog dialog = new Dialog(this);
		dialog.setTitle(R.string.tweet);
		dialog.setContentView(R.layout.tweet);

		final Button tweetButton = (Button) dialog.findViewById(R.id.tweet);
		final Button cancelButton = (Button) dialog
				.findViewById(R.id.cancel_button);
		final EditText tweetText = (EditText) dialog
				.findViewById(R.id.tweet_text);

		tweetButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				final AsyncTask<String, Void, Void> asyncTask = new AsyncTask<String, Void, Void>() {

					@Override
					protected Void doInBackground(final String... params) {
						dialog.cancel();
						final FeedManager feedManager = new FeedManager(
								getApplicationContext());
						final OAuthAuthenticatonMgr authMgr = new OAuthAuthenticatonMgr(
								getApplicationContext());
						if (!authMgr.isAuthenticationRequired()) {
							feedManager.tweet(params[0],
									authMgr.getAuthTokens());

						}

						return null;
					}

					/*
					 * (non-Javadoc)
					 * 
					 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
					 */
					@Override
					protected void onPostExecute(final Void result) {

						super.onPostExecute(result);
						refreshButton.performClick();
					}

				};
				asyncTask.execute(tweetText.getText().toString());

			}
		});
		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				dialog.cancel();

			}
		});

		dialog.show();
	}

	private void handleSettings() {
		final Dialog dialog = new Dialog(this);
		dialog.setTitle(R.string.settings);
		dialog.setContentView(R.layout.settings);

		final Button applyButton = (Button) dialog
				.findViewById(R.id.apply_button);
		final Button cancelButton = (Button) dialog
				.findViewById(R.id.cancel_button);
		final CheckBox removeAccountCheck = (CheckBox) dialog
				.findViewById(R.id.remove_account_check);
		final EditText frequencySelect = (EditText) dialog
				.findViewById(R.id.refresh_rates);

		applyButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				if (removeAccountCheck.isChecked()) {
					resetAccount();
				}
				if (!removeAccountCheck.isChecked()) {
					updateFeedRefreshInterval(frequencySelect.getText()
							.toString());
				}
				dialog.cancel();

			}
		});
		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				dialog.cancel();

			}
		});

		dialog.show();
	}

	/**
	 * Update refresh interval in SharedPreferences and update AlarmManager to
	 * update interval
	 * 
	 * @param selectedItem
	 * @param refreshInterval
	 */
	private void updateFeedRefreshInterval(final String selectedItem) {
		final int interval = Integer.valueOf(selectedItem);
		System.out.println("Interval set to be :" + interval);

		final FeedManager feedManager = new FeedManager(getApplicationContext());
		feedManager.setTwitterFeedRefreshInterval(interval);

		// Update alarm manager in current running application instance
		final AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		final Intent intent = new Intent(getApplicationContext(),
				AlarmReceiver.class);
		final PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
				100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager.cancel(pendingIntent);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis() + 1000, interval * 60000,
				pendingIntent);
	}

	/**
	 * Remove account and access tokens associated with it
	 */
	private void resetAccount() {
		authMgr.saveAuthTokens(null, null);
		finish();
		final Intent navIntent = new Intent(getApplicationContext(),
				SplashScreen.class);
		startActivity(navIntent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		clearNotification();
	}

	/**
	 * Clear All Notification when this screen is shown
	 */
	private void clearNotification() {
		NotificationManager mNotificationManager = (NotificationManager) getApplicationContext()
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancelAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		Log.e("SocialFeed", "ON PAUSE");
	}

	/**
	 * Register Broadcast receiver to update twit feeds when SocialService sends
	 * a broadcast of new twtis available
	 * */
	@Override
	protected void onStart() {
		super.onStart();
		Log.e("SocialFeed", "ON START");
	}

	@Override
	protected void onStop() {
		super.onStop();
		socialService = null;
		Log.e("SocialFeed", "ON STOP");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.e("SocialFeed", "ON DESTROY");
	}
}
