package com.zlcdgroup.util;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Spinner;

import com.zlcdgroup.tuyalib.R;


public class TuyaDialogUtil {
	
	@SuppressLint("InflateParams") 
	public  static  void   SelectColor(final Activity  context,final TuyaDialogCallback cb){

        
        final Dialog   dialog  = new  Dialog(context, R.style.tuya_loading_dialog);
        View    view  = LayoutInflater.from(context).inflate(R.layout.custom_2btn_spinner_dailog, null);
        dialog.setContentView(view);
        final Spinner  spinner  =  (Spinner)view.findViewById(R.id.dialogspinner);
       
        view.findViewById(R.id.dialogLeftBtn).setOnClickListener(new  OnClickListener() {
            
            @Override
            public void onClick(View v) {
            	 
                 dialog.dismiss();
                 if(null != cb){
                
                     cb.success(spinner.getSelectedItemPosition());
                 }
                
            }
        });
        view.findViewById(R.id.dialogRightBtn).setOnClickListener(new  OnClickListener() {
            
            @Override
            public void onClick(View v) {
            	 dialog.dismiss();
               
                
            }
        });
        
        dialog.show();
    
	}

}
