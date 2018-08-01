package com.zlcdgroup.libs.ocr.util;

import com.zlcdgroup.libs.ocr.exception.AipException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2018/8/1 10:25
 */
public class SignUtil {

  private static final char[] DIGITS;

  public static String hmacSha256(String key, String data) throws AipException {
    try {
      Mac mac = Mac.getInstance("HmacSHA256");
      SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(),
          mac.getAlgorithm());
      mac.init(signingKey);
      return encodeHex(mac.doFinal(data.getBytes()));
    } catch (Exception e) {
      e.printStackTrace();
      throw new AipException(-1, "Fail to generate HMAC-SHA256 signature");
    }
  }

  public static String md5(String data, String charset) {
    try {
      byte[] msg = data.getBytes(charset);
      MessageDigest md = MessageDigest.getInstance("MD5");
      return encodeHex(md.digest(msg));
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return null;
  }

  private static String encodeHex(byte[] data) {
    int l = data.length;
    char[] out = new char[l << 1];
    int i = 0;

    for (int j = 0; i < l; ++i) {
      out[j++] = DIGITS[(240 & data[i]) >>> 4];
      out[j++] = DIGITS[15 & data[i]];
    }

    return new String(out);
  }

  static {
    DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
  }
}
