package com.zlcdgroup.libs.ocr;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/25.
 */

public class YunShiEntity implements Serializable {

    /**
     * Code : 0
     * Pos : 105,288,440,285,440,348,105,352
     * Value : 6055,4
     * ProcessTime : 334
     * Message : ["检测成功"]
     * filename : 20161031093945_e3a1daeec3634a268c0d96ffee1a444b.jpg
     * Circle : -2,-2,-2
     */

    private int Code;
    private String Pos;
    private String Value;
    private String ProcessTime;
    private String filename;
    private String Circle;
    private String Message;

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public int getCode() {
        return Code;
    }

    public void setCode(int code) {
        Code = code;
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
