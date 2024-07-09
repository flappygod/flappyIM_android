package com.flappygo.flappyim.ApiServer.Clients.AsyncTask;

import com.flappygo.flappyim.ApiServer.Clients.Thread.ExecutePoolExecutor;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/***************
 * 异步线程执行
 */
public class LXAsyncTaskClient {

    private final ExecutePoolExecutor threadPool;

    /*****
     * 构造器创建的时候设置线程池的大小
     * @param poolSize 线程池大小
     */
    public LXAsyncTaskClient(int poolSize) {
        this.threadPool = new ExecutePoolExecutor(poolSize);
    }

    /*********
     * 执行某个异步任务
     * @param task 异步任务
     */
    public void execute(LXAsyncTask<?, ?> task) {
        execute(task, null);
    }

    /*****
     * 执行某个异步线程
     * @param task 异步任务
     * @param taskInput 输入
     */
    public void execute(LXAsyncTask<?, ?> task, Object taskInput) {
        execute(task, taskInput, null);
    }

    /******
     * 执行某个异步线程
     * @param task 异步任务
     * @param taskInput 传入的参数
     * @param taskTag 线程taskTag
     */
    public void execute(LXAsyncTask<?, ?> task, Object taskInput, String taskTag) {
        execute(task, taskInput, taskTag, null);
    }

    /******
     * 执行某个异步线程
     * @param task 异步任务
     * @param taskInput 传入的参数
     * @param context 线程的归属
     * @param taskTag 线程taskTag
     */
    public void execute(LXAsyncTask<?, ?> task, Object taskInput, String taskTag, Context context) {
        LXAsyncTaskThread<?, ?> thread = new LXAsyncTaskThread<>(context, taskInput, taskTag, task);
        threadPool.execute(thread);
    }

    /*******
     * 禁止当前的所有线程执行回调操作
     */
    public void cancelAllTask() {
        List<Thread> threads = threadPool.getAllThreads();
        for (Thread thread : threads) {
            if (thread instanceof LXAsyncTaskThread) {
                LXAsyncTaskThread<?, ?> asyncTaskThread = (LXAsyncTaskThread<?, ?>) thread;
                asyncTaskThread.setCallBackEnable(false);
                threadPool.remove(thread);
            }
        }
    }

    /******
     * 通过线程的taskTag关闭线程
     * @param taskTag 线程tag
     */
    public void cancelTaskByTaskTag(String taskTag) {
        List<Thread> threads = threadPool.getAllThreads();
        for (Thread thread : threads) {
            if (thread instanceof LXAsyncTaskThread) {
                LXAsyncTaskThread<?, ?> asyncTaskThread = (LXAsyncTaskThread<?, ?>) thread;
                if (taskTag.equals(asyncTaskThread.getTaskTag())) {
                    asyncTaskThread.setCallBackEnable(false);
                    threadPool.remove(thread);
                }
            }
        }
    }

    /******
     * 通过上下文关闭
     * @param context 上下文，这里我们使用了hashCode
     */
    public void cancelTask(Context context) {
        List<Thread> threads = threadPool.getAllThreads();
        for (Thread thread : threads) {
            if (thread instanceof LXAsyncTaskThread) {
                LXAsyncTaskThread<?, ?> asyncTaskThread = (LXAsyncTaskThread<?, ?>) thread;
                if (context == asyncTaskThread.getTaskContext()) {
                    asyncTaskThread.setCallBackEnable(false);
                    threadPool.remove(thread);
                }
            }
        }
    }

    /******
     * 撤销某个任务
     * @param task 某个任务
     */
    public void cancelTask(LXAsyncTask<?, ?> task) {
        List<Thread> threads = threadPool.getAllThreads();
        for (Thread thread : threads) {
            if (thread instanceof LXAsyncTaskThread) {
                LXAsyncTaskThread<?, ?> asyncTaskThread = (LXAsyncTaskThread<?, ?>) thread;
                if (asyncTaskThread.cancelTask(task)) {
                    threadPool.remove(thread);
                    break;
                }
            }
        }
    }

    /*********************
     * 获取当前正在进行的任务
     * @return 任务列表
     */
    public List<LXAsyncTask<?, ?>> getAllTask() {
        List<LXAsyncTask<?, ?>> tasks = new ArrayList<>();
        List<Thread> threads = threadPool.getAllThreads();
        for (Thread thread : threads) {
            if (thread instanceof LXAsyncTaskThread) {
                LXAsyncTaskThread<?, ?> asyncTaskThread = (LXAsyncTaskThread<?, ?>) thread;
                tasks.add(asyncTaskThread.getTask());
            }
        }
        return tasks;
    }
}