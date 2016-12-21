package com.zlcdgroup.taskManager;

import com.zlcdgroup.taskManager.enums.TaskManagerStatusEnum;
import com.zlcdgroup.taskManager.enums.TaskStatusEnum;

/**
 * 任务执行结果回调
 * Created by Administrator on 2016/7/3.
 */
public interface ITaskResultCallBack {

    //任务状态回调
    void    onCallBack(TaskStatusEnum statusEnum, String error);

    //取消所有任务执行
    void    onCancelAllTask(TaskManagerStatusEnum statusEnum, String error);

}
