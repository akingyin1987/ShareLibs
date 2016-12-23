package com.zlcdgroup.libs;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.facebook.stetho.Stetho;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.tencent.bugly.crashreport.CrashReport;

import com.zlcdgroup.dao.BookDao;
import com.zlcdgroup.dao.DaoMaster;
import com.zlcdgroup.dao.DaoSession;
import com.zlcdgroup.libs.config.AppConfig;
import com.zlcdgroup.libs.db.Book;
import com.zlcdgroup.libs.db.DbCore;
import com.zlcdgroup.libs.db.ImageTextBean;
import com.zlcdgroup.libs.db.UpgradeHelper;
import com.zlcdgroup.libs.db.User;
import com.zlcdgroup.libs.photovideo.vo.TempBaseVo;
import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.easydarwin.video.beautify.util.ProjectUtils;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.QueryBuilder;
import org.lasque.tusdk.core.TuSdk;

/**
 * Created by Administrator on 2016/4/23.
 */
public class MyApp  extends Application {

    private static DaoMaster daoMaster;
    private static DaoSession daoSession;
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        // 设置拍摄视频缓存路径
        CrashReport.initCrashReport(this,"900027987",false);
        CrashReport.setUserId(System.currentTimeMillis()+"test");
        File dcim = Environment.getExternalStoragePublicDirectory(AppConfig.FILE_ROOT_URL);
        if(!dcim.isDirectory() ){
            dcim.mkdirs();
        }
        //if (DeviceUtils.isZte()) {
        //    if (dcim.exists()) {
        //        VCamera.setVideoCachePath(dcim + "/Camera/VCameraDemo/");
        //    } else {
        //        VCamera.setVideoCachePath(dcim.getPath().replace("/sdcard/", "/sdcard-ext/") + "/Camera/VCameraDemo/");
        //    }
        //} else {
        //    VCamera.setVideoCachePath(dcim + "/Camera/VCameraDemo/");
        //}
        //// 开启log输出,ffmpeg输出到logcat
        //VCamera.setDebugMode(true);
        //// 初始化拍摄SDK，必须
        //VCamera.initialize(this);

        Configuration  configuration =  new  Configuration.Builder(this)
                        .addModelClass(ImageTextBean.class)
                        .addModelClass(TempBaseVo.class).create();
        ActiveAndroid.initialize(configuration,true);
        initImageLoader(this);
        ProjectUtils.init(getApplicationContext());

        TuSdk.enableDebugLog(true);
        TuSdk.init(getApplicationContext(),"4387f9d67be3c238-01-k4rko1");
        initDao();
    }


    public static final boolean ENCRYPTED = false;
    public   void   initDao(){
        DbCore.init(this);
        daoSession = DbCore.getDaoSession();
        User   user = new User();
        user.account="test"+new Random().nextInt(1000);
        user.arg="111"+new Random().nextInt(1000);
        user.setUserId(new Random().nextInt(1000));
        user.uuid = UUID.randomUUID().toString();
        daoSession.getUserDao().save(user);
        if(null != user.getId()){
            Book  book = new Book();
            book.setPersion(user.getId());
            book.setName("book--"+new Random().nextInt(1000));
            daoSession.getBookDao().save(book);
        }
        QueryBuilder<Book> queryBuilder = daoSession.getBookDao().queryBuilder();

        queryBuilder.join(BookDao.Properties.Persion,Book.class).where(BookDao.Properties.Id.eq(1));
        List<Book>  books = queryBuilder.list();
        for(Book  book : books){
            System.out.println("book--"+book.name);
        }

    }
    /**
     * 取得DaoMaster
     *
     * @param context
     * @return
     */
    public static DaoMaster getDaoMaster(Context context)
    {
        if (daoMaster == null)
        {
            UpgradeHelper helper = new UpgradeHelper(context, "test-db", null);
            daoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return daoMaster;
    }
    /**
     * 取得DaoSession
     *
     * @param context
     * @return
     */
    public static DaoSession getDaoSession(Context context)
    {
        if (daoSession == null)
        {
            if (daoMaster == null)
            {
                daoMaster = getDaoMaster(context);
            }
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
       ActiveAndroid.dispose();
    }

    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context).threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024)
                // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                // .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }
}
