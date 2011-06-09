/**
 * 
 */
package com.social.model;

/**
 * @author rohit
 * 
 */
public class OAuthTokens {

	private String accessToken;
	private String accessSecret;

	/**
	 * @param accessToken
	 * @param accessSecret
	 */
	public OAuthTokens(String accessToken, String accessSecret) {
		super();
		this.accessToken = accessToken;
		this.accessSecret = accessSecret;
	}

	/**
	 * @return the accessToken
	 */
	public String getAccessToken() {
		return accessToken;
	}

	/**
	 * @return the accessSecret
	 */
	public String getAccessSecret() {
		return accessSecret;
	}

}
