package com.zlcdgroup.libs.db;

import android.content.Context;
import com.zlcdgroup.dao.DaoMaster;
import com.zlcdgroup.dao.DaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.QueryBuilder;

/**
 * * *                #                                                   #
 * #                       _oo0oo_                     #
 * #                      o8888888o                    #
 * #                      88" . "88                    #
 * #                      (| -_- |)                    #
 * #                      0\  =  /0                    #
 * #                    ___/`---'\___                  #
 * #                  .' \\|     |# '.                 #
 * #                 / \\|||  :  |||# \                #
 * #                / _||||| -:- |||||- \              #
 * #               |   | \\\  -  #/ |   |              #
 * #               | \_|  ''\---/''  |_/ |             #
 * #               \  .-\__  '-'  ___/-. /             #
 * #             ___'. .'  /--.--\  `. .'___           #
 * #          ."" '<  `.___\_<|>_/___.' >' "".         #
 * #         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       #
 * #         \  \ `_.   \_ __\ /__ _/   .-` /  /       #
 * #     =====`-.____`.___ \_____/___.-`___.-'=====    #
 * #                       `=---='                     #
 * #     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   #
 * #                                                   #
 * #               佛祖保佑         永无BUG              #
 * #
 *
 * @ Description:                                          #
 * Company:重庆中陆承大科技有限公司
 * @ Author king
 * @ Date 2016/10/22 14:39
 * @ Version V1.0
 */

public class DbCore {
  public static final boolean ENCRYPTED = false;
  private static final String DEFAULT_DB_NAME = "rxjava.db";
  private static  final String  SECRET_KEY="";
  private static DaoMaster daoMaster;
  private static DaoSession daoSession;

  private static Context mContext;
  private static String DB_NAME;

  public static void init(Context context) {
    init(context, DEFAULT_DB_NAME);
  }

  public static void init(Context context, String dbName) {
    if (context == null) {
      throw new IllegalArgumentException("context can't be null");
    }
    mContext = context.getApplicationContext();
    DB_NAME = dbName;
  }

  public static DaoMaster getDaoMaster() {
    if (daoMaster == null) {
      DaoMaster.OpenHelper helper = new UpgradeHelper(mContext,ENCRYPTED ? "encrypted-"+DB_NAME : DB_NAME);
      Database db = ENCRYPTED ? helper.getEncryptedWritableDb(SECRET_KEY) : helper.getWritableDb();
      daoMaster = new DaoMaster(db);
    }
    return daoMaster;
  }

  public static DaoSession getDaoSession() {
    if (daoSession == null) {
      if (daoMaster == null) {
        daoMaster = getDaoMaster();
      }

      daoSession = daoMaster.newSession();
    }
    return daoSession;
  }

  public static void enableQueryBuilderLog(){

    QueryBuilder.LOG_SQL = true;
    QueryBuilder.LOG_VALUES = true;
  }
}
