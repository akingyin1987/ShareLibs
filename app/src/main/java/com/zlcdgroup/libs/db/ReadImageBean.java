package com.zlcdgroup.libs.db;

import java.io.Serializable;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Generated;

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
 * @ Date 2016/12/21 15:49
 * @ Version V1.0
 */

@Entity(nameInDb = "tb_readimg")
public class ReadImageBean  implements Serializable {

  @Transient
  private static final long serialVersionUID = 1L;

  @Id(autoincrement = true)
  private Long id;

  public   String    localPath;

  public   String    webPath;

  public   Long    rdReading;

  @Property(nameInDb = "ocrReading")
  public   Long    orcReading;

  @Property(nameInDb = "result")
  public   int      result;  //1=识别成功且与读数匹配 2=识别成功与读数不匹配 3=无法识别 0=未识别

  @Generated(hash = 797248506)
public ReadImageBean(Long id, String localPath, String webPath, Long rdReading,
        Long orcReading, int result) {
    this.id = id;
    this.localPath = localPath;
    this.webPath = webPath;
    this.rdReading = rdReading;
    this.orcReading = orcReading;
    this.result = result;
}

@Generated(hash = 1716092295)
  public ReadImageBean() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getLocalPath() {
      return this.localPath;
  }

  public void setLocalPath(String localPath) {
      this.localPath = localPath;
  }

  public String getWebPath() {
      return this.webPath;
  }

  public void setWebPath(String webPath) {
      this.webPath = webPath;
  }

  public Long getRdReading() {
      return this.rdReading;
  }

  public void setRdReading(Long rdReading) {
      this.rdReading = rdReading;
  }

public Long getOrcReading() {
    return this.orcReading;
}

public void setOrcReading(Long orcReading) {
    this.orcReading = orcReading;
}

public int getResult() {
    return this.result;
}

public void setResult(int result) {
    this.result = result;
}
}
