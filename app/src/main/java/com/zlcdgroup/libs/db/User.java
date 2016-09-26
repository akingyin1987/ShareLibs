package com.zlcdgroup.libs.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Transient;

/**
 * @ Description:
 *
 * Company:重庆中陆承大科技有限公司
 * @ Author king
 * @ Date 2016/9/26 10:43
 * @ Version V1.0
 */

@Entity(nameInDb = "tb_user")
public class User   extends  Entry {

  @Property
  public  String    account;

  public  String    arg;

  @Id(autoincrement = true)
  private Long id;


  @Property
  public   int   webid;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Property(nameInDb = "user_Id")
  public   int   userId;

  @Property
  public  String   uuid;

  @Transient
  public   String   temp;

  @Generated(hash = 1432136717)
  public User(String account, String arg, Long id, int webid, int userId,
      String uuid) {
    this.account = account;
    this.arg = arg;
    this.id = id;
    this.webid = webid;
    this.userId = userId;
    this.uuid = uuid;
  }

  @Generated(hash = 586692638)
  public User() {
  }

  public String getAccount() {
    return this.account;
  }

  public void setAccount(String account) {
    this.account = account;
  }

  public String getArg() {
    return this.arg;
  }

  public void setArg(String arg) {
    this.arg = arg;
  }

  public int getUserId() {
    return this.userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public String getUuid() {
    return this.uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public int getWebid() {
    return this.webid;
  }

  public void setWebid(int webid) {
    this.webid = webid;
  }
}
