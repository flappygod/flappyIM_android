package com.flappygo.flappyim.ApiServer.Clients.AsyncTask;

import java.lang.ref.WeakReference;
import android.content.Context;
import android.os.Message;


/******
 * 异步线程
 */
public class LXAsyncTaskThread<M,T>extends Thread {

    // 线程所归属的上下文
    private final WeakReference<Context> taskContext;

    // 用于回调
    private LXAsyncTaskHandler<M,T> handler;

    // 任务
    private LXAsyncTask<M,T> task;

    // 线程tag
    private final String taskTag;

    //传入的数据
    private final Object taskInput;

    /******
     * @param tag  线程tag
     * @param task 任务
     */
    public LXAsyncTaskThread(Context taskContext, Object inObject, String tag, LXAsyncTask<M,T> task) {
        super();
        this.handler = new LXAsyncTaskHandler<M,T>(tag, task);
        this.taskContext = new WeakReference<Context>(taskContext);
        this.task = task;
        this.taskTag = tag;
        this.taskInput = inObject;
    }

    /******
     * 获取线程tag
     * @return 线程tag
     */
    public String getTaskTag() {
        return taskTag;
    }

    /******
     * 获取线程输入
     * @return 线程输入
     */
    public Object getTaskInput() {
        return taskInput;
    }

    /******
     * 获取线程上下文
     * @return 上下文
     */
    public Context getTaskContext() {
        if (taskContext != null) {
            return taskContext.get();
        }
        return null;
    }

    /******
     * 获取Handler
     * @return handler
     */
    public LXAsyncTaskHandler<M,T> getHandler() {
        return handler;
    }

    /******
     * 设置Handler
     * @return handler
     */
    public void setHandler(LXAsyncTaskHandler<M,T> handler) {
        this.handler = handler;
    }

    /******
     * 获取任务
     * @return 任务
     */
    public LXAsyncTask<M,T> getTask() {
        return task;
    }

    /******
     * 设置任务
     * @param task 任务
     */
    public void setTask(LXAsyncTask<M,T> task) {
        this.task = task;
    }

    /******
     * 设置线程执行完成后回调是否响应
     * @param callBackEnable 是否响应
     */
    public void setCallBackEnable(boolean callBackEnable) {
        handler.setCallBackEnable(callBackEnable);
    }

    /**********
     * 取消正在执行的任务
     * @param task 任务
     * @return 是否取消成功
     */
    public boolean cancelTask(LXAsyncTask<M,T> task) {
        if (handler.getTask() == task) {
            handler.setCallBackEnable(false);
            return true;
        }
        return false;
    }

    public void run() {
        try {
            Object object = task.run((M) taskInput, taskTag);
            // 发送执行成功的消息
            Message msg = handler.obtainMessage(LXAsyncTaskHandler.SUCCESS_MSG, object);
            //发送消息
            handler.sendMessage(msg);
        } catch (Exception e) {
            // 发送执行错误的消息
            Message msg = handler.obtainMessage(LXAsyncTaskHandler.FAILURE_MSG, e);
            //错误
            handler.sendMessage(msg);
        }
    }

}
