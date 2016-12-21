package com.zlcdgroup.taskManager;

import com.zlcdgroup.taskManager.enums.TaskManagerStatusEnum;

/**
 * 任务管理返回接口
 * Created by Administrator on 2016/7/3.
 */
public interface ApiTaskCallBack {

    //返回结果
    void   onCallBack(int total, int progress, int error);

    //任务正常完成时回调
    void   onComplete();

    //任务出错时回调
    void   onError(String message, TaskManagerStatusEnum statusEnum);

}
