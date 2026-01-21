package com.antagonis1.sealoflegacy.database;

import com.antagonis1.sealoflegacy.SealOfLegacy;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

public class DatabaseManager {

    private final SealOfLegacy plugin;
    private Connection connection;

    public DatabaseManager(SealOfLegacy plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        try {
            File dataFolder = new File(plugin.getDataFolder(), "database.db");
            if (!dataFolder.getParentFile().exists()) {
                dataFolder.getParentFile().mkdirs();
            }

            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder.getAbsolutePath());
            createTables();
            plugin.getLogger().info("Database connected successfully.");
        } catch (SQLException | ClassNotFoundException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not connect to SQLite database!", e);
        }
    }

    private void createTables() {
        String sql = """
                    CREATE TABLE IF NOT EXISTS sealed_items (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        item_uuid VARCHAR(36) NOT NULL UNIQUE,
                        owner_uuid VARCHAR(36) NOT NULL,
                        signature VARCHAR(255),
                        sealed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    );
                """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not create tables!", e);
        }
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                initialize();
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not check connection status!", e);
        }
        return connection;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not close database connection!", e);
        }
    }
}
