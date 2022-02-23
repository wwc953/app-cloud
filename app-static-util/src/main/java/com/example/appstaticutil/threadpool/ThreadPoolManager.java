package com.example.appstaticutil.threadpool;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolManager {

    private static volatile ThreadPoolManager singleInstance;

    private final ExecutorService executor;

    private ThreadPoolManager() {
        executor = new ThreadPoolExecutor(0, 19999, 60L, TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                ThreadPoolManager.defaultThreadFactory());
    }

    public static ThreadPoolManager getInstance() {
        if (singleInstance == null) {
            synchronized (ThreadPoolManager.class) {
                if (singleInstance == null) {
                    singleInstance = new ThreadPoolManager();
                }
            }
        }
        return singleInstance;
    }

    public void execute(Runnable runnable) {
        executor.execute(runnable);
    }

    public <T> Future<T> submit(Callable<T> task) {
        return executor.submit(task);
    }


    static class AppThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        AppThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = "app-" +
                    poolNumber.getAndIncrement() +
                    "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }

    public static ThreadFactory defaultThreadFactory() {
        return new ThreadPoolManager.AppThreadFactory();
    }

}
