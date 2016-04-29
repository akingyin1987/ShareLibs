package com.zlcdgroup.libs;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

  public Button   camera2_default;
  public RadioButton   camera_tow;
  public RadioButton   tuya_one,tuya_tow;

  public RadioButton   camera_system,camera_one;

  public RadioGroup  rg_tuya,rg_camera;
  SharedPreferences   sharedPreferences;
  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_infex);
    sharedPreferences = getSharedPreferences("setting_info", Activity.MODE_PRIVATE);
    camera_tow = (RadioButton)findViewById(R.id.camera_tow);
    tuya_one = (RadioButton)findViewById(R.id.tuya_one);
    tuya_tow = (RadioButton)findViewById(R.id.tuya_tow);
    camera_system = (RadioButton)findViewById(R.id.camera_system);
    camera_one = (RadioButton)findViewById(R.id.camera_one);
    int   tuya = sharedPreferences.getInt("tuya",0);
    if(tuya==0){
      tuya_one.setChecked(true);
    }else{
      tuya_tow.setChecked(true);
    }
    findViewById(R.id.camera_default).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        new MaterialCamera(IndexActivity.this).saveDir(
            Environment.getExternalStorageDirectory().toString() + File.separator + "temp")
            .startAuto(100);
      }
    });

    findViewById(R.id.camera1_default).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        new MaterialCamera(IndexActivity.this).saveDir(
            Environment.getExternalStorageDirectory().toString() + File.separator + "temp")
            .startCamera(101);
      }
    });

    camera2_default = (Button) findViewById(R.id.camera2_default);
    if(Build.VERSION.SDK_INT <Build.VERSION_CODES.LOLLIPOP){
      if(null != camera2_default){
        camera2_default.setVisibility(View.GONE);
      }
      if(null != camera_tow){
        camera_tow.setVisibility(View.GONE);
      }
    }
    int   camera = sharedPreferences.getInt("camera",0);
    if(camera == 0){
      camera_system.setChecked(true);
    }else if(camera == 1){
      camera_one.setChecked(true);
    }else if(camera == 2){
      camera_tow.setChecked(true);
    }
    camera2_default.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        new MaterialCamera(IndexActivity.this).saveDir(
            Environment.getExternalStorageDirectory().toString() + File.separator + "temp")
            .startCaera2(102);
      }
    });
    findViewById(R.id.imagelist).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent  intent = new Intent(IndexActivity.this,MyImageTextActivity.class);
        intent.putExtra(MyImageTextActivity.CAMERA_KEY,sharedPreferences.getInt("camera",0));
        intent.putExtra(MyImageTextActivity.TUYA_KEY,sharedPreferences.getInt("tuya",0));
        startActivity(intent);
      }
    });
    rg_camera = (RadioGroup)findViewById(R.id.rg_camera);
    rg_tuya = (RadioGroup)findViewById(R.id.rg_tuya);
    rg_tuya.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(checkedId == R.id.tuya_one){
          sharedPreferences.edit().putInt("tuya",0).apply();
        }else{
          sharedPreferences.edit().putInt("tuya",1).apply();
        }
      }
    });

    rg_camera.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(checkedId == R.id.camera_system){
          sharedPreferences.edit().putInt("camera",0).apply();
        }else if(checkedId == R.id.camera_one){
          sharedPreferences.edit().putInt("camera",1).apply();
        }else{
          sharedPreferences.edit().putInt("camera",2).apply();
        }
      }
    });
  }
}
