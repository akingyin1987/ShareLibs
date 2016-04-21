package com.zlcdgroup.libs;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.zlcdgroup.camera.MaterialCamera;
import java.io.File;

/**
 * @ Description:
 *
 * Company:重庆中陆承大科技有限公司
 * @ Author king
 * @ Date 2016/4/21 17:40
 * @ Version V1.0
 */
public class IndexActivity  extends AppCompatActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_infex);
    findViewById(R.id.camera_default).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        new MaterialCamera(IndexActivity.this).saveDir(
            Environment.getExternalStorageDirectory().toString() + File.separator + "temp")
            .start(100);
      }
    });
  }
}
