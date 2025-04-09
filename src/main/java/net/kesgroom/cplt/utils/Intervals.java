package net.kesgroom.cplt.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Intervals {
    List<ScheduledExecutorService> schedulers = new ArrayList<>();
    ScheduledThreadPoolExecutor schedulers2 = new ScheduledThreadPoolExecutor(4);
    private static Intervals instance;

    private Intervals() {

    }

    public static Intervals getInstance() {
        if (instance == null) instance = new Intervals();
        return instance;
    }

    public void addScheduler(Runnable task, int delay, int period, TimeUnit unit) {
        schedulers2.scheduleWithFixedDelay(task, delay, period, unit);
    }

    public void stopSchedulers() {
        for (ScheduledExecutorService scheduler : schedulers) {
            scheduler.shutdownNow();
        }
    }
}
