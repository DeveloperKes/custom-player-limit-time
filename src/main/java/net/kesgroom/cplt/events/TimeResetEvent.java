package net.kesgroom.cplt.events;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.kesgroom.cplt.managers.ConfigManager;
import net.kesgroom.cplt.managers.PlayerTimeManager;
import net.kesgroom.cplt.services.PlayerTimeService;
import net.minecraft.server.MinecraftServer;

import java.time.LocalTime;
import java.util.List;

public class TimeResetEvent {
    private static final String DAILY_RESET_TIME = ConfigManager.getInstance().getHourReset();

    private static final PlayerTimeService playerTimeService = new PlayerTimeService();

    private static void onServerTick(MinecraftServer server) {
        if (server.getTicks() % 24000 == 0) {
            List<PlayerTimeManager> playersTime = playerTimeService.getPlayersForAccumulation();
            if (isAccumulationTime() && !playersTime.isEmpty()) {
                for (PlayerTimeManager player : playersTime) {
                    playerTimeService.updatePlayerTime(player.getUuid(), player.getRemaining_time() + ConfigManager.getInstance().getMaxTime());
                }
            } else return;
        }
    }

    private static boolean isAccumulationTime() {
        return LocalTime.now().isAfter(LocalTime.parse(DAILY_RESET_TIME));
    }

    public static void registerMidnightReset() {
        ServerTickEvents.END_SERVER_TICK.register(TimeResetEvent::onServerTick);
    }
}
