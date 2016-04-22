package com.zlcdgroup.libs.photovideo;


import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.zlcdgroup.base.model.BaseImgTextItem;
import com.zlcdgroup.db.ImageTextVo;
import com.zlcdgroup.db.ImageTextsVo;



import com.zlcdgroup.rushfee.R;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Viewpager 显示图片与文字的集合
 * 
 * @author Administrator
 * 
 */
public class PhotoViewpagerActivity extends Activity {

    public static final String TAG = "PhotoViewpagerActivity";

    private List<ImageTextVo> list;

    public HackyViewPager viewPager;

    public PagerTitleStrip mTitleStrip;//

    public List<View> pagerView;// 翻页页面

    ViewGroup main, group;

    TextView[] textViews;

    PhotoViewAttacher mAttacher;

    private MyAdapter myAdapter;

    private int currentPos;

    protected ImageLoader mImageLoader;
    
   

    DisplayImageOptions option;
    
    public   AnimateFirstDisplayListener  animateFirstListener= new  AnimateFirstDisplayListener();

    @SuppressWarnings("deprecation")
	@SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        LayoutInflater inflater = getLayoutInflater();
        main = (ViewGroup) inflater.inflate(R.layout.activity_view_imgtext, null);
        group = (ViewGroup) main.findViewById(R.id.viewGroup_page);
        currentPos = getIntent().getIntExtra("pos", 0);
        ImageTextsVo   imageTextsVo = (ImageTextsVo) getIntent().getSerializableExtra("imgeList");
        list = imageTextsVo.getImgs();
        Log.i(TAG, "list size = " + list.size());
        viewPager = (HackyViewPager) main.findViewById(R.id.imgtext_viewpager_view);
        mTitleStrip = (PagerTitleStrip) main.findViewById(R.id.imgtext_viewpager_pagertitle);
        textViews = new TextView[list.size()];
        /******************** 初始化界面图标 *******************/
        for (int i = 0; i < list.size(); i++) {

            TextView textView = new TextView(PhotoViewpagerActivity.this);

            textView.setLayoutParams(new LayoutParams(18, 18));

            textView.setPadding(0, 0, 2, 0);
            textViews[i] = textView;
            if (i == currentPos) {
                textViews[i].setBackgroundResource(R.drawable.page_now);
            } else {
                textViews[i].setBackgroundResource(R.drawable.page);
            }
            group.addView(textViews[i]);
        }
        mImageLoader = ImageLoader.getInstance();

        option = new DisplayImageOptions.Builder().
        		showImageForEmptyUri(R.drawable.contraband)
				.showImageOnFail(R.drawable.contraband)
				.resetViewBeforeLoading(true).cacheOnDisk(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
				.displayer(new FadeInBitmapDisplayer(300)).build();
        setContentView(main);
     //   initData();
        myAdapter = new MyAdapter(this);
        viewPager.setAdapter(myAdapter);
        viewPager.setOnPageChangeListener(new MyListener());
        viewPager.setCurrentItem(currentPos);

    }

   
    
