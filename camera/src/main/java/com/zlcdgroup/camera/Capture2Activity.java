package com.zlcdgroup.camera;

import android.app.Fragment;
import android.support.annotation.NonNull;
import com.zlcdgroup.camera.internal.BaseCaptureActivity;
import com.zlcdgroup.camera.internal.Camera2Fragment;

/**
 * @ Description:
 *
 * Company:重庆中陆承大科技有限公司
 * @ Author king
 * @ Date 2016/4/22 11:24
 * @ Version V1.0
 */
public class Capture2Activity  extends BaseCaptureActivity{

  @NonNull @Override public Fragment getFragment() {
    return Camera2Fragment.newInstance();
  }
}
