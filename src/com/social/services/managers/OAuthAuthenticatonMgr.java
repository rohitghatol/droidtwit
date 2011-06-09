/**
 * 
 */
package com.social.services.managers;

import com.social.model.OAuthTokens;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * @author rohit
 * 
 */
public class OAuthAuthenticatonMgr {
	private static final int TWITTER = 1;
	private static final String AUTHORIZATIONS = "OAuthAccessTokens";
	private static final String ACCESS_TOKEN = "AccessToken";
	private static final String TOKEN_SECRET = "TokenSecret";
	public static final String TWITTER_KEY = "QFgKeMtBipewO4IG0rCNvw";
	public static final String TWITTER_SECRET = "OLUqNsO5oSRrv8MgjAHZ0zgHx60tHzfQ7P3dbzzZPoI";

	private Context context = null;
	

	public OAuthAuthenticatonMgr(Context context) {
		this.context = context;
	}

	/**
	 * 
	 * @return true if OAuth Authentication is required, false if application
	 *         already has required tokens
	 */
	public boolean isAuthenticationRequired() {
		final SharedPreferences settings = context.getSharedPreferences(
				AUTHORIZATIONS, 0);
		boolean result = true;
		String accessToken = settings.getString(ACCESS_TOKEN, null);
		String tokenSecret = settings.getString(TOKEN_SECRET, null);

		if (null != accessToken && null != tokenSecret) {
			result = false;
		}
		return result;
	}

	/**
	 * 
	 * @return OAuth OAuthTokens from shared preference, if tokens not found in shared preferences returns null
	 */
	public OAuthTokens getAuthTokens() {
		final SharedPreferences settings = context.getSharedPreferences(
				AUTHORIZATIONS, 0);

		String accessToken = settings.getString(ACCESS_TOKEN, null);
		String tokenSecret = settings.getString(TOKEN_SECRET, null);
		if (null != accessToken && null != tokenSecret) {
			return new OAuthTokens(accessToken, tokenSecret);
		} else {
			return null;
		}

	}
	
	/**
	 * Save the oAuth Token for future use
	 * @param accessToken
	 * @param tokenSecret
	 */
	public void saveAuthTokens(String accessToken, String tokenSecret){
		final SharedPreferences settings = context.getSharedPreferences(AUTHORIZATIONS,
				0);
		Editor editor = settings.edit();
		editor.putString(ACCESS_TOKEN, accessToken);
		editor.putString(TOKEN_SECRET, tokenSecret);
		editor.commit();
	}
}
