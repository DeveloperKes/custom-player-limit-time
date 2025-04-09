package net.kesgroom.cplt.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.kesgroom.cplt.managers.*;
import net.kesgroom.cplt.services.ConfigService;
import net.kesgroom.cplt.services.PlayerService;
import net.kesgroom.cplt.services.PlayerTimeService;
import net.kesgroom.cplt.utils.Alerts;
import net.kesgroom.cplt.utils.Formats;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

public class RegisterCommands {

    private static final ConfigService configService = new ConfigService();
    private static final PlayerService playerService = new PlayerService();
    private static final PlayerTimeService playerTimeService = new PlayerTimeService();

    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("starttimelimit").requires(source -> source.hasPermissionLevel(4)) // Solo para usuarios OP
                .executes(context -> {
                    if (!ConfigManager.getInstance().getActive()) {
                        ServerCommandSource source = context.getSource();
                        ServerWorld world = source.getWorld();
                        configService.starTimer();
                        ConfigManager.getInstance().setActive(true);
                        BanIntervalManager.getInstance(source.getServer()).startInterval();
                        TimeRemainingManager.getInstance(source.getServer()).startInterval();
                        TimeAccumulateManager.getInstance().startInterval();
                        Alerts.titleAllUsers(world, "¡Restricción activada!", null);
                    } else
                        context.getSource().sendFeedback(() -> Text.literal("La restricción de tiempo ya esta activa."), true);
                    return 1;
                }));

        dispatcher.register(CommandManager.literal("stoptimelimit").requires(source -> source.hasPermissionLevel(4)) // Solo para usuarios OP
                .executes(context -> {
                    if (ConfigManager.getInstance().getActive()) {
                        ServerCommandSource source = context.getSource();
                        ServerWorld world = source.getWorld();
                        configService.stopTimer();
                        ConfigManager.getInstance().setActive(false);
                        BanIntervalManager.getInstance(source.getServer()).stopInterval();
                        TimeRemainingManager.getInstance(source.getServer()).stopInterval();
                        TimeAccumulateManager.getInstance().stopInterval();
                        Alerts.titleAllUsers(world, "¡Restricción desactivada!", "Sin límite de tiempo");
                    } else
                        context.getSource().sendFeedback(() -> Text.literal("La restricción de tiempo ya esta inactiva."), true);
                    return 1;
                }));

        dispatcher.register(CommandManager.literal("setmaxplaytime").requires(source -> source.hasPermissionLevel(4)) // Solo para usuarios OP
                .then(CommandManager.argument("millis", LongArgumentType.longArg(0)).executes(context -> {
                    long newMaxPlayTime = LongArgumentType.getLong(context, "millis");
                    if (ConfigManager.getInstance().getMaxTime() != newMaxPlayTime) {
                        ServerCommandSource source = context.getSource();
                        ServerWorld world = source.getWorld();
                        configService.setMaxTime(newMaxPlayTime);
                        ConfigManager.getInstance().setMaxTime(newMaxPlayTime);
                        Alerts.titleAllUsers(world, "Límite reestablecido", String.format("%s por día", Formats.formatRemainingTime(ConfigManager.getInstance().getMaxTime())));
                    } else
                        context.getSource().sendFeedback(() -> Text.literal("Ingresa un límite de tiempo distinto al actual."), true);
                    return 1;
                })));

        dispatcher.register(CommandManager.literal("unbanallplayers").requires(source -> source.hasPermissionLevel(4)) // Solo para usuarios OP
                .executes(context -> {
                    playerService.unbanAllPlayer();
                    return 1;
                }));

        dispatcher.register(CommandManager.literal("banplayer").requires(source -> source.hasPermissionLevel(4)) // Solo para usuarios OP
                .then(CommandManager.argument("playerName", StringArgumentType.string()).executes(context -> {
                    String playerName = context.getArgument("playerName", String.class);
                    PlayerManager playerManager = playerService.getPlayerByName(playerName);
                    if (playerManager != null) {
                        playerService.banPlayer(playerManager.getUuid());
                        context.getSource().sendFeedback(() -> Text.literal(String.format("%s ha sido baneado.", playerName)), true);
                    } else
                        context.getSource().sendFeedback(() -> Text.literal(String.format("El jugador %s no se encuentra registrado.", playerName)), true);
                    return 1;
                })));

        dispatcher.register(CommandManager.literal("unbanplayer").requires(source -> source.hasPermissionLevel(4)) // Solo para usuarios OP
                .then(CommandManager.argument("playerName", StringArgumentType.string()).executes(context -> {
                    String playerName = context.getArgument("playerName", String.class);
                    PlayerManager playerManager = playerService.getPlayerByName(playerName);
                    if (playerManager != null) {
                        playerService.unbanPlayer(playerManager.getUuid());
                        context.getSource().sendFeedback(() -> Text.literal(String.format("%s ha sido desbaneado.", playerName)), true);
                    } else
                        context.getSource().sendFeedback(() -> Text.literal(String.format("El jugador %s no se encuentra registrado.", playerName)), true);
                    return 1;
                })));

        dispatcher.register(CommandManager.literal("resetplayertime").requires(source -> source.hasPermissionLevel(4)) // Solo para usuarios OP
                .then(CommandManager.argument("playerName", StringArgumentType.string()).executes(context -> {
                    String playerName = context.getArgument("playerName", String.class);
                    PlayerManager playerManager = playerService.getPlayerByName(playerName);
                    if (playerManager != null) {
                        playerTimeService.updatePlayerTime(playerManager.getUuid(), ConfigManager.getInstance().getMaxTime());
                        context.getSource().sendFeedback(() -> Text.literal(String.format("El tiempo límite de %s se ha reestablecido a %s", playerName, Formats.formatRemainingTime(ConfigManager.getInstance().getMaxTime()))), true);
                    } else
                        context.getSource().sendFeedback(() -> Text.literal(String.format("El jugador %s no se encuentra registrado.", playerName)), true);
                    return 1;
                })));

        dispatcher.register(CommandManager.literal("getlimittime").requires(source -> source.hasPermissionLevel(0))
                .executes(context -> {
                    String formatted = Formats.formatRemainingTime(ConfigManager.getInstance().getMaxTime());
                    context.getSource().sendFeedback(() -> Text.literal(String.format("El límite de tiempo actual es: %s", formatted)), true);
                    return 1;
                }));

        dispatcher.register(CommandManager.literal("getmytime").requires(source -> source.hasPermissionLevel(0))
                .executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    assert player != null;
                    String uuid = player.getUuid().toString();
                    PlayerTimeManager playerTimeManager = playerTimeService.getPlayerTime(uuid);
                    if (playerTimeManager != null) {
                        long remaining = playerTimeManager.getRemaining_time();
                        String formatted = Formats.formatRemainingTime(remaining);
                        context.getSource().sendFeedback(() -> Text.literal(String.format("Tú tiempo de juego restante es: %s", formatted)), true);
                    }
                    return 1;
                }));
    }
}
