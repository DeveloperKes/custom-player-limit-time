package net.kesgroom.cplt.data;

import net.kesgroom.cplt.utils.Entity;

import java.sql.*;
import java.util.List;


public class DatabaseManager {
    private static Connection connection;

    public static void initialize(List<Class<? extends Entity>> entityClasses) {
        try (Statement stmt = connection.createStatement()) {
            for (Class<? extends Entity> entityClass : entityClasses) {
                Entity entityInstance = entityClass.getDeclaredConstructor().newInstance();
                String sql = entityInstance.generateTableSQL();
                stmt.executeUpdate(sql);
            }
        } catch (SQLException | ReflectiveOperationException e) {
            System.err.println("Error al crear tablas: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/dev",
                    "root",
                    "kelu0801"
            );
        }
        return connection;
    }

    public static void executeUpdateQuery(String query) {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            System.err.println("Error al crear tablas: " + e.getMessage());
        }
    }

    public static ResultSet executeQuery(String query) {
        try (Statement stmt = connection.createStatement()) {
            return stmt.executeQuery(query);
        } catch (SQLException e) {
            System.err.println("Error al crear tablas: " + e.getMessage());
        }
        return null;
    }
}
