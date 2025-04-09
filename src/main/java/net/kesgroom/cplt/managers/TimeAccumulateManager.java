package net.kesgroom.cplt.managers;

import net.kesgroom.cplt.services.PlayerTimeService;

import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimeAccumulateManager {
    private static final PlayerTimeService playerTimeService = new PlayerTimeService();

    private static TimeAccumulateManager instance;
    private final Runnable task;
    private final ScheduledExecutorService scheduler;

    private TimeAccumulateManager() {
        task = TimeAccumulateManager::searchSessions;
        scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public static TimeAccumulateManager getInstance() {
        if (instance == null) instance = new TimeAccumulateManager();
        return instance;
    }

    private static void searchSessions() {
        List<PlayerTimeManager> playersTime = playerTimeService.getPlayersForAccumulation();
        if (isAccumulationTime() && !playersTime.isEmpty()) {
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
        scheduler.scheduleAtFixedRate(
                task,
                0, 40,
                TimeUnit.MINUTES
        );
    }

    public void stopInterval() {
        scheduler.shutdownNow();
    }
}
