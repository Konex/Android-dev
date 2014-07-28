package com.yini.services;

import com.yini.services.MultipartPostService;
import com.yini.utils.application.constants;

import android.app.Application;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

public class MultipartAsync extends AsyncTask<Object, Void, Void> {
	private Application mApp;
	 
	public MultipartAsync(Application app) {
        super();
        mApp = app;
    }
	
	@Override
    protected Void doInBackground(Object... params) {
		 Intent intent = new Intent(mApp, MultipartPostService.class);
		 
		 if (params[2] != null) {
			 intent.putExtra(constants.EXTRA_KEY_API_MULTIPART_VAL, (Bundle) params[2]); 
		 }
		 
		 if (params[3] != null) {
			 intent.putExtra(constants.EXTRA_KEY_API_MULTIPART_IMG_VAL, (Bundle) params[3]); 
		 }
		 
		 intent.putExtra(constants.EXTRA_KEY_API_URL, (String) params[0]);
         intent.putExtra(constants.EXTRA_KEY_API_INTENT_FILTER, (String) params[1]);
         
         mApp.startService(intent);
         
		 return null;
	}
	
	protected void onPostExecute(Void arg) {
        mApp = null;
    }
}
