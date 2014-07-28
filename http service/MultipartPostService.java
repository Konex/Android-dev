package com.yini.services;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

import com.yini.utils.application.constants;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

public class MultipartPostService extends IntentService {
	private static String TAG = "MultipartPostService";
	
	public MultipartPostService() {
		super(TAG);
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		String filter = intent.getStringExtra(constants.EXTRA_KEY_API_INTENT_FILTER);
		
		try {
			String url = intent.getStringExtra(constants.EXTRA_KEY_API_URL);
			Bundle valBundle = intent.getBundleExtra(constants.EXTRA_KEY_API_MULTIPART_VAL);
			Bundle imgBundle = intent.getBundleExtra(constants.EXTRA_KEY_API_MULTIPART_IMG_VAL);
			
			MultipartEntityBuilder multiPartEntity = MultipartEntityBuilder.create();
			multiPartEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
			
			if (valBundle != null) {
				for (String k : valBundle.keySet()) {
					multiPartEntity.addTextBody(k, valBundle.getString(k), ContentType.APPLICATION_JSON);
				}
			}
			
			if (imgBundle != null) {
				for (String k : imgBundle.keySet()) {
					multiPartEntity.addPart(k, compressImg(imgBundle.getString(k)));
				}
			}
			
			HttpPost post = new HttpPost(url);
			post.addHeader("Connection", "keep-alive");
			post.setEntity(multiPartEntity.build());
			
			HttpClient httpClient = new DefaultHttpClient();	
			httpClient.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, Charset.forName("UTF-8"));
			
			HttpResponse httpResponse = httpClient.execute(post);
			
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
	
	private ByteArrayBody compressImg(String path) throws FileNotFoundException {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

        BitmapFactory.decodeStream(new FileInputStream(path), null, options);
		final int REQUIRED_SIZE = 100;
		int scale = 8;

        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
				&& options.outHeight / scale / 2 >= REQUIRED_SIZE)
			scale *= 2;

        BitmapFactory.Options options2 = new BitmapFactory.Options();
		options2.inSampleSize = scale;
		Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(path),
				null, options2); // BitmapFactory.decodeFile(filePath);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
		byte[] byteArray = stream.toByteArray();

        return new ByteArrayBody(byteArray, path);
	}
}
