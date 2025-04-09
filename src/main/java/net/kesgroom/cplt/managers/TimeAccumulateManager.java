package net.kesgroom.cplt.managers;

import net.kesgroom.cplt.services.PlayerTimeService;
import net.kesgroom.cplt.utils.Interval;

import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TimeAccumulateManager {
    private static final PlayerTimeService playerTimeService = new PlayerTimeService();

    private static TimeAccumulateManager instance;
    private static final Interval interval = new Interval();

    private TimeAccumulateManager() {
        Runnable task = TimeAccumulateManager::searchSessions;
        interval.setTask(task);
    }

    public static TimeAccumulateManager getInstance() {
        if (instance == null) instance = new TimeAccumulateManager();
        return instance;
    }

    private static void searchSessions() {
        List<PlayerTimeManager> playersTime = playerTimeService.getPlayersForAccumulation();
        if (isAccumulationTime() && !playersTime.isEmpty()) {
            System.out.println("Se ejecuta el aumento");
            for (PlayerTimeManager player : playersTime) {
                playerTimeService.updatePlayerTime(player.getUuid(), player.getRemaining_time() + ConfigManager.getInstance().getMaxTime());
                playerTimeService.updateLastAccumulation(player.getUuid());
            }
        }
    }

    private static boolean isAccumulationTime() {
        return LocalTime.now().isAfter(LocalTime.parse(ConfigManager.getInstance().getHourReset()));
    }

    public void startInterval() {
        interval.startInterval(20, 20, TimeUnit.MINUTES);
    }

    public void stopInterval() {
        interval.stopInterval();
    }
}
