package com.zlcdgroup.libs.utils;

import java.io.File;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Administrator on 2016/10/24.
 */

public class RxUtil {
    /**
     * rxjava递归查询内存中的图片
     * @param f
     * @return
     */
    public static Observable<File> listFiles( File f){
        if(f.isDirectory()){
            return Observable.from(f.listFiles()).flatMap(new Func1<File, Observable<File>>() {
                @Override
                public Observable<File> call(File file) {
                    /**如果是文件夹就递归**/
                    return listFiles(file);
                }
            });
        } else {
            /**filter操作符过滤视频文件,是视频文件就通知观察者**/
            return Observable.just(f).filter(new Func1<File, Boolean>() {
                @Override
                public Boolean call(File file) {
                    return file.exists() && file.canRead() && FileUtil.isImage(file);
                }
            });
        }
    }
}
