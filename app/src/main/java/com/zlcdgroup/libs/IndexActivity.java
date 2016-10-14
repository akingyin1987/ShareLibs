package com.zlcdgroup.libs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.akingyin.vcamera.ui.record.MediaRecorderActivity;
import com.cooltechworks.views.ScratchTextView;
import com.jaeger.library.StatusBarUtil;
import com.zlcdgroup.camera.GoogleCameraActivity;
import com.zlcdgroup.camera.MaterialCamera;
import com.zlcdgroup.libs.tusdkcamera.TuSdkCameraActivity;
import java.io.File;
import java.util.Date;
import java.util.Random;
import me.leolin.shortcutbadger.ShortcutBadger;
import si.virag.fuzzydateformatter.FuzzyDateTimeFormatter;
import us.pinguo.edit.sdk.PGEditActivity;
import us.pinguo.edit.sdk.base.PGEditResult;
import us.pinguo.edit.sdk.base.PGEditSDK;

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
  public TextView   tag_info;
  public RadioButton   camera_system,camera_one;
   String  mPicturePath;
  public RadioGroup  rg_tuya,rg_camera;
  SharedPreferences   sharedPreferences;
  public   long   startTime ;
  public ScratchTextView   tv_scratch;
  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_infex);

    StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary));
    startTime = System.currentTimeMillis();
    sharedPreferences = getSharedPreferences("setting_info", Activity.MODE_PRIVATE);
    camera_tow = (RadioButton)findViewById(R.id.camera_tow);
    tuya_one = (RadioButton)findViewById(R.id.tuya_one);
    tuya_tow = (RadioButton)findViewById(R.id.tuya_tow);
    camera_system = (RadioButton)findViewById(R.id.camera_system);
    camera_one = (RadioButton)findViewById(R.id.camera_one);
    tag_info =(TextView)findViewById(R.id.tag_info);
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

    findViewById(R.id.google_camera).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Intent  intent = new Intent(IndexActivity.this, GoogleCameraActivity.class);
        startActivity(intent);
      }
    });
    findViewById(R.id.vcamera).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Intent  intent = new Intent(IndexActivity.this, MediaRecorderActivity.class);
        startActivity(intent);
      }
    });

    findViewById(R.id.pg_vcamera).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Intent  intent  = new Intent(IndexActivity.this, TuSdkCameraActivity.class);
        startActivity(intent);
      }
    });
    tv_scratch = (ScratchTextView)findViewById(R.id.tv_scratch);

    tv_scratch.setText(getAppVersionName(this));
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
    tag_info.setText(FuzzyDateTimeFormatter.getTimeAgo(this,new Date(startTime)));
    ShortcutBadger.applyCount(this,new Random().nextInt(100));

  }
  private static final int REQUEST_CODE_PICK_PICTURE = 100;
  @Override protected void onResume() {
    super.onResume();
    tag_info.setText(FuzzyDateTimeFormatter.getTimeAgo(this,new Date(startTime)));
  }

  public static String getAppVersionName(Context context) {
    String versionName = "";
    try {
      // ---get the package info---
      PackageManager pm = context.getPackageManager();
      PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
      versionName = pi.versionName;

      if (versionName == null || versionName.length() <= 0) {
        return "";
      }
    } catch (Exception e) {
         e.printStackTrace();
    }
    return versionName;
  }


  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == REQUEST_CODE_PICK_PICTURE
        && resultCode == Activity.RESULT_OK
        && null != data) {

      Uri selectedImage = data.getData();
      String[] filePathColumns = new String[]{MediaStore.Images.Media.DATA};
      Cursor c = this.getContentResolver().query(selectedImage, filePathColumns, null, null, null);
      c.moveToFirst();
      int columnIndex = c.getColumnIndex(filePathColumns[0]);
      mPicturePath = c.getString(columnIndex);
      c.close();

      if (null != mPicturePath) {

      }

      return;
    }

    if (requestCode == PGEditSDK.PG_EDIT_SDK_REQUEST_CODE
        && resultCode == Activity.RESULT_OK) {

      PGEditResult editResult = PGEditSDK.instance().handleEditResult(data);


      Toast.makeText(this, "Photo saved to:" + editResult.getReturnPhotoPath(), Toast.LENGTH_LONG).show();

    }

    if (requestCode == PGEditSDK.PG_EDIT_SDK_REQUEST_CODE
        && resultCode == PGEditSDK.PG_EDIT_SDK_RESULT_CODE_CANCEL) {
      Toast.makeText(this, "Edit cancelled!", Toast.LENGTH_SHORT).show();
    }

    if (requestCode == PGEditSDK.PG_EDIT_SDK_REQUEST_CODE
        && resultCode == PGEditSDK.PG_EDIT_SDK_RESULT_CODE_NOT_CHANGED) {
      Toast.makeText(this, "Photo do not change!", Toast.LENGTH_SHORT).show();
    }
  }
}
