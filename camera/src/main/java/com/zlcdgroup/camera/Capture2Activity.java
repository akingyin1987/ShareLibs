package com.zlcdgroup.camera;

import android.app.Fragment;
import android.support.annotation.NonNull;
import android.view.KeyEvent;

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

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    return super.onKeyDown(keyCode, event);

  }


  @Override
  public void onBackPressed() {
    super.onBackPressed();
  }
}
