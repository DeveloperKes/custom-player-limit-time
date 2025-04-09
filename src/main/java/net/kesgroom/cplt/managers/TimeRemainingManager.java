package net.kesgroom.cplt.managers;

import net.kesgroom.cplt.services.PlayerTimeService;
import net.kesgroom.cplt.services.SessionService;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimeRemainingManager {

    private static final SessionService sessionService = new SessionService();
    private static final PlayerTimeService playerTimeService = new PlayerTimeService();

    private static TimeRemainingManager instance;
    private final ScheduledExecutorService scheduler;
    private final Runnable task;

    private TimeRemainingManager(MinecraftServer server) {
        task = () -> checkSessions(server);
        scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public static TimeRemainingManager getInstance(MinecraftServer server) {
        if (instance == null) instance = new TimeRemainingManager(server);
        return instance;
    }

    private static void checkSessions(MinecraftServer server) {
        List<SessionManager> playersConnect = sessionService.getSessionsConnect();

        for (SessionManager session : playersConnect) {
            String uuid = session.getUuid();
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);

            PlayerTimeManager timeManager = playerTimeService.getPlayerTime(uuid);

            assert player != null;
            assert timeManager != null;

            long remainingTime = timeManager.getRemaining_time();
            long newRemaining = remainingTime - (System.currentTimeMillis() - session.getLogin_time().getTime());
            timeManager.setRemaining_time(newRemaining);
            playerTimeService.updatePlayerTime(uuid, Math.max(newRemaining, 0));
            sessionService.updateLoginTime(uuid);
        }
    }

    public void startInterval() {
        scheduler.scheduleAtFixedRate(
                task,
                0,
                1,
                TimeUnit.MINUTES
        );
    }

    public void stopInterval() {
        scheduler.shutdownNow();
    }
}
