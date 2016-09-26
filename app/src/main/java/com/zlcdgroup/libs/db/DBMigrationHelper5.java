package com.zlcdgroup.libs.db;

import android.database.sqlite.SQLiteDatabase;
import com.zlcdgroup.dao.DaoMaster;


/**
 * @ Description:
 *
 * Company:重庆中陆承大科技有限公司
 * @ Author king
 * @ Date 2016/9/26 12:17
 * @ Version V1.0
 */

public class DBMigrationHelper5 extends  AbstractMigratorHelper {

  @Override public void onUpgrade(SQLiteDatabase db) {
    DaoMaster.dropAllTables(new DbDataBase(db),true);
    DaoMaster.createAllTables(new DbDataBase(db),true);

  }
}
