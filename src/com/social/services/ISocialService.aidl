package com.social.services;

import java.util.List;
import com.social.model.Twit;

interface ISocialService {

	List<Twit> getSocialFeed();
	
	List<Twit> getCurrentSocialFeed();

   
}
