package com.zlcdgroup.libs.ocr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zlcdgroup.libs.R;
import com.zlcdgroup.libs.db.ReadImageBean;

/**
 * @ Description:
 * @ Author king
 * @ Date 2017/1/4 11:21
 * @ Version V1.0
 */

public class Ocr2Adapter  extends  BaseTemplateAdapter<ReadImageBean> {

  Context context;
  LayoutInflater layoutInflater;
  public Ocr2Adapter(Context context) {
    this.context = context;
    layoutInflater = LayoutInflater.from(context);
  }
  @Override protected View getExView(int position, View convertView, ViewGroup parent) {
    OcrAdapter.ViewHolder viewHolder = null;
    ReadImageBean  readImageBean = getItem(position);
    if(null == convertView){
      convertView = layoutInflater.inflate(R.layout.ocr_itemview,null,false);
      viewHolder = new OcrAdapter.ViewHolder();
      viewHolder.imageView = (ImageView) convertView.findViewById(R.id.ocr_imgs);
      viewHolder.textView = (TextView) convertView.findViewById(R.id.ocr_txt);
      convertView.setTag(viewHolder);
    }else{
      viewHolder = (OcrAdapter.ViewHolder) convertView.getTag();
    }
    ImageLoader.getInstance().displayImage("file://"+getItem(position).localPath,viewHolder.imageView);

    viewHolder.textView.setText("人工："+readImageBean.rdReading+" Ocr:"+(null == readImageBean.orcReading?"":readImageBean.orcReading));
    return  convertView;
  }

  @Override protected void onReachBottom() {

  }

  static class  ViewHolder{
    ImageView imageView;
    TextView textView;
  }
}
