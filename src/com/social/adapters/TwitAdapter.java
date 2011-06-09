/**
 * Copyright Quickoffice, Inc, 2005-2009
 * 
 * NOTICE: The intellectual and technical concepts contained herein are proprietary to Quickoffice, Inc. and is
 * protected by trade secret and copyright law. Dissemination of any of this information or reproduction of this
 * material is strictly forbidden unless prior written permission is obtained from Quickoffice, Inc.
 * 
 * Created: Jun 3, 2011 Author: (sg)
 * 
 */

package com.social.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.social.R;
import com.social.model.Twit;

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
