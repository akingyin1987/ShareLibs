package com.zlcdgroup.camera.util;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import com.zlcdgroup.camera.CameraPreferences;
import com.zlcdgroup.camera.R;
import com.zlcdgroup.camera.internal.CameraApiCallback;


/**
 * @ Description:
 *
 * Company:重庆中陆承大科技有限公司
 * @ Author king
 * @ Date 2016/4/22 10:08
 * @ Version V1.0
 */
public class CameraDialogUtil {

  public   static    void   showCameraSetting(final SharedPreferences  sharedPreferences,
      final Context  context, final CameraApiCallback<String>  cb){
    final Dialog dialog =  new Dialog(context);
    dialog.setTitle("相机设置");
    dialog.setContentView(R.layout.camera_setting_custom);
    final CheckBox   custom_guideline = (CheckBox) dialog.findViewById(R.id.custom_guideline);
    final CheckBox   custom_lock_screen = (CheckBox)dialog.findViewById(R.id.custom_lock_screen);
    final EditText   custom_guideline_top = (EditText)dialog.findViewById(R.id.custom_guideline_top);
    final EditText   custom_guideline_left = (EditText)dialog.findViewById(R.id.custom_guideline_left);
    int   guide = sharedPreferences.getInt(CameraPreferences.KEY_GUIDE,0);
    custom_guideline.setChecked(guide !=0);
    final int  top = sharedPreferences.getInt(CameraPreferences.KEY_GUIDE_TOP,0);
    final int  left = sharedPreferences.getInt(CameraPreferences.KEY_GUIDE_LEFT,0);
    custom_guideline_top.setText(String.valueOf(top));
    custom_guideline_left.setText(String.valueOf(left));
    int   lockscreen = sharedPreferences.getInt(CameraPreferences.KEY_SCREEN,0);
    custom_lock_screen.setChecked(lockscreen !=0);
    dialog.findViewById(R.id.config).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
         String    topstr = custom_guideline_top.getText().toString().trim();
         String    leftstr = custom_guideline_left.getText().toString().trim();
         int   customtop = 0,customleft=0;
         if(!TextUtils.isEmpty(topstr)){
           customtop = Integer.parseInt(topstr);
         }
         if(!TextUtils.isEmpty(leftstr)){
           customleft = Integer.parseInt(leftstr);
         }
         if(customleft<0 || customleft>100 || customtop<0 || customtop>100){
           Toast.makeText(context,"数据输入错误，应在0-100",Toast.LENGTH_SHORT).show();
           return;
         }
         sharedPreferences.edit().putInt(CameraPreferences.KEY_GUIDE,custom_guideline.isChecked()?1:0)
             .putInt(CameraPreferences.KEY_SCREEN,custom_lock_screen.isChecked()?1:0)
             .putInt(CameraPreferences.KEY_GUIDE_LEFT,customleft)
             .putInt(CameraPreferences.KEY_GUIDE_TOP,customtop).apply();

           dialog.dismiss();
           if(null != cb){
             cb.call(null);
           }

      }
    });
    dialog.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        dialog.dismiss();
      }
    });
    dialog.show();
  }
}
