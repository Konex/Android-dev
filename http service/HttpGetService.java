package com.yini.services;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.yini.utils.application.constants;

import android.app.IntentService;
import android.content.Intent;

public class HttpGetService extends IntentService {
	private static String TAG = "HttpGetService";
	
	public HttpGetService() {
        super(TAG);
        if (constants.SUPPORTS_ECLAIR) setIntentRedelivery(true);
        else setIntentRedelivery(false);
    }
	
	@Override
    protected void onHandleIntent(Intent intent) {
        String filter = intent.getStringExtra(constants.EXTRA_KEY_API_INTENT_FILTER);
        
        try {
        	String url = intent.getStringExtra(constants.EXTRA_KEY_API_URL);
            HttpResponse httpResponse = (new DefaultHttpClient()).execute(new HttpGet(url));

            if (httpResponse != null && 
        		httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK && 
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
