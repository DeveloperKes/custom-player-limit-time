package net.kesgroom.cplt.services;

import net.kesgroom.cplt.data.DatabaseManager;
import net.kesgroom.cplt.managers.SessionManager;
import net.kesgroom.cplt.models.Session;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SessionService {

    static Session sessionModel = new Session();

    public SessionManager getSession(String uuid) {
        try (Connection conn = DatabaseManager.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sessionModel.getSession(uuid))) {
            if (rs.next()) {
                SessionManager sessionManager = new SessionManager();

                sessionManager.setEnded(rs.getInt("ended") == 1);
                sessionManager.setLogin_time(rs.getTimestamp("login_time"));

                return sessionManager;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load config", e);
        }
    }

    public void createSession(String uuid) {
        try (Connection conn = DatabaseManager.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sessionModel.saveNewSession(uuid));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load config", e);
        }
    }

    public int endSession(String uuid) {
        try (Connection conn = DatabaseManager.getConnection(); Statement stmt = conn.createStatement()) {
            return stmt.executeUpdate(sessionModel.endCurrentSession(uuid));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load config", e);
        }
    }

    public void updateLoginTime(String uuid) {
        try (Connection conn = DatabaseManager.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sessionModel.updateCurrentSession(uuid));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load config", e);
        }
    }
}
