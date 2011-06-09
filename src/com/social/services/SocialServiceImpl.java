/**
 * Copyright 2011 Saurabh Gangarde & Rohit Ghatol (http://code.google.com/p/droidtwit/)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and limitations under the License.
 */
package com.social.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.social.R;
import com.social.SocialFeed;
import com.social.db.DBAdapter;
import com.social.model.Twit;
import com.social.services.managers.FeedManager;
import com.social.services.managers.OAuthAuthenticatonMgr;

public class SocialServiceImpl extends Service {
	private static final String TAG = "SocialService";
	// FIXME - Need more optimum logic, but this will do for the demo
	private final ISocialService.Stub mBinder = new ISocialService.Stub() {

		// FIXME - Need more optimum logic, but this will do for the demo
		public List<Twit> getSocialFeed() throws RemoteException {
			return getDBTwits();

		}

		// FIXME - Need more optimum logic, but this will do for the demo
		public List<Twit> getCurrentSocialFeed() throws RemoteException {
			FeedManager feedManager = new FeedManager();
			OAuthAuthenticatonMgr authMgr = new OAuthAuthenticatonMgr(
					getApplicationContext());
			if (!authMgr.isAuthenticationRequired()) {
				List<Twit> twits = feedManager.getSocialFeed(authMgr
						.getAuthTokens());
				DBAdapter dbAdapter = new DBAdapter(getApplicationContext());
				dbAdapter.open();
				for (Twit twit : twits) {
					long noOfRowsEffected = dbAdapter.updateTwit(
							twit.getTwitId(), twit.getProfileName(),
							twit.getImageUrl(), twit.getTwitMessage());
					// Check for new twit
					if (noOfRowsEffected < 1) {
						// Insert if not already present
						dbAdapter.insertTwit(twit.getTwitId(),
								twit.getProfileName(), twit.getImageUrl(),
								twit.getTwitMessage());

					}

				}
				dbAdapter.close();

				return getDBTwits();
			} else {
				return new ArrayList<Twit>();
			}
		}

		/**
		 * 
		 * @return All Twits present in the DB
		 */
		private List<Twit> getDBTwits() {

			DBAdapter dbAdapter = new DBAdapter(getApplicationContext());
			dbAdapter.open();
			Cursor cursor = dbAdapter.getAllTwits();
			try {
				int dbEntries = cursor.getCount();
				List<Twit> dbTwits = new ArrayList<Twit>();
				while (cursor.moveToNext()) {
					dbTwits.add(new Twit(cursor.getLong(0),
							cursor.getString(1), cursor.getString(2), cursor
									.getString(3)));
				}

				return dbTwits;
			} finally {
				if (null != cursor) {
					cursor.close();
				}
				dbAdapter.close();
			}
		}

	};

	@Override
	public IBinder onBind(final Intent intent) {
		return mBinder;
	}

	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate");

	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");
		// [timer release] Thank god, its not Objective-C
	}

	@Override
	public void onStart(final Intent intent, final int startid) {
		Log.d(TAG, "onStart");

		if (null != intent
				&& intent.getExtras().containsKey("ACTION")
				&& "UPDATE_FEEDS"
						.equals(intent.getExtras().getString("ACTION"))) {
			try {
				updateFeeds();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	// FIXME - Need more optimum logic, but this will do for the demo
	private void updateFeeds() throws RemoteException {
		System.out.println("updateFeed called at " + (new Date()));
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				boolean sendNotification = false;
				FeedManager feedManager = new FeedManager();
				OAuthAuthenticatonMgr authMgr = new OAuthAuthenticatonMgr(
						getApplicationContext());
				if (!authMgr.isAuthenticationRequired()) {
					List<Twit> twits = feedManager.getSocialFeed(authMgr
							.getAuthTokens());
					DBAdapter dbAdapter = new DBAdapter(getApplicationContext());
					dbAdapter.open();
					for (Twit twit : twits) {
						long noOfRowsEffected = dbAdapter.updateTwit(
								twit.getTwitId(), twit.getProfileName(),
								twit.getImageUrl(), twit.getTwitMessage());
						// Check for new twit
						if (noOfRowsEffected < 1) {
							// Insert and mark for notification
							dbAdapter.insertTwit(twit.getTwitId(),
									twit.getProfileName(), twit.getImageUrl(),
									twit.getTwitMessage());
							// Notify so that user comes to know about this
							sendNotification = true;
						}

					}
					if (sendNotification) {
						sendNotification();
					}

					dbAdapter.close();
				}

			}

		};
		Thread thread = new Thread(runnable);
		thread.start();

	}

	private void sendNotification() {
		final String ns = Context.NOTIFICATION_SERVICE;
		final NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		final int icon = R.drawable.twitter_icon;
		final CharSequence tickerText = "Twitter";
		final long when = System.currentTimeMillis();

		final Notification notification = new Notification(icon, tickerText,
				when);

		final Context context = getApplicationContext();
		final CharSequence contentTitle = "New Twits";
		final CharSequence contentText = "You have new twits!";
		final Intent notificationIntent = new Intent(getApplicationContext(),
				SocialFeed.class);

		final PendingIntent contentIntent = PendingIntent.getActivity(
				getApplicationContext(), 0, notificationIntent, 0);

		notification.setLatestEventInfo(context, contentTitle, contentText,
				contentIntent);

		final int HELLO_ID = 1;

		mNotificationManager.notify(HELLO_ID, notification);
	}

}
