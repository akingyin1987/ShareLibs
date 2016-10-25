package com.zlcdgroup.libs.ocr;

/**
 * Created by Administrator on 2016/10/25.
 */

public class YunShiEntity extends  OcrVo {

    /**
     * Code : 9
     * Pos : 0,0,0,0,0,0,0,0
     * Value : XXXXX,5
     * ProcessTime : 0
     * Message : 请求参数错误(MacAddress)
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
