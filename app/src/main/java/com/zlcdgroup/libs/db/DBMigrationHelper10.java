package com.zlcdgroup.libs.db;

import android.database.sqlite.SQLiteDatabase;

/**
 * @ Description:
 * @ Author king
 * @ Date 2017/1/4 11:05
 * @ Version V1.0
 */

public class DBMigrationHelper10 extends AbstractMigratorHelper{
  @Override public void onUpgrade(SQLiteDatabase db) {
    String  sql1="alte table tb_readimg add ocrReading Long";
    String  sql2="alte table tb_readimg add result  int";
    db.execSQL(sql1);
    db.execSQL(sql2);
  }
}
