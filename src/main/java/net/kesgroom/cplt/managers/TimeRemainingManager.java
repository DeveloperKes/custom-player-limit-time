package net.kesgroom.cplt.managers;

import net.kesgroom.cplt.services.PlayerService;
import net.kesgroom.cplt.services.PlayerTimeService;
import net.kesgroom.cplt.services.SessionService;
import net.kesgroom.cplt.utils.Interval;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class TimeRemainingManager {

    private static final SessionService sessionService = new SessionService();
    private static final PlayerTimeService playerTimeService = new PlayerTimeService();

    private static TimeRemainingManager instance;
    private static final Interval interval = new Interval();

    private TimeRemainingManager(MinecraftServer server) {
        Runnable task = () -> checkSessions(server);
        interval.setTask(task);
    }

    public static TimeRemainingManager getInstance(MinecraftServer server) {
        if (instance == null) instance = new TimeRemainingManager(server);
        return instance;
    }

    private static void checkSessions(MinecraftServer server) {
        System.out.println("Se ejecuta la validación de sesiones");
        List<SessionManager> playersConnect = sessionService.getSessionsConnect();

        System.out.println(playersConnect);
        for (SessionManager session : playersConnect) {
            String uuid = session.getUuid();
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);

            PlayerTimeManager timeManager = playerTimeService.getPlayerTime(uuid);

            if (player != null && timeManager != null) {
                long remainingTime = timeManager.getRemaining_time();
                long newRemaining = remainingTime - (System.currentTimeMillis() - session.getLogin_time().getTime());
                timeManager.setRemaining_time(newRemaining);
                playerTimeService.updatePlayerTime(uuid, Math.max(newRemaining, 0));
                sessionService.updateLoginTime(uuid);
            }
        }
    }

    public void startInterval() {
        interval.startInterval(0, 1, TimeUnit.MINUTES);
    }

    public void stopInterval() {
        interval.stopInterval();
    }
}
