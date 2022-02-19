package com.example.apputil.ons.kafka;

import org.springframework.beans.factory.annotation.Value;
import java.util.concurrent.*;

public class KafkaThreadPool {

    private static volatile KafkaThreadPool singleInstance;

    private final ExecutorService executor;

    @Value("${mq.threadNumber:8}")
    private Integer threadNumber;

    private KafkaThreadPool() {
        executor = new ThreadPoolExecutor(threadNumber, threadNumber, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(100), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public static KafkaThreadPool getInstance() {
        if (singleInstance == null) {
            synchronized (KafkaThreadPool.class) {
                if (singleInstance == null) {
                    singleInstance = new KafkaThreadPool();
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

    public ExecutorService getExecutor() {
        return executor;
    }

}
