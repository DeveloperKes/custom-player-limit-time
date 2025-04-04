package net.kesgroom.cplt.models;

import net.kesgroom.cplt.utils.Entity;

public class Session implements Entity {


    @Override
    public String generateTableSQL() {
        return """
                    CREATE TABLE IF NOT EXISTS sessions(
                    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
                    login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    ended TINYINT(1) DEFAULT 0,
                    player_uuid VARCHAR(36) NOT NULL,
                    FOREIGN KEY (player_uuid) REFERENCES players(uuid)
                );
                """;
    }

    public String getSession(String uuid) {
        return String.format("""
                SELECT s.id, s.login_time, s.ended FROM sessions as s
                INNER JOIN players p ON s.player_uuid = p.uuid
                WHERE p.uuid = '%s'
                ORDER BY s.id DESC
                LIMIT 1
                """, uuid);
    }

    public String saveNewSession(String uuid) {
        return String.format("""
                INSERT INTO sessions (player_uuid, login_time) VALUES ('%s', CURRENT_TIMESTAMP);
                """, uuid);
    }

    public String updateCurrentSession(String uuid) {
        return String.format("""
                UPDATE sessions SET login_time = CURRENT_TIMESTAMP WHERE player_uuid = '%s';
                """, uuid);
    }

    public String endCurrentSession(String uuid) {
        return String.format("""
                UPDATE sessions SET ended = 1 WHERE player_uuid = '%s';
                """, uuid);
    }
}
