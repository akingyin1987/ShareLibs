package com.zlcdgroup.taskManager.enums;

/**
 * //任务执行状态
 * Created by Administrator on 2016/7/3.
 */
public enum TaskStatusEnum {
    NULL(1,"无"),WAITING(2,"等待中"),DOING(3,"进行中"),SUCCESS(4,"成功"),
    ERROR(5,"错误"),NETERROR(6,"网络错误"),CANCEL(7,"已取消");



    private String name;
    private int code;

    TaskStatusEnum(int   code, String  name){
        this.code = code;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }


    public static TaskStatusEnum getName(int code) {
        for (TaskStatusEnum c : TaskStatusEnum.values()) {
            if (c.getCode() == code) {
                return c;
            }
        }
        return null;
    }


}
