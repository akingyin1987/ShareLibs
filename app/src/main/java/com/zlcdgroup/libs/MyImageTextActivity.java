package com.zlcdgroup.libs;

import android.os.Environment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.zlcdgroup.libs.db.ImageTextBean;
import com.zlcdgroup.libs.photovideo.BaseImgTextItem;
import com.zlcdgroup.libs.photovideo.ImageVideoActivity;
import com.zlcdgroup.libs.photovideo.ImageVideoTypeModel;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2016/4/23.
 */
public class MyImageTextActivity  extends ImageVideoActivity {

    @Override
    public String getTitleName() {
        return "测试";
    }

    @Override
    public void addDATA(List<BaseImgTextItem> baseImgTextItems) {
        List<ImageTextBean>    items = new Select().from(ImageTextBean.class).orderBy("sort").execute();
        baseImgTextItems.addAll(items);
    }

    @Override
    public String getImageDirectory() {
        return  Environment.getExternalStorageDirectory().toString() + File.separator + "temp";
    }

    @Override
    public String getVideoDirectory() {
        return  Environment.getExternalStorageDirectory().toString() + File.separator + "temp";
    }

    @Override
    public void fristAdd(long time) {

    }

    @Override
    public BaseImgTextItem addText(int sort, String message) {
        ImageTextBean  item = new ImageTextBean();
        item.style=1;
        item.textdesc = message;
        item.sort = sort;
        item.save();
        return item;
    }

    @Override
    public BaseImgTextItem addImage(int sort, String localpath) {
        ImageTextBean  item = new ImageTextBean();
        item.style=2;
        item.localPath = localpath;
        item.originalLocalPath = localpath;
        item.sort = sort;
        item.save();
        return item;
    }

    @Override
    public BaseImgTextItem addVideo(int sort, String localpath) {
        ImageTextBean  item = new ImageTextBean();
        item.style=3;
        item.localPath = localpath;
        item.originalLocalPath = localpath;
        item.sort = sort;
        item.save();
        return item;
    }

    @Override
    public BaseImgTextItem PasteImageVideo(int sort, int style, String localPath, String oldFileName) {
        ImageTextBean  item = new ImageTextBean();
        item.style=style;
        item.localPath = localPath;
        item.originalLocalPath = localPath;
        item.copyName = oldFileName;
        item.sort = sort;
        item.save();
        return null;
    }

    @Override
    public ImageVideoTypeModel initImageVideoType() {
        return ImageVideoTypeModel.PHOTO;
    }

    @Override
    public void CustomView(LinearLayout content) {

    }

    @Override
    public void DataChange() {

    }

    @Override
    public void customOperation() {

    }

    @Override
    public void customOperationView(TextView customOperation, View v) {

    }
}
