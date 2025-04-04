package net.kesgroom.cplt.services;

import net.kesgroom.cplt.data.DatabaseManager;
import net.kesgroom.cplt.managers.ConfigManager;
import net.kesgroom.cplt.models.Config;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConfigService {

    Config configModel = new Config();

    public void loadConfig() {
        try (Connection conn = DatabaseManager.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(configModel.getConfig())) {
            if (rs.next()) {
                ConfigManager.getInstance().setMaxTime(rs.getInt("max_time"));
                ConfigManager.getInstance().setActive(rs.getInt("active") == 1);
                ConfigManager.getInstance().setHourReset(rs.getString("time_reset"));
            } else {
                stmt.executeUpdate(configModel.initConfig());
                System.out.println("Config initialized");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load config", e);
        }
    }

    public void starTimer() {
        try (Connection conn = DatabaseManager.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(configModel.updateMode(true));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load config", e);
        }
    }

    public void stopTimer() {
        try (Connection conn = DatabaseManager.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(configModel.updateMode(false));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load config", e);
        }
    }

    public void setMaxTime(long max_time) {
        try (Connection conn = DatabaseManager.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(configModel.updateMaxTime(max_time));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load config", e);
        }
    }


}
