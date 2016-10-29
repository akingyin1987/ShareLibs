package com.zlcdgroup.libs.ocr;

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
 * @ Date 2016/10/29 10:07
 * @ Version V1.0
 */

public class YuanshBean {

  /**
   * Code : 10
   * Pos : 0,0,0,0,0,0,0,0
   * Value : XXXXX,5
   * ProcessTime : 0
   * Message : 用户信息未授权
   * filename :
   * Circle : 0,0,0
   */

  private String Code;
  private String Pos;
  private String Value;
  private String ProcessTime;
  private String Message;
  private String filename;
  private String Circle;

  public String getCode() {
    return Code;
  }

  public void setCode(String Code) {
    this.Code = Code;
  }

  public String getPos() {
    return Pos;
  }

  public void setPos(String Pos) {
    this.Pos = Pos;
  }

  public String getValue() {
    return Value;
  }

  public void setValue(String Value) {
    this.Value = Value;
  }

  public String getProcessTime() {
    return ProcessTime;
  }

  public void setProcessTime(String ProcessTime) {
    this.ProcessTime = ProcessTime;
  }

  public String getMessage() {
    return Message;
  }

  public void setMessage(String Message) {
    this.Message = Message;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public String getCircle() {
    return Circle;
  }

  public void setCircle(String Circle) {
    this.Circle = Circle;
  }
}
