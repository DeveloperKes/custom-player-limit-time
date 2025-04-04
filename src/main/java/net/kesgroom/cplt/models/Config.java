package net.kesgroom.cplt.models;

import net.kesgroom.cplt.utils.Entity;

public class Config implements Entity {
    @Override
    public String generateTableSQL() {
        return """
                CREATE TABLE IF NOT EXISTS config(
                    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
                    max_time BIGINT DEFAULT 10800000,
                    active TINYINT(1) DEFAULT 0,
                    time_reset VARCHAR(5) DEFAULT '00:00'
                );
                """;
    }

    public String initConfig() {
        return """
                INSERT INTO config (max_time, active) VALUES (10800000, 0);
                """;
    }

    public String getConfig() {
        return """
                SELECT * FROM config WHERE id = 1;
                """;
    }

    public String updateMaxTime(long newTime) {
        return String.format("""
                UPDATE config SET max_time = %d WHERE id = 1;
                """, newTime);
    }

    public String updateMode(boolean active) {
        return String.format("""
                UPDATE config SET active = %d WHERE id = 1;
                """, active ? 1 : 0);
    }
}
