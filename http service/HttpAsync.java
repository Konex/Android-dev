package com.yini.services;

import com.yini.utils.application.constants;

import android.app.Application;
import android.content.Intent;
import android.os.AsyncTask;

public class HttpAsync extends AsyncTask<String, Void, Void> {	
	private Application mApp;
	 
	public HttpAsync(Application app) {
        super();
        mApp = app;
    }

	 @Override
     protected Void doInBackground(String... params) {
		 Intent intent;
		 
		 if (params[2] != null) {
			 intent = new Intent(mApp, HttpPostService.class);
        	 intent.putExtra(constants.EXTRA_KEY_API_POST_VALUE, params[2]);
         } else {
        	 intent = new Intent(mApp, HttpGetService.class);
         }
		 
         intent.putExtra(constants.EXTRA_KEY_API_URL, params[0]);
         intent.putExtra(constants.EXTRA_KEY_API_INTENT_FILTER, params[1]);
         
         mApp.startService(intent);
         return null;
     }

     protected void onPostExecute(Void arg) {
         mApp = null;
     }
}
