package com.social;

import java.util.List;

import android.app.ListActivity;
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
import android.widget.ImageButton;
import android.widget.ListView;

import com.social.adapters.TwitAdapter;
import com.social.model.OAuthTokens;
import com.social.model.Twit;
import com.social.services.ISocialService;
import com.social.services.SocialServiceImpl;
import com.social.services.managers.OAuthAuthenticatonMgr;

public class SocialFeed extends ListActivity {
	private static final String TAG = "SocialFeed";
	private ProgressDialog dialog = null;
	private OAuthAuthenticatonMgr authMgr;
	private ISocialService socialService = null;
	private ImageButton refreshButton = null;

	private class TwitServiceConnection implements ServiceConnection {
		// Called when the connection with the service is established
		public void onServiceConnected(ComponentName className, IBinder service) {
			socialService = ISocialService.Stub.asInterface(service);
			AsyncTask<OAuthTokens, Void, List<Twit>> async = new AsyncTask<OAuthTokens, Void, List<Twit>>() {

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
				protected List<Twit> doInBackground(OAuthTokens... params) {
					List<Twit> result = null;
					if (null != socialService) {
						try {
							result = socialService.getSocialFeed();
							// Nothing found in database so do a force fetch
							if (null == result || result.size() == 0) {
								result = socialService.getCurrentSocialFeed();
							}
						} catch (RemoteException e) {
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
				protected void onPostExecute(List<Twit> result) {

					super.onPostExecute(result);
					if (null != result) {
						TwitAdapter adapter = new TwitAdapter(
								getApplicationContext(), result);
						setListAdapter(adapter);

					}
					if (dialog.isShowing()) {
						dialog.dismiss();
					}
				}

			};
			OAuthAuthenticatonMgr authMgr = new OAuthAuthenticatonMgr(
					getApplicationContext());
			OAuthTokens oAuthTokens = authMgr.getAuthTokens();
			async.execute(oAuthTokens);
		}

		// Called when the connection with the service disconnects unexpectedly
		public void onServiceDisconnected(ComponentName className) {
			socialService = null;
		}
	};

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feed_list);
		this.refreshButton = (ImageButton) findViewById(R.id.force_refresh);
		this.dialog = new ProgressDialog(this);

		authMgr = new OAuthAuthenticatonMgr(getApplicationContext());
		OAuthTokens oAuthTokens = authMgr.getAuthTokens();
		final ServiceConnection connection = new TwitServiceConnection();
		if (null != oAuthTokens) {

			bindService(new Intent(getApplicationContext(),
					SocialServiceImpl.class), connection,
					Context.BIND_AUTO_CREATE);

		}
		this.refreshButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (null != socialService) {
					AsyncTask<OAuthTokens, Void, List<Twit>> async = new AsyncTask<OAuthTokens, Void, List<Twit>>() {

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
								OAuthTokens... params) {
							List<Twit> result = null;
							if (null != socialService) {
								try {
									result = socialService
											.getCurrentSocialFeed();

								} catch (RemoteException e) {
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
						protected void onPostExecute(List<Twit> result) {

							super.onPostExecute(result);
							if (null != result) {
								TwitAdapter adapter = new TwitAdapter(
										getApplicationContext(), result);
								setListAdapter(adapter);

							}
							if (dialog.isShowing()) {
								dialog.dismiss();
							}
						}

					};
					OAuthAuthenticatonMgr authMgr = new OAuthAuthenticatonMgr(
							getApplicationContext());
					OAuthTokens oAuthTokens = authMgr.getAuthTokens();
					async.execute(oAuthTokens);
				}

			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		Log.e("SocialFeed", "ON RESUME");
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
		this.socialService = null;
		Log.e("SocialFeed", "ON STOP");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.e("SocialFeed", "ON DESTROY");
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
		Intent navIntent = null;
		switch (item.getItemId()) {
		case R.id.remove_account:
			authMgr.saveAuthTokens(null, null);
			finish();
			navIntent = new Intent(getApplicationContext(),
					SplashScreen.class);
			startActivity(navIntent);
			return true;
		case R.id.settings:
			navIntent = new Intent(getApplicationContext(),
					DroidTwitSettings.class);
			startActivity(navIntent);
		default:
			return false;
		}
	}
}
