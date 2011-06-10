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
package com.tutorial.listactivity.adapter;

import java.util.List;

import com.tutorial.listactivity.R;
import com.tutorial.listactivity.R.id;
import com.tutorial.listactivity.R.layout;
import com.tutorial.listactivity.model.Twit;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @author (sg)
 * 
 */
public class TwitAdapter extends BaseAdapter
{
	private List<Twit> socialFeed;
	private Context context;
	

	/**
	 * @param context
	 * @param textViewResourceId
	 * @param objects
	 */
	public TwitAdapter(final Context context,  List<Twit> socialFeed )
	{
		super();
		this.socialFeed = socialFeed;
		this.context = context;
	}


	/*
	 * (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	
	public View getView(final int position, final View convertView, final ViewGroup parent)
	{
		View row = convertView;
		if ( row == null )
		{
			final LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = vi.inflate(R.layout.list_item, null);
		}

		Twit twit = (Twit) getItem(position);
		
		
		final TextView profileName = (TextView)row.findViewById(R.id.profileName);
		profileName.setText(twit.getProfileName());

		final TextView twitMessage = (TextView)row.findViewById(R.id.twitMessage);
		twitMessage.setText(twit.getTwitMessage());

		return row;
	}


	/*
	 * (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	
	public int getCount()
	{
		return socialFeed.size();
	}


	/*
	 * (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	
	public Object getItem(final int index)
	{
		
		return socialFeed.get(index);
	}


	/*
	 * (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	
	public long getItemId(final int index)
	{
		return index;
	}
}
