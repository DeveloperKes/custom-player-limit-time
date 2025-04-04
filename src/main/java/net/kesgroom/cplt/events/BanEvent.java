package net.kesgroom.cplt.events;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.kesgroom.cplt.managers.PlayerTimeManager;
import net.kesgroom.cplt.managers.SessionManager;
import net.kesgroom.cplt.services.PlayerService;
import net.kesgroom.cplt.services.PlayerTimeService;
import net.kesgroom.cplt.services.SessionService;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;

public class BanEvent {
    private static final PlayerTimeService playerTimeService = new PlayerTimeService();
    private static final PlayerService playerService = new PlayerService();
    private static final SessionService sessionService = new SessionService();

    private static void onServerTick(MinecraftServer server) {
        if (server.getTicks() % 1000 == 0) {

            List<ServerPlayerEntity> onlinePlayers = server.getPlayerManager().getPlayerList();
            System.out.println("Se ejecuto un ciclo");

            for (ServerPlayerEntity player : onlinePlayers) {
                String uuid = player.getUuid().toString();
                PlayerTimeManager timeManager = playerTimeService.getPlayerTime(uuid);
                SessionManager sessionManager = sessionService.getSession(uuid);
                if (player.networkHandler != null && timeManager != null && sessionManager != null) {
                    long remainingTime = timeManager.getRemaining_time();
                    long newRemaining = remainingTime - (System.currentTimeMillis() - sessionManager.getLogin_time().getTime());
                    timeManager.setRemaining_time(newRemaining);
                    playerTimeService.updatePlayerTime(uuid, Math.max(newRemaining, 0));
                    sessionService.updateLoginTime(uuid);
                    if (remainingTime <= 0) {
                        playerService.banPlayer(uuid);
                        player.networkHandler.disconnect(Text.literal("Has excedido el tiempo de juego acumulado. Vuelve mañana"));
                    }
                }
            }
        }
    }

    public static void registerBanPlayers() {
        ServerTickEvents.END_SERVER_TICK.register(BanEvent::onServerTick);
    }
}
