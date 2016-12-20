package com.zlcdgroup.libs.tusdkcamera;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import com.zlcdgroup.camera.internal.CameraIntentKey;
import com.zlcdgroup.libs.R;
import com.zlcdgroup.libs.config.AppConfig;
import java.io.File;
import java.util.UUID;
import org.lasque.tusdk.core.TuSdkResult;
import org.lasque.tusdk.impl.activity.TuFragment;
import org.lasque.tusdk.impl.components.camera.TuCameraFragment;
import uk.co.senab.photoview.PhotoView;

/**
 * @ Description:
 *
 * Company:重庆中陆承大科技有限公司
 * @ Author king
 * @ Date 2016/9/23 17:15
 * @ Version V1.0
 */

public class TuSdkCameraActivity  extends AppCompatActivity  {

  PhotoView  camera_image;

  String   dir,picName;
  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.demo_extend_camera_base_fragment);
     camera_image = (PhotoView) findViewById(R.id.camera_image);
     dir = getIntent().getStringExtra(CameraIntentKey.SAVE_DIR);
     picName = getIntent().getStringExtra(CameraIntentKey.SAVE_NAME);
     if(TextUtils.isEmpty(picName)){
       picName = UUID.randomUUID().toString()+".jpg";
     }
     if(TextUtils.isEmpty(dir)){
       dir = AppConfig.FILE_ROOT_URL;
     }
    File  dirfile = new File(dir);
    if(null != dirfile && !dirfile.isDirectory()){
      dirfile.mkdirs();
    }
     File   saveFile = new File(dirfile,picName);
     new CameraComponent().showSample(this,dirfile,saveFile);
  }

  @Override protected void onStart() {
    super.onStart();
    try {
      File  file = new File(dir,picName);
      if(file.exists() && file.isFile()){
        camera_image.setImageURI(Uri.fromFile(file));
      }
    }catch (Exception e){
      e.printStackTrace();
    }
  }
}
