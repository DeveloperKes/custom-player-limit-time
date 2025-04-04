package net.kesgroom.cplt.models;

import net.kesgroom.cplt.managers.ConfigManager;
import net.kesgroom.cplt.utils.Entity;

import java.sql.Date;
import java.time.LocalDate;

public class PlayerTime implements Entity {
    @Override
    public String generateTableSQL() {
        return String.format("""
                    CREATE TABLE IF NOT EXISTS player_time(
                        id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
                        remaining_time BIGINT DEFAULT %d,
                        last_accumulation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        player_uuid VARCHAR(36) NOT NULL,
                        FOREIGN KEY (player_uuid) REFERENCES players(uuid)
                    );
                """, ConfigManager.getInstance().getMaxTime());
    }

    public String getPlayerTime(String uuid) {
        return String.format("""
                SELECT pt.id, pt.remaining_time, pt.last_accumulation_date FROM player_time as pt
                INNER JOIN players p ON pt.player_uuid = p.uuid
                WHERE p.uuid = '%s'
                ORDER BY pt.id DESC
                LIMIT 1
                """, uuid);
    }

    public String getAllPlayersTime() {
        return """
                SELECT pt.id, pt.remaining_time, p.uuid, pt.last_accumulation_date FROM player_time as pt
                INNER JOIN players p ON pt.player_uuid = p.uuid
                ORDER BY pt.id DESC
                """;
    }

    public String getPlayerWithoutAccumulation() {
        LocalDate today = LocalDate.now();
        return String.format("""
                SELECT pt.id, pt.remaining_time, p.uuid FROM player_time as pt
                INNER JOIN players p ON pt.player_uuid = p.uuid
                WHERE last_accumulation_date < '%s'
                ORDER BY pt.id DESC
                """, Date.valueOf(today));
    }

    public String saveNewPlayerTime(String uuid) {
        LocalDate today = LocalDate.now();
        return String.format("""
                INSERT INTO player_time (player_uuid, remaining_time, last_accumulation_date) VALUES ('%s', %d, '%s');
                """, uuid, ConfigManager.getInstance().getMaxTime(), Date.valueOf(today));
    }

    public String updateCurrentTime(String uuid, long time) {
        return String.format("""
                UPDATE player_time SET remaining_time = %d WHERE player_uuid = '%s';
                """, time, uuid);
    }
}