    public    class  AnimateFirstDisplayListener  extends  SimpleImageLoadingListener{

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>()); 
            if (loadedImage != null) {
                @SuppressWarnings("unused")
				ImageView imageView = (ImageView) view;
               
                // 是否第一次显示
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                   // mAttacher = new PhotoViewAttacher(imageView);
                    displayedImages.add(imageUri);
                }
            }
           
        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
          
            super.onLoadingFailed(imageUri, view, failReason);
        }
        
    }
    
    
    class ViewHolder {
        public TextView imgdesc;// 图片说明

        public TextView text;// 文字说明

        public ImageView imgview;
        
        public PhotoView   photoView;

    }

    /**
     * Viewpager 翻转处理
     * 
     * @author Administrator
     * 
     */
    class MyAdapter extends PagerAdapter {
    	
    	public   Context    mContext;
    	
    	public   MyAdapter(Context   mContext){
    		this.mContext  =  mContext;
    	}

        @Override
        public int getCount() {

            return list.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {

            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

            ((ViewPager) container).removeView((View) object);

        }

        @Override
        public CharSequence getPageTitle(int position) {

            return (position + 1) + "";
        }

        @SuppressLint("InflateParams")
		@Override
        public Object instantiateItem(ViewGroup container, int position) {
        	
        	 BaseImgTextItem    img   =   list.get(position);
        	 View view = null;
        	 ViewHolder holder = null;
        	 if(null == view){
        		 view = LayoutInflater.from(mContext).inflate(R.layout.imgtext_item, null);
                  holder = new ViewHolder();
               
                 holder.imgdesc = (TextView) view.findViewById(R.id.imgtext_item_matrix);
                 holder.text = (TextView) view.findViewById(R.id.imgtext_item_text);
                 holder.photoView = (PhotoView) view.findViewById(R.id.imgtext_item_img);
         
                 view.setTag(holder);
             	
                 if (!TextUtils.isEmpty(img.localPath) ) {
                     if (holder.photoView.getVisibility() == View.GONE) {
                         holder.photoView.setVisibility(View.VISIBLE);
                         holder.text.setVisibility(View.GONE);
                     }
                     
                     if (!TextUtils.isEmpty(img.localPath)) {
     					
     					try {
     						mImageLoader.displayImage("file:///"+img.localPath, holder.photoView, option, animateFirstListener);
     					//	mAttacher = new PhotoViewAttacher(holder.imgview);
//     						File f = new File(img.localPath);
//     						if (f.exists()) {
//     							holder.imgview.setImageURI(Uri.fromFile(f));					
//     							mAttacher = new PhotoViewAttacher(holder.imgview);
//     							
//     						} else {
////     							if(iswifi){
////     								mImageLoader.displayImage(AppConfig.HTTP_BASE_URL + File.separator + img.webPath, holder.imgview, option, animateFirstListener);
////     							}
//     							holder.imgview.setImageResource(R.drawable.contraband);
//     						}
//     						
     					} catch (Exception e) {
     						holder.photoView.setImageResource(R.drawable.contraband);
     						// TODO: handle exception
     					}catch (Error e) {
     						holder.photoView.setImageResource(R.drawable.contraband);
     					}
     					
     				}else {
                     	 holder.photoView.setImageResource(R.drawable.contraband);
                       //  mImageLoader.displayImage(AppConfig.HTTP_BASE_URL+File.separator+img.webPath, holder.imgview, option,animateFirstListener);                
                     }
                  //   mAttacher = new PhotoViewAttacher(holder.imgview);
                     if(TextUtils.isEmpty(img.textdesc)){
                         holder.imgdesc.setVisibility(View.INVISIBLE);
                     }else{
                         holder.imgdesc.setText(String.valueOf(img.textdesc));
                     }
                 }else{
                     holder.imgdesc.setVisibility(View.INVISIBLE);
                     holder.photoView.setVisibility(View.INVISIBLE);
                     holder.text.setVisibility(View.VISIBLE);
                     holder.text.setText(String.valueOf(img.textdesc));
                 }
            //  maps.put(position, view);
        	 }
        
            ((ViewPager) container).addView(view);
           
            return view;
        }

    }

    class MyListener implements OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int arg0) {
            Log.i(TAG, "onPageSelected arg = " + arg0);
            textViews[arg0].setBackgroundResource(R.drawable.page_now);
            if (arg0 >= 1 && arg0 < textViews.length - 1) {
                textViews[arg0 - 1].setBackgroundResource(R.drawable.page);
                textViews[arg0 + 1].setBackgroundResource(R.drawable.page);
            }
            if (arg0 == 0) {
                textViews[1].setBackgroundResource(R.drawable.page);
            }
            if (arg0 == textViews.length - 1) {
                textViews[arg0 - 1].setBackgroundResource(R.drawable.page);
            }

        }

    }

    public void endbutton(View v) {
        finish();
    }

    @Override
    protected void onDestroy() {
        if (mAttacher != null) {
            mAttacher.cleanup();
        }

        super.onDestroy();
    }

}
