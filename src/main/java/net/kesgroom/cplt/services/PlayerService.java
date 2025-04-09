package net.kesgroom.cplt.services;

import net.kesgroom.cplt.data.DatabaseManager;
import net.kesgroom.cplt.managers.PlayerManager;
import net.kesgroom.cplt.models.Player;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PlayerService {

    static Player playerModel = new Player();

    public PlayerManager getPlayer(String uuid) {
        try (Connection conn = DatabaseManager.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(playerModel.getPlayer(uuid))) {
            if (rs.next()) {
                PlayerManager player = new PlayerManager();

                player.setUuid(rs.getString("uuid"));
                player.setName(rs.getString("name"));
                player.setBanned(rs.getInt("banned") == 1);

                return player;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load config", e);
        }
    }

    public PlayerManager getPlayerByName(String name) {
        try (Connection conn = DatabaseManager.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(playerModel.getPlayerByName(name))) {
            if (rs.next()) {
                PlayerManager player = new PlayerManager();

                player.setUuid(rs.getString("uuid"));
                player.setName(rs.getString("name"));
                player.setBanned(rs.getInt("banned") == 1);

                return player;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load config", e);
        }
    }

    public int registerPlayer(String uuid, String name) {
        try (Connection conn = DatabaseManager.getConnection(); Statement stmt = conn.createStatement()) {
            return stmt.executeUpdate(playerModel.saveNewPlayer(uuid, name));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load config", e);
        }
    }

    public void banPlayer(String uuid) {
        try (Connection conn = DatabaseManager.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(playerModel.banPlayer(uuid));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load config", e);
        }
    }

    public void unbanPlayer(String uuid) {
        try (Connection conn = DatabaseManager.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(playerModel.unbanPlayer(uuid));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load config", e);
        }
    }

    public void unbanAllPlayer() {
        try (Connection conn = DatabaseManager.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(playerModel.unbanAllPlayers());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load config", e);
        }
    }

    public void updateLastConnection(String uuid) {
        try (Connection conn = DatabaseManager.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(playerModel.updateLastConnection(uuid));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load config", e);
        }
    }

    public void freezePlayer(ServerPlayerEntity player, boolean freeze) {
        EntityAttributeInstance movementSpeed = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (movementSpeed != null) {
            movementSpeed.setBaseValue(freeze ? 0.0 : 0.1);
        }
    }

}
