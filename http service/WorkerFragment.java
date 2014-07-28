package com.yini.services;

import android.app.Application;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class WorkerFragment extends Fragment {
	private Application mApp;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Tell the framework to try to keep this fragment around
		// during a configuration change.
		setRetainInstance(true);
		mApp = null;
	}
	
	/**
	 * This is called when the Fragment's Activity is ready to go, after its
	 * content view has been installed; it is called both after the initial
	 * fragment creation and after the fragment is re-attached to a new
	 * activity.
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		if (mApp == null) mApp = getActivity().getApplication();
	}
	
	/**
	 * This is called right before the fragment is detached from its current
	 * activity instance every time there is a configuration change.
	 */
	@Override
	public void onDetach() {
		// This fragment is being detached from its activity. We need
		// to make sure its thread is not going to touch any activity
		// state after returning from this function.
		super.onDetach();
	}

	@Override
	public void onDestroy() {
		mApp = null;
		super.onDestroy();
	}
	
	public void startAsync(String url, String filter, String postValue) {
		HttpAsync async = new HttpAsync(mApp);
		if (async.getStatus() == AsyncTask.Status.PENDING) {
			async.execute(url, filter, postValue);
		}
	}
	
	public void startAsync(String url, String filter, Bundle multipartVal, Bundle multipartImgVal) {
		MultipartAsync async = new MultipartAsync(mApp);
		if (async.getStatus() == AsyncTask.Status.PENDING) {
			async.execute(url, filter, multipartVal, multipartImgVal);
		}
	}
}
