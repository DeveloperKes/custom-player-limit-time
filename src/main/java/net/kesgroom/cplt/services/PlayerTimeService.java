package net.kesgroom.cplt.services;

import net.kesgroom.cplt.data.DatabaseManager;
import net.kesgroom.cplt.managers.PlayerTimeManager;
import net.kesgroom.cplt.models.PlayerTime;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PlayerTimeService {

    static PlayerTime playerTimeModel = new PlayerTime();

    public PlayerTimeManager getPlayerTime(String uuid) {
        try (Connection conn = DatabaseManager.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(playerTimeModel.getPlayerTime(uuid))) {
            if (rs.next()) {
                PlayerTimeManager playerTimeManager = new PlayerTimeManager();
                playerTimeManager.setRemaining_time(rs.getInt("remaining_time"));
                playerTimeManager.setUuid(uuid);
                return playerTimeManager;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load config", e);
        }
    }

    public void createPlayerTime(String uuid) {
        try (Connection conn = DatabaseManager.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(playerTimeModel.saveNewPlayerTime(uuid));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load config", e);
        }
    }

    public void updatePlayerTime(String uuid, long remaining) {
        try (Connection conn = DatabaseManager.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(playerTimeModel.updateCurrentTime(uuid, remaining));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load config", e);
        }
    }

    public void updateLastAccumulation(String uuid) {
        try (Connection conn = DatabaseManager.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(playerTimeModel.updateLastAccumulation(uuid));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load config", e);
        }
    }

    public List<PlayerTimeManager> getPlayersToBan() {
        List<PlayerTimeManager> playersTime = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(playerTimeModel.getPlayersToBan())) {
            while (rs.next()) {
                PlayerTimeManager playerTimeManager = new PlayerTimeManager();
                playerTimeManager.setUuid(rs.getString("uuid"));
                playerTimeManager.setRemaining_time(rs.getInt("remaining_time"));
                playerTimeManager.setName(rs.getString("name"));

                playersTime.add(playerTimeManager);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load config", e);
        }

        return playersTime;
    }

    public List<PlayerTimeManager> getPlayersForAccumulation() {
        List<PlayerTimeManager> playersTime = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(playerTimeModel.getPlayerWithoutAccumulation())) {
            while (rs.next()) {
                PlayerTimeManager playerTimeManager = new PlayerTimeManager();
                playerTimeManager.setUuid(rs.getString("uuid"));
                playerTimeManager.setRemaining_time(rs.getInt("remaining_time"));

                playersTime.add(playerTimeManager);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load config", e);
        }

        return playersTime;
    }

}
