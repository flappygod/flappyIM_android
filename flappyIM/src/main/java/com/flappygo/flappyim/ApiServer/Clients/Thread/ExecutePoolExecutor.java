package com.flappygo.flappyim.ApiServer.Clients.Thread;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.*;
import java.util.ArrayList;
import java.util.List;


//自定义线程池执行器
public class ExecutePoolExecutor extends ScheduledThreadPoolExecutor {

    //当前线程列表（线程安全）
    private final ConcurrentHashMap<Long, Thread> activeThreads = new ConcurrentHashMap<>();

    //构造方法
    public ExecutePoolExecutor(int corePoolSize) {
        super(corePoolSize);
    }

    public ExecutePoolExecutor(int corePoolSize, ThreadFactory threadFactory) {
        super(corePoolSize, threadFactory);
    }

    public ExecutePoolExecutor(int corePoolSize, RejectedExecutionHandler handler) {
        super(corePoolSize, handler);
    }

    public ExecutePoolExecutor(int corePoolSize, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, threadFactory, handler);
    }

    //执行任务
    @Override
    public void execute(Runnable task) {
        if (task instanceof Thread) {
            Thread thread = (Thread) task;
            addThreadToMap(thread);
            //包装任务，确保任务完成后移除线程
            Runnable wrappedTask = () -> {
                try {
                    task.run();
                } finally {
                    removeThreadFromMap(thread);
                }
            };
            super.execute(wrappedTask);
        } else {
            super.execute(task);
        }
    }

    //添加线程到线程列表
    private void addThreadToMap(Thread thread) {
        activeThreads.put(thread.getId(), thread);
    }

    //从线程列表中移除线程
    private void removeThreadFromMap(Thread thread) {
        activeThreads.remove(thread.getId());
    }

    //移除任务
    @Override
    public boolean remove(Runnable task) {
        if (task instanceof Thread) {
            Thread thread = (Thread) task;
            removeThreadFromMap(thread);
        }
        return super.remove(task);
    }

    //获取当前所有线程
    public List<Thread> getAllThreads() {
        return new ArrayList<>(activeThreads.values());
    }
}