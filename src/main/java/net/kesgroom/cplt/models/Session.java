package net.kesgroom.cplt.models;

import net.kesgroom.cplt.utils.Entity;

public class Session implements Entity {


    @Override
    public String generateTableSQL() {
        return """
                    CREATE TABLE IF NOT EXISTS sessions(
                    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
                    login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    connect TINYINT(1) DEFAULT 1,
                    player_uuid VARCHAR(36) NOT NULL,
                    FOREIGN KEY (player_uuid) REFERENCES players(uuid) ON DELETE CASCADE
                );
                """;
    }

    public String getSession(String uuid) {
        return String.format("""
                SELECT p.uuid, s.login_time, s.connect FROM sessions as s
                INNER JOIN players p ON s.player_uuid = p.uuid
                WHERE p.uuid = '%s'
                ORDER BY s.id DESC
                LIMIT 1
                """, uuid);
    }

    public String getSessionsConnect() {
        return """
                SELECT s.id, p.uuid, s.login_time, s.connect FROM sessions as s
                INNER JOIN players p ON s.player_uuid = p.uuid
                INNER JOIN player_time pt ON s.player_uuid = pt.player_uuid
                WHERE s,connect = 1
                ORDER BY s.id DESC
                """;
    }

    public String saveNewSession(String uuid) {
        return String.format("""
                INSERT INTO sessions (player_uuid, login_time) VALUES ('%s', CURRENT_TIMESTAMP);
                """, uuid);
    }

    public String updateCurrentSession(String uuid) {
        return String.format("""
                UPDATE sessions SET login_time = CURRENT_TIMESTAMP, connect = 1 WHERE player_uuid = '%s';
                """, uuid);
    }

    public String endCurrentSession(String uuid) {
        return String.format("""
                UPDATE sessions SET connect = 0 WHERE player_uuid = '%s';
                """, uuid);
    }
}
