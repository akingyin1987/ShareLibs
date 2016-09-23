package com.zlcdgroup.libs.tusdkcamera;

import android.support.v7.app.AppCompatActivity;
import org.lasque.tusdk.modules.components.TuSdkHelperComponent;

/**
 * @ Description:
 *
 * Company:重庆中陆承大科技有限公司
 * @ Author king
 * @ Date 2016/9/23 17:42
 * @ Version V1.0
 */

public abstract class CameraComponentBase {



  /** 组件帮助方法 */
  // see-http://tusdk.com/docs/android/api/org/lasque/tusdk/impl/components/base/TuSdkHelperComponent.html
  public TuSdkHelperComponent componentHelper;



  /** 显示范例 */
  public abstract void showSample(AppCompatActivity activity);
}
