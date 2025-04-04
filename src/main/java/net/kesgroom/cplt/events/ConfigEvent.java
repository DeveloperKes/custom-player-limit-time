package net.kesgroom.cplt.events;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.kesgroom.cplt.data.DatabaseManager;
import net.kesgroom.cplt.models.Config;
import net.kesgroom.cplt.models.Player;
import net.kesgroom.cplt.models.PlayerTime;
import net.kesgroom.cplt.models.Session;
import net.kesgroom.cplt.services.ConfigService;
import net.kesgroom.cplt.utils.Entity;

import java.sql.SQLException;
import java.util.List;

public class ConfigEvent {
    public static void initializeConfig() {
        try {
            DatabaseManager.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        List<Class<? extends Entity>> models = List.of(
                Player.class,
                Session.class,
                PlayerTime.class,
                Config.class
        );
        DatabaseManager.initialize(models);

        ConfigService configService = new ConfigService();

        ServerWorldEvents.LOAD.register((server, world) -> {
            configService.loadConfig();
        });
    }
}
