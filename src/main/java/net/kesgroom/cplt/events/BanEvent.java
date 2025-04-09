package net.kesgroom.cplt.events;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.kesgroom.cplt.managers.BanIntervalManager;
import net.kesgroom.cplt.managers.ConfigManager;

public class BanEvent {
    public static void activeIntervalForSessions() {
        ServerWorldEvents.LOAD.register((server, world) -> {
            if (ConfigManager.getInstance().getActive()) {
                BanIntervalManager.getInstance(server).startInterval();
            }
        });

//        ServerWorldEvents.UNLOAD.register((server, serverWorld) -> {
//            if (ConfigManager.getInstance().getActive()) {
//                BanIntervalManager.getInstance(server).stopInterval();
//            }
//        });
    }
}
