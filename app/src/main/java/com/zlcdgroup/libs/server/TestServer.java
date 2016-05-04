package com.zlcdgroup.libs.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * @ Description:
 *
 * Company:重庆中陆承大科技有限公司
 * @ Author king
 * @ Date 2016/5/4 9:51
 * @ Version V1.0
 */
public class TestServer  extends Service {

  @Override
  public void onCreate() {
    super.onCreate();
    Log.d("Server","onCreate");
  }

  @Nullable @Override public IBinder onBind(Intent intent) {
    return null;
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    Log.d("Server","onStartCommand");
    return super.onStartCommand(intent, flags, startId);


  }
}
