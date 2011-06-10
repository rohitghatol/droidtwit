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
package com.tutorial;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView; 

public class HelloWorldScreen extends Activity {
	
	private static String Tag = HelloWorldScreen.class.getSimpleName();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Log.d(Tag,"onCreate()");
        
        Button button = (Button)findViewById(R.id.click_me);
        final TextView textView = (TextView)findViewById(R.id.textview);
        
        button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				textView.setText(R.string.world);
				
			}
		});
    }


	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		
		super.onStart();
		Log.d(Tag,"onStart()");
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		
		super.onResume();
		Log.d(Tag,"onResume()");
	}

	

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		
		super.onPause();
		Log.d(Tag,"onPause()");
	}

	

	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		
		super.onStop();
		Log.d(Tag,"onStop()");
	}
    
	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		
		super.onDestroy();
		Log.d(Tag,"onDestroy()");
	}
    
}