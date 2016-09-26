package com.zlcdgroup.libs.db;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

/**
 * @ Description:
 *
 * Company:重庆中陆承大科技有限公司
 * @ Author king
 * @ Date 2016/9/26 15:13
 * @ Version V1.0
 */

public class DbDataBase  implements Database {
  private   SQLiteDatabase   db;
  public DbDataBase(SQLiteDatabase   db) {
    this.db = db;
  }

  @Override public Cursor rawQuery(String sql, String[] selectionArgs) {
    return db.rawQuery(sql,selectionArgs);
  }

  @Override public void execSQL(String sql) throws SQLException {
     db.execSQL(sql);
  }

  @Override public void beginTransaction() {
     db.beginTransaction();
  }

  @Override public void endTransaction() {
     db.endTransaction();
  }

  @Override public boolean inTransaction() {
    return false;
  }

  @Override public void setTransactionSuccessful() {
      db.setTransactionSuccessful();
  }

  @Override public void execSQL(String sql, Object[] bindArgs) throws SQLException {
     db.execSQL(sql,bindArgs);
  }

  @Override public DatabaseStatement compileStatement(String sql) {
    return null;
  }

  @Override public boolean isDbLockedByCurrentThread() {
    return false;
  }

  @Override public void close() {
      db.close();
  }

  @Override public Object getRawDatabase() {
    return null;
  }
}
