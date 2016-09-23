package com.zlcdgroup.libs.tusdkcamera;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.zlcdgroup.libs.R;
import org.lasque.tusdk.core.TuSdkResult;
import org.lasque.tusdk.impl.activity.TuFragment;
import org.lasque.tusdk.impl.components.camera.TuCameraFragment;

/**
 * @ Description:
 *
 * Company:重庆中陆承大科技有限公司
 * @ Author king
 * @ Date 2016/9/23 17:15
 * @ Version V1.0
 */

public class TuSdkCameraActivity  extends AppCompatActivity  {

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.demo_extend_camera_base_fragment);

     new CameraComponent().showSample(this);
  }


}
