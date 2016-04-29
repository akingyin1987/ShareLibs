package com.zlcdgroup.stickerlib;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import java.io.File;
import java.util.UUID;

/**
 * @ Description:
 *
 * Company:重庆中陆承大科技有限公司
 * @ Author king
 * @ Date 2016/4/29 11:34
 * @ Version V1.0
 */
public class Sticker {

  public   static    final   String  KEY_SAVE_FILENAME="saveReName";
  public   static    final   String  KEY_SAVE_FILEDIR="directoryPath";
  public   static    final   String  KEY_PATH="key_path";

  private Activity mContext;

  private String mSaveDir;

  private String mSaveName;

  private String   path;

  public   Sticker(@NonNull Activity  mContext){
       this.mContext = mContext;
  }

  public  Sticker  Path(@NonNull  String   path){
      this.path = path;
      return  this;
  }

  public  Sticker  saveName(String   mSavename){
    if(TextUtils.isEmpty(mSavename)){
      this.mSaveName = UUID.randomUUID().toString().replace("-","");
    }else{
      this.mSaveName = mSavename;
    }
    return  this;
  }

  public Sticker saveDir(@Nullable File dir) {
    if (dir == null) return saveDir((String) null);
    return saveDir(dir.getAbsolutePath());
  }

  public Sticker saveDir(@Nullable String dir) {
    mSaveDir = dir;
    return this;
  }

  public  void    start(int   resultCode){
    if(TextUtils.isEmpty(path) || TextUtils.isEmpty(mSaveDir) || TextUtils.isEmpty(mSaveName)){
      return;
    }
    Intent   intent = new Intent(mContext,StickerMainActivity.class);
    intent.putExtra(StickerMainActivity.KEY_PATH,path);
    intent.putExtra(StickerMainActivity.KEY_SAVE_FILEDIR,mSaveDir);
    intent.putExtra(StickerMainActivity.KEY_SAVE_FILENAME,mSaveName);
    mContext.startActivityForResult(intent,resultCode);
  }
}
