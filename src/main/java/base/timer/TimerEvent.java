package base.timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by zyl on 2017/7/21.
 */


public class TimerEvent {

    private static final Logger logger = LoggerFactory.getLogger(TimerEvent.class);

    private String name;

    Runnable runnable;

    long delay;

    long period;

    //<0 执行无数次
    //>0 执行n此
    int repeat;

    TimeUnit timeUnit;

    EventTriggerMode triggerMode;


    public static final int MAX_REPEAT_COUNT = 100;

    /**
     * 注意限制repeat次数， 最好不要超过100次
     * @param name
     * @param runnable
     * @param initDelay
     * @param period
     * @param timeUnit
     * @param repeat
     */
    public TimerEvent(String name, Runnable runnable, long initDelay, long period,  int repeat, TimeUnit timeUnit){
        if(repeat == 0){
            logger.warn("TimerEvent:[{}] set repeat count to 0 will execute nothing.", name);
        }
        if(repeat > MAX_REPEAT_COUNT){
            logger.warn("TimerEvent:[{}] repeat amount exceed MAX_REPEAT_COUNT.", name);
        }
        this.name = name;
        this.runnable = runnable;
        this.delay = initDelay;
        this.period = period;
        this.repeat = repeat;
        this.timeUnit = timeUnit;

        if(repeat < 0){
            triggerMode = EventTriggerMode.Infinite;
        }
        else if(repeat == 0){
            triggerMode = EventTriggerMode.DoNothing;
        }
        else if(repeat == 1){
            triggerMode = EventTriggerMode.TriggerOnce;
        }
        else{
            triggerMode = EventTriggerMode.CountDown;
        }
    }


    public TimerEvent(Runnable runnable, long initDelay, long period,  int repeat, TimeUnit timeUnit){
        this("", runnable, initDelay, period, repeat, timeUnit);
    }

    public TimerEvent(Runnable runnable, long delay, TimeUnit timeUnit){
        this("", runnable, delay, 0L, 1, timeUnit);
    }

    public EventTriggerMode getTriggerMode(){
        return this.triggerMode;
    }

}
