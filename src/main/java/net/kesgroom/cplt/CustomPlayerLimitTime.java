package net.kesgroom.cplt;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import net.kesgroom.cplt.commands.RegisterCommands;
import net.kesgroom.cplt.events.*;
import net.kesgroom.cplt.utils.Intervals;


public class CustomPlayerLimitTime implements ModInitializer {
    @Override
    public void onInitialize() {
        // Se carga la configuración
        ConfigEvent.initializeConfig();
        // Se registran los comandos
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            RegisterCommands.registerCommands(dispatcher);
        });
        // Se inician los eventos
        PlayerEvents.joinEvent();
        PlayerEvents.disconnectEvent();
        TimeRemainingEvent.activeIntervalForSessions();
        TimeAccumulateEvent.activeIntervalForSessions();
        BanEvent.activeIntervalForSessions();

        Runtime.getRuntime().addShutdownHook(new Thread(() ->
                Intervals.getInstance().stopSchedulers()));
    }
}