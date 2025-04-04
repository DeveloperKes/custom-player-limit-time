package net.kesgroom.cplt.models;

import net.kesgroom.cplt.utils.Entity;

public class Player implements Entity {

    @Override
    public String generateTableSQL() {
        return """
                CREATE TABLE IF NOT EXISTS players(
                    uuid VARCHAR(36) PRIMARY KEY,
                    name VARCHAR(255) NOT NULL,
                    banned TINYINT(1) DEFAULT 0,
                    last_connection TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                );
                """;
    }

    public String getPlayer(String uuid) {
        return String.format("""
                SELECT * FROM players WHERE uuid = '%s';
                """, uuid);
    }

    public String getPlayerByName(String name) {
        return String.format("""
                SELECT * FROM players WHERE name = '%s';
                """, name);
    }

    public String saveNewPlayer(String uuid, String name) {
        return String.format("""
                INSERT INTO players (uuid, name) VALUES ('%s', '%s');
                """, uuid, name);
    }

    public String updateLastConnection(String uuid) {
        return String.format("""
                UPDATE players SET last_connection = NOW() WHERE uuid = '%s';
                """, uuid);
    }

    public String banPlayer(String uuid) {
        return String.format("""
                UPDATE players SET banned = 1 WHERE uuid = '%s';
                """, uuid);
    }

    public String unbanPlayer(String uuid) {
        return String.format("""
                UPDATE players SET banned = 0 WHERE uuid = '%s';
                """, uuid);
    }

    public String unbanAllPlayers() {
        return """
                UPDATE players SET banned = 0;
                """;
    }
}
