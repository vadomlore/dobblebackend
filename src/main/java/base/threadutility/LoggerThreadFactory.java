package base.threadutility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadFactory;

/**
 * Created by zyl on 2017/7/21.
 */
public class LoggerThreadFactory implements ThreadFactory {

    private static final Logger logger = LoggerFactory.getLogger(LoggerThreadFactory.class);

    private String factoryName;

    public LoggerThreadFactory(String factoryName) {
        this.factoryName = factoryName;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        t.setName(String.format("[Thread=>%s-%s]", this.factoryName, t.getId()));
        t.setUncaughtExceptionHandler(
                (Thread thread, Throwable e) -> {
                    logger.error("{} {} ｛｝", thread.getName(), e.getMessage(), e);
                }
        );
        return t;
    }
}
