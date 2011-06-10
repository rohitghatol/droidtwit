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
package com.tutorial.alarmmgr;

import java.util.Date;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author rohit
 * 
 */

public class AlarmReceiver extends BroadcastReceiver {

	

	@Override
	public void onReceive(final Context context, Intent intent) {
		Log.d(AlarmReceiver.class.getSimpleName(),"Got Broadcast message "+(new Date()));
		
		Runnable runnable = new Runnable(){

			public void run() {
				sendNotification(context);
				
			}
			
		};
		
		Thread thread = new Thread(runnable);
		thread.start();
		
		
	}
	
	private void sendNotification(Context context)
	{
		final String ns = Context.NOTIFICATION_SERVICE;
		final NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(ns);
		final int icon = R.drawable.icon;
		final CharSequence tickerText = "Notification";
		final long when = System.currentTimeMillis();

		final Notification notification = new Notification(icon, tickerText, when);


		final CharSequence contentTitle = "New Notification";
		final CharSequence contentText = "You have new Notification!";
		final Intent notificationIntent = new Intent(context, AlarmManagerExample.class);

		final PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

		final int HELLO_ID = 1;

		mNotificationManager.notify(HELLO_ID, notification);
	}
}
