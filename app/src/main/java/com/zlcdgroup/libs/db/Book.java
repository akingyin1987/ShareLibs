package com.zlcdgroup.libs.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @ Description:
 *
 * Company:重庆中陆承大科技有限公司
 * @ Author king
 * @ Date 2016/9/27 10:16
 * @ Version V1.0
 */

@Entity(nameInDb = "tb_book")
public class Book  extends  Entry {

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

  public int getWebid() {
    return this.webid;
  }

  public void setWebid(int webid) {
    this.webid = webid;
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

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public long getPersion() {
    return this.persion;
  }

  public void setPersion(long persion) {
    this.persion = persion;
  }

  @Property(nameInDb = "user_Id")
  public   int   userId;

  @Property
  public  String   uuid;

  @Property
  public   String   name;

  @Property
  public   long   persion;

  @Generated(hash = 125132421)
  public Book(Long id, int webid, int userId, String uuid, String name,
      long persion) {
    this.id = id;
    this.webid = webid;
    this.userId = userId;
    this.uuid = uuid;
    this.name = name;
    this.persion = persion;
  }

  @Generated(hash = 1839243756)
  public Book() {
  }

}
