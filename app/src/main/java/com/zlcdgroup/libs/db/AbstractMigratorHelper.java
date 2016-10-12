package com.zlcdgroup.libs.db;

import android.database.sqlite.SQLiteDatabase;

/**
 * @ Description:
 *  数据库升级
 * Company:重庆中陆承大科技有限公司
 * @ Author king
 * @ Date 2016/9/26 12:05
 * @ Version V1.0
 */

public abstract class AbstractMigratorHelper {

  public abstract void onUpgrade(SQLiteDatabase db);
}
