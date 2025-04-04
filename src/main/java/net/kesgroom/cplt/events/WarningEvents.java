package net.kesgroom.cplt.events;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.kesgroom.cplt.managers.PlayerTimeManager;
import net.kesgroom.cplt.services.PlayerTimeService;
import net.kesgroom.cplt.utils.Formats;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;


public class WarningEvents {

    private static final PlayerTimeService playerTimeService = new PlayerTimeService();

    private static void onServerTick(MinecraftServer server) {
        if (server.getTicks() % 12000 == 0) {

            List<ServerPlayerEntity> onlinePlayers = server.getPlayerManager().getPlayerList();

            for (ServerPlayerEntity player : onlinePlayers) {
                String uuid = player.getUuid().toString();
                PlayerTimeManager timeManager = playerTimeService.getPlayerTime(uuid);

                if (timeManager != null) {
                    long remainingTime = timeManager.getRemaining_time();
                    if (remainingTime == 300000) {
                        player.sendMessage(Text.literal("¡Atención! Tu tiempo se está agotando: " + Formats.formatRemainingTime(remainingTime)).formatted(Formatting.RED));
                    }
                }
            }
        }
    }

    public static void registerWarningAlert() {
        ServerTickEvents.END_SERVER_TICK.register(WarningEvents::onServerTick);
    }
}
