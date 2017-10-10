package com.wm.bleconnect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

public class DisplayActivity extends Activity {
	private static final String TAG = ResourceUtils.TAG;
	private WebView mWeb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display);
		mWeb = (WebView)findViewById(R.id.web_dsp);
		Intent intent = getIntent();
		String name = intent.getStringExtra(ResourceUtils.DATA_HISTORY_FILE_NAME);
		if(name == null){
			Log.d(TAG,"DisplayActivity : ERROR file name = null ");
			return;
		}
		mWeb.getSettings().setSupportZoom(true);
		mWeb.getSettings().setUseWideViewPort(true);
		mWeb.loadUrl("file://"+name); 
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
