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
                        FOREIGN KEY (player_uuid) REFERENCES players(uuid) ON DELETE CASCADE
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

    public String getPlayersNearBanned() {
        int near_time = Math.max(1, (int) (ConfigManager.getInstance().getMaxTime() * 0.05));
        return String.format("""
                SELECT pt.id, pt.remaining_time, p.uuid, pt.last_accumulation_date, s.connect FROM player_time AS pt
                INNER JOIN players p ON pt.player_uuid = p.uuid
                INNER JOIN sessions s ON pt.player_uuid = s.player_uuid
                WHERE s.connect = 1 AND pt.remaining_time < %d AND pt.remaining_time > 0
                ORDER BY pt.id DESC
                """, near_time);
    }

    public String getPlayersToBan() {
        return """
                SELECT pt.id, pt.remaining_time, p.uuid, pt.last_accumulation_date, s.connect FROM player_time AS pt
                INNER JOIN players p ON pt.player_uuid = p.uuid
                INNER JOIN sessions s ON pt.player_uuid = s.player_uuid
                WHERE s.connect = 1 AND pt.remaining_time == 0
                ORDER BY pt.id DESC
                """;
    }

    public String getPlayerWithoutAccumulation() {
        LocalDate today = LocalDate.now();
        return String.format("""
                SELECT pt.id, pt.remaining_time, p.uuid, pt.last_accumulation_date FROM player_time AS pt
                INNER JOIN players p ON pt.player_uuid = p.uuid
                WHERE pt.last_accumulation_date < '%s'
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

    public String updateLastAccumulation(String uuid) {
        LocalDate today = LocalDate.now();
        return String.format("""
                UPDATE player_time SET last_accumulation_date = '%s' WHERE player_uuid = '%s';
                """, Date.valueOf(today), uuid);
    }


}
