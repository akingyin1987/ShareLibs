package com.zlcdgroup.libs.ocr.exception;

/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2018/8/1 10:27
 */
public class AipException extends  Exception{

  private String errorMsg;
  private int errorCode;

  public AipException(int code, String msg) {
    errorCode = code;
    errorMsg = msg;
  }

  public AipException(int code, Throwable e) {
    errorCode = code;
    errorMsg = e.getMessage();
  }

  public String getErrorMsg() {
    return errorMsg;
  }

  public void setErrorMsg(String errorMsg) {
    this.errorMsg = errorMsg;
  }

  public int getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(int errorCode) {
    this.errorCode = errorCode;
  }
}
