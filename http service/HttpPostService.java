package com.yini.services;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.yini.utils.application.constants;

import android.app.IntentService;
import android.content.Intent;

public class HttpPostService extends IntentService {
	private static String TAG = "HttpPostService";
	
	public HttpPostService() {
		super(TAG);
		if (constants.SUPPORTS_ECLAIR) setIntentRedelivery(true);
        else setIntentRedelivery(false);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String filter = intent.getStringExtra(constants.EXTRA_KEY_API_INTENT_FILTER);
		
		try {
			String url = intent.getStringExtra(constants.EXTRA_KEY_API_URL);
			String postValue = intent.getStringExtra(constants.EXTRA_KEY_API_POST_VALUE);
			StringEntity se = new StringEntity(postValue, HTTP.UTF_8);
			se.setContentType("application/json");
			HttpPost post = new HttpPost(url);
			post.setEntity(se);
			
			HttpResponse httpResponse = new DefaultHttpClient().execute(post);
			
			if (httpResponse != null && 
				(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK || 
				httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) &&
				httpResponse.getEntity() != null) {
				
				Intent completeIntent = new Intent(filter);
				
				completeIntent.putExtra(constants.EXTRA_KEY_HTTP_RESPONSE, 
						EntityUtils.toString(httpResponse.getEntity()));
				
				sendBroadcast(completeIntent);
			} else {
				
				Intent completeIntent = new Intent(filter);
				sendBroadcast(completeIntent);
			}
		} catch (Exception e) {
			e.printStackTrace();
			
			Intent completeIntent = new Intent(filter);
			sendBroadcast(completeIntent);
		}
	}
}
