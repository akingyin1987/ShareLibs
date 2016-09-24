package com.zlcdgroup.libs.config;

import android.os.Environment;
import java.io.File;

/**
 * @ Description:
 *
 * Company:重庆中陆承大科技有限公司
 * @ Author king
 * @ Date 2016/9/24 10:38
 * @ Version V1.0
 */

public class AppConfig {

  /** SD卡文件存储根目录 */
  public static final  String FILE_ROOT_URL = Environment.getExternalStorageDirectory().toString() + File.separator + "temp";
}
