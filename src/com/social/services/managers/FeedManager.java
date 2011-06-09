/**
 * 
 */
package com.social.services.managers;

import java.util.ArrayList;
import java.util.List;

import winterwell.jtwitter.OAuthSignpostClient;
import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.Twitter.User;

import com.social.model.OAuthTokens;
import com.social.model.Twit;

/**
 * @author rohit
 *
 */
public class FeedManager {

	public List<Twit> getSocialFeed(OAuthTokens tokens){
		List<Twit> twits = null;
		OAuthSignpostClient client = new OAuthSignpostClient(OAuthAuthenticatonMgr.TWITTER_KEY,OAuthAuthenticatonMgr.TWITTER_SECRET,tokens.getAccessToken(),tokens.getAccessSecret()); 
		Twitter twitter = new Twitter("saurabh",client);
		
		
		final List<Twitter.Status> statues = twitter.getHomeTimeline();
		twits = new ArrayList<Twit>(statues.size());
		for(Twitter.Status status:statues){
			User user = status.getUser();
			
			String twitText = status.getText();
			twits.add(new Twit(status.getId().longValue(),user.getName(), user.getProfileImageUrl().toString(), twitText));
		}
		return twits;
		
	}
	
	public void tweet(String tweet,OAuthTokens tokens){
		OAuthSignpostClient client = new OAuthSignpostClient(OAuthAuthenticatonMgr.TWITTER_KEY,OAuthAuthenticatonMgr.TWITTER_SECRET,tokens.getAccessToken(),tokens.getAccessSecret()); 
		Twitter twitter = new Twitter("saurabh",client);
		twitter.setStatus(tweet);
	}
}
