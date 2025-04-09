package net.kesgroom.cplt.events;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.kesgroom.cplt.managers.ConfigManager;
import net.kesgroom.cplt.managers.TimeRemainingManager;

public class TimeRemainingEvent {
    public static void activeIntervalForSessions() {
        ServerWorldEvents.LOAD.register((server, world) -> {
            if (ConfigManager.getInstance().getActive()) {
                TimeRemainingManager.getInstance(server).startInterval();
            }
        });

        ServerWorldEvents.UNLOAD.register((server, serverWorld) -> {
            if (ConfigManager.getInstance().getActive()) {
                TimeRemainingManager.getInstance(server).stopInterval();
            }
        });
    }
}
