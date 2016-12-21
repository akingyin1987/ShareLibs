package com.zlcdgroup.libs.ocr.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zlcdgroup.libs.R;
import com.zlcdgroup.libs.ocr.OcrVo;

/**
 * Created by Administrator on 2016/10/24.
 */

public class OcrAdapter  extends  BaseTemplateAdapter<OcrVo> {
    Context  context;
    LayoutInflater   layoutInflater;
    public OcrAdapter(Context context) {
      this.context = context;
       layoutInflater = LayoutInflater.from(context);
    }

    @Override
    protected View getExView(int position, View convertView, ViewGroup parent) {
        ViewHolder   viewHolder = null;
        if(null == convertView){
            convertView = layoutInflater.inflate(R.layout.ocr_itemview,null,false);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.ocr_imgs);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.ocr_txt);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ImageLoader.getInstance().displayImage("file://"+getItem(position).localpath,viewHolder.imageView);
        System.out.println("text="+getItem(position).getOcrtext());
        viewHolder.textView.setText(getItem(position).getOcrtext());
        return convertView;
    }

    @Override
    protected void onReachBottom() {

    }

    static class  ViewHolder{
        ImageView   imageView;
        TextView     textView;
    }
}
