package com.example.androidwebview;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends Activity {
	WebView mWebView;
	public int FILECHOOSER_RESULTCODE = 2;
	public Uri mCapturedImageURI;
	public ValueCallback<Uri> mUploadMessage;  
	private String url;
	private String TAG = this.getClass().getSimpleName();
	
	@SuppressLint({ "JavascriptInterface", "NewApi", "SetJavaScriptEnabled" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mWebView = (WebView) findViewById(R.id.webView);  
		//mWebView.loadUrl("file:///android_asset/myfile.html");


		/*mWebView.addJavascriptInterface(new Object() {
			public void performClick(){
				// Deal with a click on the OK button
				Log.e(TAG,"click");
			}
		}, "imageblock_one");*/

		mWebView.getSettings().setLoadWithOverviewMode(true);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.addJavascriptInterface(new Jscalls(this), "Android");
		mWebView.setWebViewClient(new MyWebViewClient()); 
		mWebView.setWebChromeClient(new MyWebChromeClient());
		mWebView.getSettings().setPluginState(PluginState.ON);
		mWebView.getSettings().setAllowFileAccess(true);
		mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		mWebView.getSettings().setLoadWithOverviewMode(true);
		//mWebView.getSettings().setUseWideViewPort(true);
		
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
			mWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
			mWebView.getSettings().setAllowFileAccessFromFileURLs(true);
	    }
		
		url = "file:///android_asset/trial.html";
		mWebView.loadUrl(url);

		// "file:///android_asset/trial.html"

		// http://massimilianobianchi.info/max/drag_drop_mobile.html
	}


	private void refreshWebView() {
		mWebView.loadUrl("file:///android_asset/myfile.html");
	}

	final class MyWebChromeClient extends WebChromeClient {
		@Override
		public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
			Log.d(TAG, message);
			result.confirm();
			return true;
		}

		// openFileChooser for Android 3.0+
		public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType){  

			// Update message
			mUploadMessage = uploadMsg;

			try{    

				// Create AndroidExampleFolder at sdcard

				File imageStorageDir = new File(
						Environment.getExternalStoragePublicDirectory(
								Environment.DIRECTORY_PICTURES)
								, "AndroidExampleFolder");

				if (!imageStorageDir.exists()) {
					// Create AndroidExampleFolder at sdcard
					imageStorageDir.mkdirs();
				}

				// Create camera captured image file path and name 
				File file = new File(
						imageStorageDir + File.separator + "IMG_"
								+ String.valueOf(System.currentTimeMillis()) 
								+ ".jpg");

				mCapturedImageURI = Uri.fromFile(file); 

				// Camera capture image intent
				final Intent captureIntent = new Intent(
						android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

				captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);

				Intent i = new Intent(Intent.ACTION_GET_CONTENT); 
				i.addCategory(Intent.CATEGORY_OPENABLE);
				i.setType("image/*");

				// Create file chooser intent
				Intent chooserIntent = Intent.createChooser(i, "Image Chooser");

				// Set camera intent to file chooser 
				chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS
						, new Parcelable[] { captureIntent });

				// On select image call onActivityResult method of activity
				startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE );


			}
			catch(Exception e){
				Toast.makeText(getBaseContext(), "Exception:"+e, 
						Toast.LENGTH_LONG).show();
			}

		}

		// openFileChooser for Android < 3.0
		public void openFileChooser(ValueCallback<Uri> uploadMsg){
			openFileChooser(uploadMsg, "");
		}

		//openFileChooser for other Android versions
		public void openFileChooser(ValueCallback<Uri> uploadMsg, 
				String acceptType, 
				String capture) {

			openFileChooser(uploadMsg, acceptType);
		}



		// The webPage has 2 filechoosers and will send a 
		// console message informing what action to perform, 
		// taking a photo or updating the file

		public boolean onConsoleMessage(ConsoleMessage cm) {  
			onConsoleMessage(cm.message(), cm.lineNumber(), cm.sourceId());
			return true;
		}

		public void onConsoleMessage(String message, int lineNumber, String sourceID) {
			Log.e("androidruntime", "Show console messages, Used for debugging: " + message + lineNumber);
		}
	}   // End setWebChromeClient

	private class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			Log.e(TAG,"onpagefinished called ! ");
		}
	}

	class MyJavascriptBridge { 
		public void caller() {
			//now you know you are on the right place (chat.html)
			mWebView.loadUrl("javascript:openDialog()"); 
		}

		public void displayLog() {
			Log.e(TAG,"logging");
		}
	}

	public class Jscalls {

		Context mContext;
		Jscalls(Context c) {
			mContext = c;
		}

		@JavascriptInterface
		/** Show a toast from the web page */
		public void alert(String toast) {
			Log.e(TAG,"toast : " + toast);
			Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
		}    
	}

	@Override 
	protected void onActivityResult(int requestCode, int resultCode,  
			Intent intent) { 
		if(requestCode==FILECHOOSER_RESULTCODE)  {  
			if (null == this.mUploadMessage) {
				return;
			}
			Uri result=null;
			try{
				if (resultCode != RESULT_OK) {
					result = null;
				} else {
					// retrieve from the private variable if the intent is null
					result = intent == null ? mCapturedImageURI : intent.getData(); 
				} 
			}
			catch(Exception e){
				Toast.makeText(getApplicationContext(), "activity :"+e,
						Toast.LENGTH_LONG).show();
			}

			if(result != null) {
				String imagePath = "";
				String[] imgData = { MediaStore.Images.Media.DATA };
				Cursor imgCursor = getContentResolver().query(result, imgData, null, null, null);
				if(imgCursor!=null) {
					int index = imgCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
					imgCursor.moveToFirst();
					imagePath = imgCursor.getString(index);
				}
				else
					imagePath = result.getPath();

				mUploadMessage.onReceiveValue(result);
				
				Bitmap bmp = BitmapFactory.decodeFile(imagePath);
				bmp = Bitmap.createScaledBitmap(bmp, 240, 240, false);
				
				String resizedImageFilePath = saveToFile(bmp);
				String js = "";
				
				if(resizedImageFilePath.trim().length() > 0) {
					js = "javascript:loadImage(file://"+ resizedImageFilePath + ")";
				}else {
					js =  "javascript:loadImage("+ "" + ")";
				}
				mWebView.loadUrl(js);
			}else {
				mUploadMessage.onReceiveValue(new Uri.Builder().build());
				String js =  "javascript:loadImage("+ "" + ")";
				mWebView.loadUrl(js);
			}
			mUploadMessage = null;
		}
	}
	
	/** 
	 * resize bitmap and save to file
	 * @param bmp
	 * @return
	 */
	private String saveToFile(Bitmap bmp) {
		FileOutputStream out = null;
		try {
			File file = new File(Environment.getExternalStorageDirectory() + "/ResizedImages");
			if(!file.exists()) {
				file.mkdirs();
			}
			
			file = new File(file + "/temp.jpeg");
		    out = new FileOutputStream(file);
		    bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
		    
		    return file.getAbsolutePath();
		} catch (Exception e) {
		    e.printStackTrace();
		} finally {
		    try {
		        if (out != null) {
		            out.close();
		        }
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}
		
		return "";
	}
}
