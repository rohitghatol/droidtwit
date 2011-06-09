/**
 * Copyright 2011 Saurabh Gangard & Rohit Ghatol (http://code.google.com/p/droidtwit/)
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
