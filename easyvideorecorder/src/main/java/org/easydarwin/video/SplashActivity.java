/*
	Copyright (c) 2013-2016 EasyDarwin.ORG.  All rights reserved.
	Github: https://github.com/EasyDarwin
	WEChat: EasyDarwin
	Website: http://www.easydarwin.org
*/
package org.easydarwin.video;

import akingyin.easyvideorecorder.R;
import org.easydarwin.video.recoder.activity.VideoRecorderActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_activity);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				startActivity(new Intent(SplashActivity.this,VideoRecorderActivity.class));
				SplashActivity.this.finish();
			}
		}, 2000);

	}

}
