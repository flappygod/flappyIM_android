package com.flappygo.flappyim.ApiServer.Clients.Thread;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/******
 * 自定义线程池
 */
public class ExecutePoolExecutor extends ScheduledThreadPoolExecutor {

    /******
     * 线程map
     */
    private final ConcurrentHashMap<String, Thread> threadMap = new ConcurrentHashMap<>();

    /******
     * 构造器
     * @param corePoolSize 核心大小
     */
    public ExecutePoolExecutor(int corePoolSize) {
        super(corePoolSize);
    }

    /******
     * 构造器
     * @param corePoolSize 核心大小
     * @param handler 拒绝执行处理器
     */
    public ExecutePoolExecutor(int corePoolSize, RejectedExecutionHandler handler) {
        super(corePoolSize, handler);
    }

    /******
     * 构造器
     * @param corePoolSize 核心大小
     * @param threadFactory 线程工厂
     */
    public ExecutePoolExecutor(int corePoolSize, ThreadFactory threadFactory) {
        super(corePoolSize, threadFactory);
    }

    /******
     * 构造器
     * @param corePoolSize 核心大小
     * @param threadFactory 线程工厂
     * @param handler 拒绝执行处理器
     */
    public ExecutePoolExecutor(int corePoolSize, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, threadFactory, handler);
    }

    /******
     * 执行某个线程
     * @param thread 线程
     */
    public void execute(Thread thread) {
        addThreadToMap(thread);
        ThreadInterceptor interceptor = new ThreadInterceptor();
        interceptor.setTarget(thread);
        interceptor.setThreadListener(new ThreadInterceptor.ThreadListener() {
            @Override
            public void death(Thread thread) {
                removeThreadFromMap(thread);
            }

            @Override
            public void began(Thread thread) {
                //No-op
            }
        });

        Object proxy = Proxy.newProxyInstance(
                interceptor.getClass().getClassLoader(),
                new Class<?>[]{Runnable.class},
                interceptor
        );

        super.execute((Runnable) proxy);
    }

    /******
     * 添加线程到Map中
     * @param thread 线程
     */
    private void addThreadToMap(Thread thread) {
        synchronized (threadMap) {
            threadMap.put(Long.toString(thread.getId()), thread);
        }
    }

    /******
     * 从Map中移除线程
     * @param thread 线程
     */
    private void removeThreadFromMap(Thread thread) {
        synchronized (threadMap) {
            threadMap.remove(Long.toString(thread.getId()));
        }
    }

    @Override
    public boolean remove(Runnable task) {
        if (task instanceof Thread) {
            removeThreadFromMap((Thread) task);
        }
        return super.remove(task);
    }

    /***********
     * 获取当前所有的线程
     * @return 线程列表
     */
    public List<Thread> getAllThreads() {
        synchronized (threadMap) {
            return new ArrayList<>(threadMap.values());
        }
    }
}