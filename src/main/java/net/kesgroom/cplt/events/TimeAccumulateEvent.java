package net.kesgroom.cplt.events;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.kesgroom.cplt.managers.ConfigManager;
import net.kesgroom.cplt.managers.TimeAccumulateManager;

public class TimeAccumulateEvent {

    public static void activeIntervalForSessions() {
        ServerWorldEvents.LOAD.register((server, world) -> {
            if (ConfigManager.getInstance().getActive()) {
                TimeAccumulateManager.getInstance().startInterval();
            }
        });

        ServerWorldEvents.UNLOAD.register((server, serverWorld) -> {
            if (ConfigManager.getInstance().getActive()) {
                TimeAccumulateManager.getInstance().stopInterval();
            }
        });
    }
}
