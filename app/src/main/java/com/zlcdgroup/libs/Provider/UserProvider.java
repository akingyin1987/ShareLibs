package com.zlcdgroup.libs.Provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.zlcdgroup.dao.DaoMaster;
import com.zlcdgroup.dao.UserDao;
import com.zlcdgroup.libs.BuildConfig;
import com.zlcdgroup.libs.MyApp;
import com.zlcdgroup.libs.Provider.base.BaseContentProvider;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.StandardDatabase;

/**
 * @ Description:
 *  用户数据共享
 * Company:重庆中陆承大科技有限公司
 * @ Author king
 * @ Date 2016/9/27 13:11
 * @ Version V1.0
 */

public class UserProvider  extends BaseContentProvider {

  private static final String TAG = UserProvider.class.getSimpleName();

  private static final boolean DEBUG = BuildConfig.DEBUG;

  private static final String TYPE_CURSOR_ITEM = "vnd.android.cursor.item/";
  private static final String TYPE_CURSOR_DIR = "vnd.android.cursor.dir/";

  public static final String AUTHORITY = "com.zlcdgroup.user.provider";
  public static final String CONTENT_URI_BASE = "content://" + AUTHORITY;

  private static final int URI_TYPE_USER = 0;
  private static final int URI_TYPE_USER_ID = 1;


  private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

  static {
    URI_MATCHER.addURI(AUTHORITY, UserDao.TABLENAME, URI_TYPE_USER);
    URI_MATCHER.addURI(AUTHORITY, UserDao.TABLENAME+"/#",URI_TYPE_USER_ID);//#号为通配符

  }


  @Override protected QueryParams getQueryParams(Uri uri, String selection, String[] projection) {
    QueryParams res = new QueryParams();
    String id = null;
    int matchedId = URI_MATCHER.match(uri);
    switch (matchedId) {
      case URI_TYPE_USER:
        res.table = UserDao.TABLENAME;
        res.idColumn = UserDao.Properties.Id.columnName;
        break;
      case URI_TYPE_USER_ID:
        res.table = UserDao.TABLENAME;
        res.idColumn = UserDao.Properties.Id.columnName;
        break;

      default:
        throw new IllegalArgumentException("The uri '" + uri + "' is not supported by this ContentProvider");
    }

    switch (matchedId) {
      case URI_TYPE_USER_ID:
        id = uri.getLastPathSegment();
        break;
      case URI_TYPE_USER:


    }
    if (id != null) {
      if (selection != null) {
        res.selection = res.table + "." + res.idColumn + "=" + id + " and (" + selection + ")";
      } else {
        res.selection = res.table + "." + res.idColumn + "=" + id;
      }
    } else {
      res.selection = selection;
    }
    return res;
  }

  @Override protected boolean hasDebug() {
    return DEBUG;
  }

  @Override protected SQLiteDatabase createSqLiteOpenHelper() {
    Database  database =MyApp.getDaoMaster(getContext()).getDatabase();
    if(null != database && database instanceof  StandardDatabase){
      return  ((StandardDatabase) database).getSQLiteDatabase();
    }
    return null;
  }

  @Nullable @Override public String getType(Uri uri) {
    int match = URI_MATCHER.match(uri);
    switch (match) {
      case URI_TYPE_USER:
        return TYPE_CURSOR_DIR + UserDao.TABLENAME;
      case URI_TYPE_USER_ID:
        return TYPE_CURSOR_ITEM + UserDao.TABLENAME;

    }
    return null;
  }



}
