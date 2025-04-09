package net.kesgroom.cplt.managers;

import net.kesgroom.cplt.services.PlayerService;
import net.kesgroom.cplt.services.PlayerTimeService;
import net.kesgroom.cplt.services.SessionService;
import net.kesgroom.cplt.utils.Interval;
import net.kesgroom.cplt.utils.Intervals;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class BanIntervalManager {
    private static final PlayerTimeService playerTimeService = new PlayerTimeService();
    private static final PlayerService playerService = new PlayerService();

    private static BanIntervalManager instance;
    private static Runnable task;

    private BanIntervalManager(MinecraftServer server) {
        task = () -> checkSessionsToBan(server);
    }

    public static BanIntervalManager getInstance(MinecraftServer server) {
        if (instance == null) instance = new BanIntervalManager(server);
        return instance;
    }

    private static void checkSessionsToBan(MinecraftServer server) {
        System.out.println("Se ejecuta la validación de baneo");
        List<PlayerTimeManager> playerToBan = playerTimeService.getPlayersToBan();

        System.out.println(playerToBan);
        for (PlayerTimeManager playerTime : playerToBan) {
            String uuid = playerTime.getUuid();
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);

            if (player != null) {
                playerService.banPlayer(uuid);
                player.networkHandler.disconnect(Text.literal("Has excedido el tiempo de juego acumulado. Vuelve mañana"));
            }
        }
    }

    public void startInterval() {
        Intervals.getInstance().addScheduler(
                task,
                0,
                1,
                TimeUnit.MINUTES);
    }

    public void stopInterval() {

    }
}
