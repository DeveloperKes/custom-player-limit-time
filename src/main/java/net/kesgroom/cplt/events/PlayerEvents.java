package net.kesgroom.cplt.events;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.kesgroom.cplt.managers.ConfigManager;
import net.kesgroom.cplt.managers.PlayerManager;
import net.kesgroom.cplt.managers.PlayerTimeManager;
import net.kesgroom.cplt.managers.SessionManager;
import net.kesgroom.cplt.services.PlayerService;
import net.kesgroom.cplt.services.PlayerTimeService;
import net.kesgroom.cplt.services.SessionService;
import net.kesgroom.cplt.utils.Formats;
import net.kesgroom.cplt.utils.TitleHelper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class PlayerEvents {

    private static final PlayerService playerService = new PlayerService();
    private static final SessionService sessionService = new SessionService();
    private static final PlayerTimeService playerTimeService = new PlayerTimeService();

    public static void joinEvent() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.player;
            String uuid = player.getUuid().toString();
            String name = player.getName().getString();
            PlayerManager playerManager = playerService.getPlayer(uuid);
            if (playerManager == null) registerEvent(player);
            else {
                if (ConfigManager.getInstance().getActive()) {
                    if (playerManager.getBanned()) {
                        player.networkHandler.disconnect(Text.literal("Has alcanzado tu límite de tiempo de juego acumulado. Vuelve mañana."));
                        return;
                    }
                    welcomeTitle(uuid, name, player);
                } else {
                    Text title = Text.literal(String.format("Bienvenido, %s", name)).formatted(Formatting.GOLD);
                    Text subtitle = Text.literal("Sin límite de tiempo").formatted(Formatting.GRAY);
                    TitleHelper.sendTitle(player, title, subtitle, 10, 70, 20);
                }
            }
        });
    }

    public static void disconnectEvent() {
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            if (ConfigManager.getInstance().getActive()) {
                ServerPlayerEntity player = handler.player;
                String uuid = player.getUuid().toString();
                PlayerManager playerManager = playerService.getPlayer(uuid);
                if (playerManager != null) {
                    PlayerTimeManager currentTime = playerTimeService.getPlayerTime(uuid);
                    SessionManager currentSession = sessionService.getSession(uuid);

                    if (currentTime != null && currentSession != null) {
                        long sessionTime = System.currentTimeMillis() - currentSession.getLogin_time().getTime();
                        long remaining = currentTime.getRemaining_time() - sessionTime;
                        currentTime.setRemaining_time(remaining);
                        playerTimeService.updatePlayerTime(uuid, Math.max(remaining, 0));
                        if (remaining <= 0) {
                            playerService.banPlayer(uuid);
                        }
                    }
                    playerService.updateLastConnection(uuid);
                }
            }
        });
    }

    private static void registerEvent(ServerPlayerEntity player) {
        Text title = Text.literal("Registrando...").formatted(Formatting.GREEN);
        Text subtitle = Text.literal("No podrás moverte, espera un momento").formatted(Formatting.DARK_GRAY);
        TitleHelper.sendTitle(player, title, subtitle, 10, 50, 20);
        playerService.freezePlayer(player, true);
        int res = playerService.registerPlayer(player.getUuid().toString(), player.getName().getString());
        if (res == 1) {
            playerService.freezePlayer(player, false);
            Text registerTitle = Text.literal("¡Registrado!").formatted(Formatting.GOLD);
            Text registerSubtitle = Text.literal(String.format("Tiempo restante: %s", Formats.formatRemainingTime(ConfigManager.getInstance().getMaxTime()))).formatted(Formatting.GRAY);

            TitleHelper.sendTitle(player, registerTitle, registerSubtitle, 10, 70, 20);
        } else
            player.networkHandler.disconnect(Text.literal("No se ha podido realizar el registro, intente más tarde."));

    }


    private static void welcomeTitle(String uuid, String name, ServerPlayerEntity player) {
        SessionManager currentSession = sessionService.getSession(uuid);
        if (currentSession == null) sessionService.createSession(uuid);
        else sessionService.updateLoginTime(uuid);

        long remaining = ConfigManager.getInstance().getMaxTime();

        PlayerTimeManager currentTime = playerTimeService.getPlayerTime(uuid);
        if (currentTime == null) playerTimeService.createPlayerTime(uuid);
        else remaining = currentTime.getRemaining_time();

        Text title = Text.literal(String.format("Bienvenido, %s", name)).formatted(Formatting.GOLD);
        Text subtitle = Text.literal(String.format("Tiempo restante: %s", Formats.formatRemainingTime(remaining))).formatted(TitleHelper.choiceColorByRemainingTime(remaining));
        TitleHelper.sendTitle(player, title, subtitle, 10, 70, 20);
    }

}
