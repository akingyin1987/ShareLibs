/*
	Copyright (c) 2013-2016 EasyDarwin.ORG.  All rights reserved.
	Github: https://github.com/EasyDarwin
	WEChat: EasyDarwin
	Website: http://www.easydarwin.org
*/
package org.easydarwin.video;

import akingyin.easyvideorecorder.R;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;

import org.easydarwin.video.recoder.activity.VideoRecorderActivity;
import org.easydarwin.video.recoder.base.BaseActivity;

public class StartActivity extends BaseActivity {




  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_start);
    ButterKnife.bind(this);
  }


  public void onStartClick(View v) {
    startActivity(VideoRecorderActivity.class);
  }
}
