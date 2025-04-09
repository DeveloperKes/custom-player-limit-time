package net.kesgroom.cplt.managers;

import net.kesgroom.cplt.services.PlayerService;
import net.kesgroom.cplt.services.PlayerTimeService;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;
import java.util.concurrent.*;

public class BanIntervalManager {
    private static final PlayerTimeService playerTimeService = new PlayerTimeService();
    private static final PlayerService playerService = new PlayerService();

    private final ScheduledExecutorService scheduler;

    private static BanIntervalManager instance;
    private final Runnable task;

    private BanIntervalManager(MinecraftServer server) {
        task = () -> checkSessionsToBan(server);
        scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public static BanIntervalManager getInstance(MinecraftServer server) {
        if (instance == null) instance = new BanIntervalManager(server);
        return instance;
    }

    private void checkSessionsToBan(MinecraftServer server) {
        List<PlayerTimeManager> playerToBan = playerTimeService.getPlayersToBan();
        for (PlayerTimeManager playerTime : playerToBan) {
            String uuid = playerTime.getUuid();
            String name = playerTime.getName();
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(name);
            if (player != null && player.networkHandler != null) {
                playerService.banPlayer(uuid);
                player.networkHandler.disconnect(Text.literal("Has excedido el tiempo de juego acumulado. Vuelve mañana"));
            }
        }
    }

    public void startInterval() {
        scheduler.scheduleAtFixedRate(
                task,
                0,
                2,
                TimeUnit.MINUTES);
    }

    public void stopInterval() {
        scheduler.shutdownNow();
    }
}
