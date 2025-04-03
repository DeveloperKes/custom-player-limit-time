package net.kesgroom.cplt.orm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class DatabaseManager {
    private static Connection connection;

    public static void initialize(List<Class<? extends Entity>> entities) {
        try (Statement stmt = connection.createStatement()) {
            for (Class<? extends Entity> entity : entities) {
                String sql = TableGenerator.generateCreateTable(entity);
                System.out.println(sql);
                stmt.executeUpdate(sql);
            }
        } catch (SQLException e) {
            System.err.printf("Error al crear tablas: %s%n", e.getMessage());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/dev",
                    "root",
                    "kelu0801"
            );
        }
    }
}
