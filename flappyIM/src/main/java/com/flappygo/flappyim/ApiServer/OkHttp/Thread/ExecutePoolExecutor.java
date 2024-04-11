package com.flappygo.flappyim.ApiServer.OkHttp.Thread;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.List;


/******
 * 自定义线程池
 */
public class ExecutePoolExecutor extends ScheduledThreadPoolExecutor {

    /******
     * 现成维护列表
     */
    private final ConcurrentHashMap<String, Thread> lThreads = new ConcurrentHashMap<>();

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
     */
    public ExecutePoolExecutor(int corePoolSize,
                               RejectedExecutionHandler handler) {
        super(corePoolSize, handler);
    }

    /******
     * 构造器
     * @param corePoolSize 核心大小
     */
    public ExecutePoolExecutor(int corePoolSize,
                               ThreadFactory threadFactory) {
        super(corePoolSize, threadFactory);
    }

    /******
     * 构造器
     * @param corePoolSize 核心大小
     */
    public ExecutePoolExecutor(int corePoolSize,
                               ThreadFactory threadFactory,
                               RejectedExecutionHandler handler) {
        super(corePoolSize, threadFactory, handler);
    }


    /******
     * 执行某个线程
     * @param thread 线程
     */
    public void execute(Thread thread) {
        // 添加到保存的hashMap中
        addThreadToHashMap(thread);
        // 动态代理，当线程执行完成后进行移除操作
        ThreadInterceptor transactionInterceptor = new ThreadInterceptor();
        transactionInterceptor.setTarget(thread);
        transactionInterceptor
                .setThreadListener(new ThreadInterceptor.ThreadListener() {
                    @Override
                    public void death(Thread thread) {
                        //线程执行完成的时候自动从列表中移除
                        removeThreadFromHashMap(thread);
                    }

                    @Override
                    public void began(Thread thread) {

                    }
                });
        Class<?> classType = transactionInterceptor.getClass();
        Object userServiceProxy = Proxy.newProxyInstance(
                classType.getClassLoader(), Thread.class.getInterfaces(),
                transactionInterceptor);
        super.execute((Runnable) userServiceProxy);
    }

    /******
     * 添加线程到HashMap中
     * @param thread   线程
     */
    private void addThreadToHashMap(Thread thread) {
        // 加入到当前正在进行的线程中
        synchronized (lThreads) {
            lThreads.put(Long.toString(thread.getId()), thread);
        }
    }

    /******
     * 从hashmap中移除线程
     * @param thread  线程
     */
    private void removeThreadFromHashMap(Thread thread) {
        // 保证原子操作
        String key = Long.toString(((Thread) thread).getId());
        synchronized (lThreads) {
            if (lThreads.containsKey(key)) {
                this.lThreads.remove(key);
            }
        }
    }

    @Override
    public boolean remove(Runnable task) {
        // 从HashMap中移除某个线程
        if (task instanceof Thread) {
            removeThreadFromHashMap((Thread) task);
        }
        return super.remove(task);
    }

    /***********
     * 获取当前所有的线程
     * @return 列表
     */
    public List<Thread> getAllThread() {
        // 加锁进行原子操作
        List<Thread> ret = new ArrayList<>();
        synchronized (lThreads) {
            for (Entry<String, Thread> entry : lThreads.entrySet()) {
                Thread thread = entry.getValue();
                ret.add(thread);
            }
        }
        return ret;
    }

}
