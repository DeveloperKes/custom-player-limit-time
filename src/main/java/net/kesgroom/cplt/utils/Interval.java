package net.kesgroom.cplt.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Interval {
    private ScheduledExecutorService scheduler;
    private Runnable task;

    public Interval() {
    }

    public void startInterval(int delay, int period, TimeUnit unit) {
        scheduler = Executors.newSingleThreadScheduledExecutor();
//        scheduler.scheduleWithFixedDelay(
//                task,
//                delay,
//                period,
//                unit
//        );

    }

    public void stopInterval() {
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
    }

    public void setTask(Runnable task) {
        System.out.println("Se asigna la tarea");
        this.task = task;
    }
}
