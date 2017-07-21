package base.timer;

import base.threadutility.LoggerThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.concurrent.*;

/**
 * Created by zyl on 2017/7/21.
 */
public class ScheduledTimerService {
    private static final Logger logger = LoggerFactory.getLogger(ReplyerTimerEvent.class);

    public void execute(TimerEvent event){
        switch (event.triggerMode){
            case DoNothing:
                break;
            case Infinite:
                scheduledServiceExecutor.scheduleAtFixedRate(event.runnable, event.delay, event.period, event.timeUnit);
                break;
            case TriggerOnce:
                scheduledServiceExecutor.schedule(event.runnable, event.delay, event.timeUnit);
                break;
            case CountDown:
                scheduledServiceExecutor.schedule(event.runnable, event.delay, event.timeUnit);
                for(int i = 0; i < event.repeat - 1; i++){
                    long executeDelay = event.delay +  ( i + 1) * event.period;
                    scheduledServiceExecutor.schedule(event.runnable, executeDelay, event.timeUnit);
                }
            default:
                break;
        }
    }

    public void submit(ReplyerTimerEvent replayer){
        replayer.asyncResult = scheduledServiceExecutor.schedule(replayer.callable, replayer.delay, replayer.timeUnit);
    }

    private ScheduledExecutorService scheduledServiceExecutor = Executors.newSingleThreadScheduledExecutor(
            new LoggerThreadFactory("timer-task"));

    private static ScheduledTimerService timerService;


    public static ScheduledTimerService getInstance(){
        return Singleton.INSTANCE.getInstance();
    }
    private enum Singleton {
        INSTANCE;

        Singleton() {
            timerService = new ScheduledTimerService();
        }

        ScheduledTimerService getInstance() {
            return timerService;
        }
    }

    public static void main(String[] args) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("Executing at: " + LocalDateTime.now().toString());
            }
        };
        System.out.println(LocalDateTime.now().toString());
        ScheduledTimerService.getInstance().execute(new TimerEvent("helloEvent", runnable, 1L, 2L,  0, TimeUnit.SECONDS));
    }
}
