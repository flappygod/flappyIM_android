package com.flappygo.flappyim.ApiServer.Clients.AsyncTask;

import java.lang.ref.WeakReference;
import android.content.Context;
import android.os.Message;

/******
 * 异步线程
 * @param <M> 输入类型
 * @param <T> 输出类型
 */
public class LXAsyncTaskThread<M, T> extends Thread {

    private final WeakReference<Context> taskContext;
    private final LXAsyncTaskHandler<M, T> handler;
    private final LXAsyncTask<M, T> task;
    private final String taskTag;
    private final Object taskInput;

    /******
     * 构造函数
     * @param taskContext 线程所归属的上下文
     * @param taskInput   传入的数据
     * @param taskTag     线程tag
     * @param task        任务
     */
    public LXAsyncTaskThread(Context taskContext, Object taskInput, String taskTag, LXAsyncTask<M, T> task) {
        this.taskContext = new WeakReference<>(taskContext);
        this.handler = new LXAsyncTaskHandler<>(taskTag, task);
        this.task = task;
        this.taskTag = taskTag;
        this.taskInput = taskInput;
    }

    public String getTaskTag() {
        return taskTag;
    }

    public Object getTaskInput() {
        return taskInput;
    }

    public Context getTaskContext() {
        return taskContext.get();
    }

    public LXAsyncTaskHandler<M, T> getHandler() {
        return handler;
    }

    public LXAsyncTask<M, T> getTask() {
        return task;
    }

    public void setCallBackEnable(boolean callBackEnable) {
        handler.setCallBackEnable(callBackEnable);
    }

    public boolean cancelTask(LXAsyncTask<?, ?> task) {
        if (handler.getTask() == task) {
            handler.setCallBackEnable(false);
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void run() {
        try {
            T result = (T) task.run((M) taskInput, taskTag);
            Message msg = handler.obtainMessage(LXAsyncTaskHandler.SUCCESS_MSG, result);
            handler.sendMessage(msg);
        } catch (Exception e) {
            Message msg = handler.obtainMessage(LXAsyncTaskHandler.FAILURE_MSG, e);
            handler.sendMessage(msg);
        }
    }
}