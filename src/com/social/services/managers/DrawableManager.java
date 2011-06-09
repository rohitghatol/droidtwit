/**
 * 
 */
package com.social.services.managers;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

/**
 * @author rohit
 * 
 */
public class DrawableManager {
	private final Map<String, Drawable> drawableMap;

	public DrawableManager() {
		drawableMap = new HashMap<String, Drawable>();
	}

	private Drawable fetchDrawable(String urlString) {

		Log.d(this.getClass().getSimpleName(),
				"fetchDrawable() -> trying to image url:" + urlString);
		try {
			InputStream is = fetch(urlString);
			Drawable drawable = Drawable.createFromStream(new FlushedInputStream(is), "src");
			if (null != drawable) {
				drawableMap.put(urlString, drawable);
				Log.d(this.getClass().getSimpleName(),
						"fetchDrawable() -> got a thumbnail drawable:"
								+ urlString + " " + drawable.getBounds() + ", "
								+ drawable.getIntrinsicHeight() + ","
								+ drawable.getIntrinsicWidth() + ", "
								+ drawable.getMinimumHeight() + ","
								+ drawable.getMinimumWidth());
			}
			return drawable;
		} catch (MalformedURLException e) {
			Log.e(this.getClass().getSimpleName(), "*************   fetchDrawable() -> "
					+ urlString + " fetchDrawable failed", e);
			return null;
		} catch (IOException e) {
			Log.e(this.getClass().getSimpleName(), "*************   fetchDrawable() -> "
					+ urlString + " fetchDrawable failed", e);
			return null;
		}
	}

	public void fetchDrawableOnThread(final String urlString,
			final ImageView imageView) {
		if (drawableMap.containsKey(urlString)) {
			Log.d(this.getClass().getSimpleName(),
					"fetchDrawableOnThread() -> got cached image for "
							+ urlString);
			imageView.setImageDrawable(drawableMap.get(urlString));
		} else {
			Log.d(this.getClass().getSimpleName(),
					"fetchDrawableOnThread() -> did not get cached image for "
							+ urlString);
			Thread thread = new Thread() {
				@Override
				public void run() {
					final Drawable drawable = fetchDrawable(urlString);
					if(!drawableMap.containsKey(urlString)){
						drawableMap.put(urlString, drawable);
					}
					if (null != drawable) {
						imageView.post(new Runnable() {

							@Override
							public void run() {
								Log.d(this.getClass().getSimpleName(),
										"fetchDrawableOnThread() -> setting image after downloading for "
												+ urlString);
								imageView.setImageDrawable(drawable);
							}
						});
					}
					else{
						Log.d(this.getClass().getSimpleName(),
								"fetchDrawableOnThread() -> Something wrong "
										+ urlString);
					}

				}
			};
			thread.start();
		}

	}

	private InputStream fetch(String urlString) throws MalformedURLException,
			IOException {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet request = new HttpGet(urlString);
		HttpResponse response = httpClient.execute(request);
		return response.getEntity().getContent();
	}

}