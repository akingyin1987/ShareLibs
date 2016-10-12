package com.zlcdgroup.libs.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.zlcdgroup.dao.DaoMaster;

/**
 * @ Description:
 * 数据库升级
 * Company:重庆中陆承大科技有限公司
 * @ Author king
 * @ Date 2016/9/26 12:07
 * @ Version V1.0
 */

public class UpgradeHelper  extends DaoMaster.OpenHelper {

  public  static  String TAG="UpgradeHelper";

  public UpgradeHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
    super(context, name, factory);
  }

  public UpgradeHelper(Context context, String name) {
    super(context, name);
  }

  /**
   * Here is where the calls to upgrade are executed
   */
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    System.out.println("oldVersion"+oldVersion+":"+newVersion);

        /* i represent the version where the user is now and the class named with this number implies that is upgrading from i to i++ schema */
    for (int i = oldVersion; i < newVersion; i++) {
      try {
                /* New instance of the class that migrates from i version to i++ version named DBMigratorHelper{version that the db has on this moment} */
        AbstractMigratorHelper migratorHelper = (AbstractMigratorHelper) Class.forName("com.zlcdgroup.libs.db.DBMigrationHelper" + i).newInstance();

        if (migratorHelper != null) {

                    /* Upgrade de db */
          migratorHelper.onUpgrade(db);
        }

      } catch (ClassNotFoundException | ClassCastException | IllegalAccessException | InstantiationException e) {

        Log.e(TAG, "Could not migrate from schema from schema: " + i + " to " + i++);
                /* If something fail prevent the DB to be updated to future version if the previous version has not been upgraded successfully */
        break;
      }


    }
  }
}
