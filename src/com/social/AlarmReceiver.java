/**
 * 
 */
package com.social;

import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.social.model.OAuthTokens;
import com.social.services.ISocialService;
import com.social.services.SocialServiceImpl;
import com.social.services.managers.OAuthAuthenticatonMgr;

/**
 * @author rohit
 * 
 */

public class AlarmReceiver extends BroadcastReceiver {

	

	@Override
	public void onReceive(final Context context, Intent intent) {
		System.out.println("Got Broadcast message "+(new Date()));
		
		Runnable runnable = new Runnable(){

			public void run() {
				Intent serviceIntent = new Intent(context,SocialServiceImpl.class);
				serviceIntent.putExtra("ACTION", "UPDATE_FEEDS");
				context.startService(serviceIntent);
				
			}
			
		};
		
		Thread thread = new Thread(runnable);
		thread.start();
		
		
	}
}
