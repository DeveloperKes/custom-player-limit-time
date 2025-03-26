package net.kesgroom.cplt;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class CustomPlayerLimitTime implements ModInitializer {

    private static CustomPlayTimeManager manager;

    private static long MAX_PLAY_TIME() {
        return CustomConfigManager.getTimeLimit();
    }

    private static boolean IS_LIMIT_TIME() {
        return CustomConfigManager.getEnabledTimeLimit();
    }

    private static final Map<UUID, ScheduledFuture<?>> playerTimers = new HashMap<>();

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    private static boolean hasResetToday = false;

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            registerCommands(dispatcher);
        });

        manager = new CustomPlayTimeManager();
        manager.loadPlayTimes();
        registerMidnightReset();

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.player;
            UUID playerId = player.getUuid();
            if (IS_LIMIT_TIME()) {
                if (manager.getBanUserByUUID(playerId)) {
                    player.networkHandler.disconnect(Text.literal("Has alcanzado tu límite de tiempo de juego diario. Vuelve mañana."));
                    return; // Salir del evento sin continuar
                }
                CustomPlayerInfo playerSaved = manager.getPlayer(playerId);
                if (playerSaved == null) {
                    CustomPlayerInfo playerInfoTemplate = new CustomPlayerInfo(MAX_PLAY_TIME(), player.getName());
                    manager.saveNewPlayer(playerId, playerInfoTemplate);
                } else {
                    manager.startSession(playerId);
                }

                long remainingTime = manager.getPlayTime(playerId);

                if (remainingTime > 0) {
                    String remainingTimeFormatted = formatRemainingTime(remainingTime);
                    String message = String.format("Te quedán %s.. ¡Aprovecha tu tiempo!", remainingTimeFormatted);
                    player.sendMessage(Text.literal(message).formatted(Formatting.GREEN));
                    if (IS_LIMIT_TIME()) startTimerPlayer(playerId, player);
                }
            } else {
                player.sendMessage(Text.literal("El limitador de tiempo esta apagado").formatted(Formatting.GOLD));
            }
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            if (IS_LIMIT_TIME()) {
                ServerPlayerEntity player = handler.player;
                UUID playerId = player.getUuid();

                stopTimerPlayer(playerId);

                CustomPlayerInfo playerSaved = manager.getPlayer(playerId);
                if (playerSaved != null) {
                    long sessionTime = System.currentTimeMillis() - playerSaved.sessionStartAgo;
                    manager.updatePlayTime(playerId, playerSaved.remainingTime - sessionTime > 0 ? playerSaved.remainingTime - sessionTime : 0);
                }
            }
        });
    }

    private static void startTimerPlayer(UUID playerId, ServerPlayerEntity player) {
        CustomPlayerInfo playerInfo = manager.getPlayer(playerId);
        if (player != null && playerInfo.remainingTime > 0 && !playerInfo.banPlayer) {
            ScheduledFuture<?> timer = playerTimers.get(playerId);
            if (timer == null) {
                ScheduledFuture<?> newTimer = scheduler.schedule(() -> {
                    if (player.networkHandler != null && IS_LIMIT_TIME()) {
                        String timeLimitFormatted = formatTimeLimit(MAX_PLAY_TIME());
                        String message = String.format("Has alcanzado tu límite de tiempo de juego diario %s. Serás desconectado en 30 segundos...", timeLimitFormatted);
                        player.sendMessage(Text.literal(message).formatted(Formatting.RED));
                        // Programar la desconexión después de 30 segundos
                        scheduler.schedule(() -> {
                            if (player.networkHandler != null) {
                                player.networkHandler.disconnect(Text.literal("Límite de tiempo alcanzado. Vuelve mañana."));
                            }
                        }, 30, TimeUnit.SECONDS);

                        manager.updatePlayTime(playerId, 0);
                        manager.banUser(playerId);
                    } else stopTimerPlayer(playerId);
                }, playerInfo.remainingTime, TimeUnit.MILLISECONDS);
                playerTimers.put(playerId, newTimer);
            }
        }
    }

    public static void stopTimerPlayer(UUID playerId) {
        ScheduledFuture<?> timer = playerTimers.get(playerId);
        if (timer != null) {
            timer.cancel(false); // Cancela el timer
            playerTimers.remove(playerId); // Elimina el timer del mapa
        }
    }

    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("starttimelimit").requires(source -> source.hasPermissionLevel(4)) // Solo para usuarios OP
                .executes(context -> {
                    CustomConfigManager.setTimeLimitEnabled(true);
                    String message = String.format("Límite de tiempo iniciado. El tiempo limite establecido es de %s Se debe cerrar las sesiones para aplicar", formatTimeLimit(MAX_PLAY_TIME()));
                    context.getSource().sendFeedback(() -> Text.literal(message), true);
                    return 1;
                }));

        dispatcher.register(CommandManager.literal("stoptimelimit").requires(source -> source.hasPermissionLevel(4)) // Solo para usuarios OP
                .executes(context -> {
                    CustomConfigManager.setTimeLimitEnabled(false);
                    context.getSource().sendFeedback(() -> Text.literal("Límite de tiempo detenido."), true);
                    manager.resetPlayTimes();
                    return 1;
                }));

        dispatcher.register(CommandManager.literal("resetconfig").requires(source -> source.hasPermissionLevel(4)) // Solo para usuarios OP
                .executes(context -> {
                    CustomConfigManager.resetConfig();
                    context.getSource().sendFeedback(() -> Text.literal("Se ha reiniciado la configuración. Tiempo limite de 3 horas."), true);
                    return 1;
                }));

        dispatcher.register(CommandManager.literal("resetplayerstime").requires(source -> source.hasPermissionLevel(4)) // Solo para usuarios OP
                .executes(context -> {
                    manager.resetPlayTimes();
                    context.getSource().sendFeedback(() -> Text.literal("Se ha reiniciado el tiempo de juego para todos los usuarios."), true);
                    return 1;
                }));

        dispatcher.register(CommandManager.literal("setmaxplaytime").requires(source -> source.hasPermissionLevel(4)) // Solo para usuarios OP
                .then(CommandManager.argument("millis", LongArgumentType.longArg(0)).executes(context -> {
                    long newMaxPlayTime = LongArgumentType.getLong(context, "millis");
                    CustomConfigManager.setMaxPlayTime(newMaxPlayTime);
                    String timeFormatted = formatTimeLimit(newMaxPlayTime);
                    String message = String.format("Tiempo máximo de juego actualizado a %s Se debe cerrar las sesiones para aplicar.", timeFormatted);
                    context.getSource().sendFeedback(() -> Text.literal(message), true);
                    manager.resetPlayTimes();
                    return 1;
                })));

        dispatcher.register(CommandManager.literal("resetplayertime")
                .requires(source -> source.hasPermissionLevel(4)) // Solo para usuarios OP
                .then(CommandManager.argument("playerName", StringArgumentType.string())
                        .executes(context -> {
                            String playerName = context.getArgument("playerName", String.class);
                            boolean response = manager.resetPlayerByPlayerName(playerName);
                            String message;
                            if (response) {
                                message = String.format("Al jugador %s se le ha reiniciado su tiempo limite a %s", playerName, formatTimeLimit(MAX_PLAY_TIME()));
                            } else {
                                message = String.format("No se ha encontrado al jugador %s", playerName);
                            }
                            context.getSource().sendFeedback(() -> Text.literal(message), true);
                            return 1;
                        })
                )
        );
    }

    public static String formatRemainingTime(long remainingTime) {
        long hours = remainingTime / 3600000; // 1 hora = 3600000 ms
        long minutes = (remainingTime % 3600000) / 60000; // 1 minuto = 60000 ms
        long seconds = (remainingTime % 60000) / 1000; // 1 segundo = 1000 ms

        if (hours > 0) {
            return String.format("%d horas y %d minutos.", hours, minutes);
        } else if (minutes > 0) {
            return String.format("%d minutos y %d segundos.", minutes, seconds);
        } else {
            return String.format("%d segundos.", seconds);
        }
    }

    public static String formatTimeLimit(long timeInMillis) {
        long hours = timeInMillis / 3600000; // 1 hora = 3600000 ms
        long minutes = (timeInMillis % 3600000) / 60000; // 1 minuto = 60000 ms
        long seconds = (timeInMillis % 60000) / 1000; // 1 segundo = 1000 ms

        if (hours > 0) {
            return String.format("%d horas, %d minutos y %d segundos", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%d minutos y %d segundos", minutes, seconds);
        } else {
            return String.format("%d segundos", seconds);
        }
    }

    public static void registerMidnightReset() {
        ServerTickEvents.END_SERVER_TICK.register(CustomPlayerLimitTime::onServerTick);
    }


    private static void onServerTick(MinecraftServer server) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        if (hour == 0 && minute == 0) {
            if (!hasResetToday) {
                manager.resetPlayTimes();
                hasResetToday = true;
            }
        } else {
            hasResetToday = false;
        }
    }

}