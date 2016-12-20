package com.zlcdgroup.libs.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import us.pinguo.edit.sdk.core.utils.MD5;

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
 * @ Date 2016/11/7 16:30
 * @ Version V1.0
 */

public class FileHashUtil {

  public  static   String    EMPTY="";
  public  static   final   String   MD5="MD5";
  public  static   final   String   SHA1="SHA1";
  public  static   final   String   SHA_256="SHA-256";
  public  static   final   String   SHA_384="SHA-384";
  public  static   final   String   SHA_512="SHA-512";

  public   static   String    getFileHash(File  file,String  hashtype){
    if(null == file || !file.exists()){
      return  EMPTY;
    }
    MessageDigest digest = null;
    FileInputStream in = null;
    byte buffer[] = new byte[1024 *8];
    int len;
    try {
      digest = MessageDigest.getInstance(hashtype);
      in = new FileInputStream(file);
      while ((len = in.read(buffer)) != -1){
        digest.update(buffer, 0, len);
      }
      BigInteger bigInt = new BigInteger(1, digest.digest());
      return bigInt.toString(16);
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }finally {

      if(null != in){
        try {
          in.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return  EMPTY;
  }

  /**
   * 通过文件获取MD5值
   * @param file
   * @return
   */
  public  static   String   getFileMd5(File  file){

    return   getFileHash(file,MD5);
  }

  /**
   * 通过路径转MD5值
   * @param localpath
   * @return
   */
  public  static   String   getFileMd5(String  localpath){
    return  getFileMd5(new File(localpath));
  }
}
