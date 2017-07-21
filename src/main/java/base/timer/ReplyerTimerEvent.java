package base.timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by zyl on 2017/7/21.
 */
public class ReplyerTimerEvent<V> {

    private static final Logger logger = LoggerFactory.getLogger(ReplyerTimerEvent.class);

    long delay;

    TimeUnit timeUnit;

    ScheduledFuture<V> asyncResult;

    Callable<V> callable;

    public ReplyerTimerEvent(Callable<V> callable, long delay, TimeUnit timeUnit){
        this.callable = callable;
        this.delay = delay;
        this.timeUnit = timeUnit;
    }

}

