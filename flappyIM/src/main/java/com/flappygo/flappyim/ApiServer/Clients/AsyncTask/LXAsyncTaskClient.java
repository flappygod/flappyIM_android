package com.flappygo.flappyim.ApiServer.Clients.AsyncTask;

import com.flappygo.flappyim.ApiServer.Clients.Thread.ExecutePoolExecutor;
import android.content.Context;
import java.util.ArrayList;
import java.util.List;


/***************
 * 异步线程执行
 * @author lijunlin
 */
public class LXAsyncTaskClient {

    /******
     * 线程池
     */
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
    public void execute(LXAsyncTask<?,?> task) {
        execute(task, null);
    }

    /*****
     * 执行某个异步线程
     * @param task 异步任务
     * @param taskTag  线程taskTag
     */
    public void execute(LXAsyncTask<?,?> task, String taskTag) {
        execute(task, null, taskTag);
    }

    /******
     * 执行某个异步线程
     * @param task     异步任务
     * @param taskInput 传入的参数
     * @param taskTag      线程taskTag
     */
    public void execute(LXAsyncTask<?,?> task, Object taskInput, String taskTag) {
        execute(task, taskInput, taskTag, null);
    }

    /******
     * 执行某个异步线程
     * @param task       异步任务
     * @param taskInput  传入的参数
     * @param context    线程的归属
     * @param taskTag    线程taskTag
     */
    public void execute(LXAsyncTask<?,?> task, Object taskInput, String taskTag, Context context) {
        //创建一个线程
        LXAsyncTaskThread<?,?> thread = new LXAsyncTaskThread<>(context, taskInput, taskTag, task);
        // 执行线程
        threadPool.execute(thread);
    }

    /*******
     * 禁止当前的所有线程执行回调操作
     */
    @SuppressWarnings("unchecked")
    public void cancelAllTask() {
        //线程池中取出所有的线程
        List<Thread> threads = threadPool.getAllThread();
        //反向
        for (int s = threads.size() - 1; s >= 0; s--) {
            //获取
            Thread thread = threads.get(s);
            // 下载的线程
            if (thread instanceof LXAsyncTaskThread) {
                // 不再执行回调了
                ((LXAsyncTaskThread<?,?>) thread).setCallBackEnable(false);
                // 从当前的任务中移除这个线程
                threadPool.remove(thread);
            }
        }
    }

    /******
     * 通过线程的taskTag关闭线程
     * @param taskTag 线程tag
     */
    @SuppressWarnings("unchecked")
    public void cancelTaskByTaskTag(String taskTag) {
        //线程池中取出所有的线程
        List<Thread> threads = threadPool.getAllThread();
        //反向
        for (int s = threads.size() - 1; s >= 0; s--) {
            //获取
            Thread thread = threads.get(s);
            // 下载的线程
            if (thread instanceof LXAsyncTaskThread) {
                String men = ((LXAsyncTaskThread<?,?>) thread).getTaskTag();
                if (men != null && men.equals(taskTag)) {
                    // 不再执行回调了
                    ((LXAsyncTaskThread<?,?>) thread).setCallBackEnable(false);
                    // 从当前的任务中移除这个线程
                    threadPool.remove(thread);
                }
            }
        }
    }


    /******
     * 通过上下文关闭
     * @param context 上下文，这里我们使用了hashCode
     */
    @SuppressWarnings("unchecked")
    public void cancelTask(Context context) {
        //获取所有线程
        List<Thread> threads = threadPool.getAllThread();
        //进行遍历
        for (int s = threads.size() - 1; s >= 0; s--) {
            Thread thread = threads.get(s);
            // 下载的线程
            if (thread instanceof LXAsyncTaskThread) {
                Context men = ((LXAsyncTaskThread<?,?>) thread).getTaskContext();
                if (men == context) {
                    // 不再执行回调了
                    ((LXAsyncTaskThread<?,?>) thread).setCallBackEnable(false);
                    // 从当前的任务中移除这个线程
                    threadPool.remove(thread);
                }
            }
        }
    }

    /******
     * 撤销某个任务
     * @param task 某个任务
     */
    @SuppressWarnings("unchecked")
    public void cancelTask(LXAsyncTask<?,?> task) {
        // 获取所有线程
        List<Thread> threads = threadPool.getAllThread();
        //进行遍历
        for (int s = threads.size() - 1; s >= 0; s--) {
            Thread thread = threads.get(s);
            // 下载的线程
            if (thread instanceof LXAsyncTaskThread) {
                // 判断是否移除成功
                boolean isRemoved = ((LXAsyncTaskThread<?,?>) thread).cancelTask(task);
                if (isRemoved) {
                    // 从当前的线程池中移除这个任务
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
    @SuppressWarnings("unchecked")
    public List<LXAsyncTask<?,?>> getAllTask() {
        List<LXAsyncTask<?,?>> tasks = new ArrayList<>();
        List<Thread> threads = threadPool.getAllThread();
        for (int s = 0; s < threads.size(); s++) {
            if (threads.get(s) instanceof LXAsyncTaskThread) {
                LXAsyncTaskThread<?,?> men = (LXAsyncTaskThread<?,?>) threads.get(s);
                tasks.add(men.getTask());
            }
        }
        return tasks;
    }

}
