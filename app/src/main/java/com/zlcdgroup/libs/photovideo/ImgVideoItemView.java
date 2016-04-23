package com.zlcdgroup.libs.photovideo;




import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.zlcdgroup.libs.R;
public class ImgVideoItemView {


    public   TextView  txt,img_text;
    

    public   LinearLayout  img_layout;
    

    public   ImageView img,video_img,copy_img;

    
   
    
    public   void   bind(BaseImgTextItem  imgTextVo,Bitmap   bitmap){
        if(imgTextVo.style == 1){
            txt.setVisibility(View.VISIBLE);
            img_layout.setVisibility(View.GONE);
        }else if(imgTextVo.style == 2){
            txt.setVisibility(View.GONE);
            img_layout.setVisibility(View.VISIBLE);
            img.setVisibility(View.VISIBLE);
            video_img.setVisibility(View.GONE);
            img_text.setVisibility(View.GONE);
        }else if(imgTextVo.style == 3){
            txt.setVisibility(View.GONE);
            img_layout.setVisibility(View.VISIBLE);
            img.setVisibility(View.VISIBLE);
            video_img.setVisibility(View.VISIBLE);
            img_text.setVisibility(View.GONE);
        }
    
        if(imgTextVo.isIschecked()){
        	copy_img.setVisibility(View.VISIBLE);
        }else{
        	copy_img.setVisibility(View.GONE);
        }
        
        if(imgTextVo.style == 1){
            txt.setText(imgTextVo.textdesc);
        }else if(imgTextVo.style == 2){
            try {
               
                if(null != bitmap){
                	//CameraBitmapUtil.getImageThumbnail(imgTextVo.localPath, 320, 200)
                    img.setImageBitmap(bitmap);
                }else {
                    img.setImageResource(R.drawable.img_deletion);
                }
            } catch (Exception e) {
                img.setImageResource(R.drawable.img_deletion);
            } catch (Error e) {
                // TODO: handle exception
            }
        }else if(imgTextVo.style == 3){

            try {
               
                if(null != bitmap){
                	//CameraBitmapUtil.getVideoThumbnail(imgTextVo.localPath,320, 200, MediaStore.Images.Thumbnails.MICRO_KIND)
                    img.setImageBitmap(bitmap);
                }else {
                    img.setImageResource(R.drawable.video_deletion);
                }
            } catch (Exception e) {
                e.printStackTrace();
                img.setImageResource(R.drawable.video_deletion);
            } catch (Error e) {
                // TODO: handle exception
            }
        
        }
    
    }



}
