package com.flappygo.flappyim.ApiServer.Clients.AsyncTask;

import androidx.annotation.NonNull;
import android.os.Handler;
import android.os.Message;
import android.os.Looper;


/******
 * 异步回调Handler
 */
public class LXAsyncTaskHandler<M,T> extends Handler {

    // 成功的消息
    public static int SUCCESS_MSG = 1;

    // 失败的消息
    public static int FAILURE_MSG = 0;

    // 是否执行回调
    private boolean callBackEnable = true;

    //任务
    private final LXAsyncTask<M,T> task;

    //线程tag
    private final String tag;


    /*******
     * 判断当前handler是否响应
     * @return 回调是否可用
     */
    public boolean isCallBackEnable() {
        return callBackEnable;
    }

    /******
     * 设置当前handler是否响应
     * @param callBackEnable 是否响应
     */
    public void setCallBackEnable(boolean callBackEnable) {
        this.callBackEnable = callBackEnable;
    }

    /*********
     * 构建handler
     * @param task 任务
     */
    public LXAsyncTaskHandler(String tag, @NonNull LXAsyncTask<M,T> task) {
        super(Looper.getMainLooper());
        this.task = task;
        this.tag = tag;
    }

    /***********
     * 获取当前的task
     * @return 任务
     */
    public @NonNull LXAsyncTask<M,T> getTask() {
        return task;
    }

    /*************
     * 发送过来的消息
     */
    @SuppressWarnings("unchecked")
    public void handleMessage(@NonNull Message message) {
        if (!callBackEnable) {
            return;
        }
        if (message.what == SUCCESS_MSG) {
            task.success((T)message.obj, tag);
        } else if (message.what == FAILURE_MSG) {
            task.failure((Exception) message.obj, tag);
        }
    }

}
