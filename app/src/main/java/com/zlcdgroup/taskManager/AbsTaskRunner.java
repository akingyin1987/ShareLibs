package com.zlcdgroup.taskManager;



import android.text.TextUtils;

import com.zlcdgroup.taskManager.enums.TaskManagerStatusEnum;
import com.zlcdgroup.taskManager.enums.TaskStatusEnum;
import java.util.LinkedList;
import java.util.List;

import java.util.UUID;



/**
 * 执行的任务
 * Created by Administrator on 2016/7/3.
 */
public  abstract class AbsTaskRunner implements Runnable ,ApiSonTaskCallBack{

    private     String   Tag = UUID.randomUUID().toString();//任务唯一ID标识


    public String getTag() {
        return Tag;
    }

    private TaskStatusEnum taskStatusEnum = TaskStatusEnum.NULL;

    private TaskStatusEnum  sonTaskStatusEnum = TaskStatusEnum.NULL;//子集任务状态

    private  ApiSonTaskCallBack   sonTaskCallBack;//监听子集结果异步

    public ApiSonTaskCallBack getSonTaskCallBack() {
        return sonTaskCallBack;
    }

    public void setSonTaskCallBack(ApiSonTaskCallBack sonTaskCallBack) {
        this.sonTaskCallBack = sonTaskCallBack;
    }

    public TaskStatusEnum getSonTaskStatusEnum() {
        return sonTaskStatusEnum;
    }

    public void setSonTaskStatusEnum(TaskStatusEnum sonTaskStatusEnum) {
        this.sonTaskStatusEnum = sonTaskStatusEnum;
    }

    public TaskStatusEnum getTaskStatusEnum() {
        return taskStatusEnum;
    }

    public void setTaskStatusEnum(TaskStatusEnum taskStatusEnum) {
        this.taskStatusEnum = taskStatusEnum;
    }

    //子任务只需要依次执行与当前任务在同一线程
    private List<AbsTaskRunner> queueTasks = new LinkedList<>();

    //获取子任务
    public List<AbsTaskRunner> getQueueTasks() {
        return queueTasks;
    }

    public   void    addTask(AbsTaskRunner   taskRunner){
        queueTasks.add(taskRunner);
    }

    public   void   addTasks(List<AbsTaskRunner>   taskRunners){
        queueTasks.addAll(taskRunners);
    }

    private  ITaskResultCallBack     callBack;

    public ITaskResultCallBack getCallBack() {
        return callBack;
    }

    public void setCallBack(ITaskResultCallBack callBack) {
        this.callBack = callBack;
    }

    //错误信息描述
    private     String    errorMsg;


    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {

        this.errorMsg = errorMsg;
    }

    public   void    TaskDoing(){
        taskStatusEnum = TaskStatusEnum.DOING;
    }

    public   void    TaskError(){
        taskStatusEnum = TaskStatusEnum.ERROR;
        if(null != callBack){
            callBack.onCallBack(taskStatusEnum,errorMsg);
        }
    }

    public   void    TaskOnNetError(){
        taskStatusEnum = TaskStatusEnum.NETERROR;
        if(TextUtils.isEmpty(errorMsg)){
            errorMsg ="网络错误，请稍候再试";
        }

        if(null != callBack){
            callBack.onCallBack(taskStatusEnum,errorMsg);
        }
    }

    public  void   TaskOnDoing(){
        if(taskStatusEnum != TaskStatusEnum.CANCEL){
            taskStatusEnum = TaskStatusEnum.DOING;
        }

    }

    //关闭整 个任务管理器
    public  void   closeTaskManager(){
        if(null != callBack){
            callBack.onCancelAllTask(TaskManagerStatusEnum.CANCEL,getErrorMsg());
        }
    }

    //完成整个任务管理器
    public  void   completeTaskManager(){
        if(null != callBack){
            callBack.onCancelAllTask(TaskManagerStatusEnum.COMPLETE,getErrorMsg());
        }
    }

    public  void  TaskOnSuccess(){

        taskStatusEnum = TaskStatusEnum.SUCCESS;
        if(null != callBack){
            callBack.onCallBack(taskStatusEnum,errorMsg);
        }
    }

    //取消任务
    public     void onCancel(){

      for(AbsTaskRunner   taskRunner : queueTasks){
          taskRunner.onCancel();
      }
        queueTasks.clear();
        if(taskStatusEnum == TaskStatusEnum.NULL || taskStatusEnum == TaskStatusEnum.WAITING
                || taskStatusEnum == TaskStatusEnum.DOING){
            taskStatusEnum = TaskStatusEnum.CANCEL;
        }


    }






    //任务执行前处理
    public  abstract   TaskStatusEnum  onBefore();

    //执行当前任务
    public  abstract  void    onToDo();


    private      void   doBackground(){
        TaskOnDoing();
        TaskStatusEnum  temp  = onBefore();
        if(temp == TaskStatusEnum.SUCCESS){
             doSonTaskBackground();
        }

    }



    @Override
    public void run() {

       if(taskStatusEnum == TaskStatusEnum.CANCEL){
           return;
       }
        doBackground();
    }

    int   index=0;
    public    void     doSonTaskBackground(){
        if(queueTasks.size() == 0 || index >= queueTasks.size()){
            sonTaskStatusEnum = TaskStatusEnum.SUCCESS;
            //当子任务执行完

            onToDo();
            return;
        }
        if(taskStatusEnum == TaskStatusEnum.CANCEL){
            sonTaskStatusEnum = TaskStatusEnum.CANCEL;
           return;
        }

        //执行子任务
        AbsTaskRunner   absTaskRunner = queueTasks.get(index);
        if(null != absTaskRunner && absTaskRunner.taskStatusEnum != TaskStatusEnum.CANCEL){
            absTaskRunner.setSonTaskCallBack(this);
            absTaskRunner.doBackground();

        }

    }

    @Override public void call(TaskStatusEnum taskStatusEnum) {
        setSonTaskStatusEnum(taskStatusEnum);
        if(taskStatusEnum == TaskStatusEnum.SUCCESS){
            index++;
            doSonTaskBackground();
        }else if(taskStatusEnum == TaskStatusEnum.ERROR ||
            taskStatusEnum == TaskStatusEnum.WAITING){
            TaskError();
        }else if(taskStatusEnum == TaskStatusEnum.NETERROR){
            TaskOnNetError();
        }
    }


}
