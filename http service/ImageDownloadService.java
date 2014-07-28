package com.yini.services;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import javax.mail.BodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;

import com.yini.utils.application.constants;
import com.google.common.io.ByteStreams;

public class ImageDownloadService extends IntentService {
	private static String TAG = "ImageDownloadService";

	public ImageDownloadService() {
		super(TAG);
		if (constants.SUPPORTS_ECLAIR)
			setIntentRedelivery(true);
		else
			setIntentRedelivery(false);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String filter = intent
				.getStringExtra(constants.EXTRA_KEY_API_INTENT_FILTER);

		try {
			String url = intent.getStringExtra(constants.EXTRA_KEY_API_URL);
			
			HttpResponse httpResponse = (new DefaultHttpClient())
					.execute(new HttpGet(url));

			if (httpResponse != null
					&& httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK
					&& httpResponse.getEntity() != null) {

				int IsSucessful = 0;
				String message = null;

				if (getSDRootPath() != null) {
					File imgDir = new File(getSDRootPath() + constants.LOCAL_IMAGE_LOCATION);
					if (!imgDir.exists()) {
						imgDir.mkdir();
					}

					ByteArrayDataSource ds = new ByteArrayDataSource(httpResponse
							.getEntity().getContent(), "multipart/mixed");

					MimeMultipart multipart = new MimeMultipart(ds);

					for (int i = 0; i < multipart.getCount(); i++) {

						BodyPart bodyPart = multipart.getBodyPart(i);

						if (bodyPart.getContentType().contains("image/png")) {
							File file = new File(imgDir, bodyPart.getFileName());

							if (file.exists())
								file.delete();

							InputStream is = (InputStream) bodyPart
									.getInputStream();

							FileOutputStream fos = new FileOutputStream(file);

							int size = 1024 * 1024;
							byte[] buf = new byte[size];
							int byteRead;
							while (((byteRead = is.read(buf)) != -1)) {
								fos.write(buf, 0, byteRead);
								byteRead += byteRead;
							}
							fos.close();
							buildPng(getSDRootPath() + constants.LOCAL_IMAGE_LOCATION,
									bodyPart.getFileName());
						}

						if (bodyPart.getContentType().contains("text/plain")
								&& bodyPart.getHeader("Content-Disposition")[0]
										.contains("IsSuccess")) {
							IsSucessful = Integer.parseInt(bodyPart.getContent()
									.toString());
						}

						if (bodyPart.getContentType().contains("text/plain")
								&& bodyPart.getHeader("Content-Disposition")[0]
										.contains("Message")) {
							message = bodyPart.getContent() == null ? null
									: bodyPart.getContent().toString();
						}
					}

					Bundle httpResp = new Bundle();
					httpResp.putInt("IsSuccess", IsSucessful);
					httpResp.putString("Message", message);

					Intent completeIntent = new Intent(filter);
					completeIntent.putExtra(constants.EXTRA_KEY_HTTP_RESPONSE,
							httpResp);
					sendBroadcast(completeIntent);
				}else {
					Intent completeIntent = new Intent(filter);
					sendBroadcast(completeIntent);
				}

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

	private String getSDRootPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();
		}else {
			return null;
		}
		return sdDir.toString();
	}

	private void buildPng(String path, String fileName) throws Exception {
		byte[] byteArray = ByteStreams.toByteArray(new FileInputStream(path
				+ "/" + fileName));
		Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0,
				byteArray.length);
		File imgDir = new File(getSDRootPath() + constants.LOCAL_IMAGE_LOCATION);
		File file = new File(imgDir, fileName + ".png");
		FileOutputStream fos = new FileOutputStream(file);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
		bos.flush();
		bos.close();
		fos.flush();
		fos.close();
		new File(imgDir, fileName).delete();
	}

}
