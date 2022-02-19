package com.example.appstaticutil.threadpool;


import java.util.concurrent.*;

public class ThreadPoolManager {

    private static volatile ThreadPoolManager singleInstance;

    private final ExecutorService executor;

    private ThreadPoolManager() {
        executor = new ThreadPoolExecutor(0, 19999, 60L, TimeUnit.SECONDS, new SynchronousQueue<>());
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


}
