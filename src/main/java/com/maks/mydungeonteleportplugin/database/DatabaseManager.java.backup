package com.maks.mydungeonteleportplugin.database;

import com.maks.mydungeonteleportplugin.MyDungeonTeleportPlugin;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseManager {
    private final MyDungeonTeleportPlugin plugin;
    private HikariDataSource dataSource;

    public DatabaseManager(MyDungeonTeleportPlugin plugin) {
        this.plugin = plugin;
        initializeDataSource();
        createTables();
    }

    private void initializeDataSource() {
        FileConfiguration config = plugin.getConfig();
        
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mysql://" + 
                config.getString("database.host") + ":" + 
                config.getString("database.port") + "/" + 
                config.getString("database.name"));
        hikariConfig.setUsername(config.getString("database.user"));
        hikariConfig.setPassword(config.getString("database.password"));
        
        // Connection pool settings
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setMinimumIdle(5);
        hikariConfig.setIdleTimeout(300000); // 5 minutes
        hikariConfig.setMaxLifetime(600000); // 10 minutes
        hikariConfig.setConnectionTimeout(10000); // 10 seconds
        
        // Performance optimizations
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");
        hikariConfig.addDataSourceProperty("useLocalSessionState", "true");
        hikariConfig.addDataSourceProperty("rewriteBatchedStatements", "true");
        hikariConfig.addDataSourceProperty("cacheResultSetMetadata", "true");
        hikariConfig.addDataSourceProperty("cacheServerConfiguration", "true");
        hikariConfig.addDataSourceProperty("elideSetAutoCommits", "true");
        hikariConfig.addDataSourceProperty("maintainTimeStats", "false");
        
        try {
            dataSource = new HikariDataSource(hikariConfig);
            plugin.getLogger().info("Database connection established successfully!");
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to establish database connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createTables() {
        // Create players_dungeon_stats table
        String createStatsTable = "CREATE TABLE IF NOT EXISTS players_dungeon_stats (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "player_uuid VARCHAR(36) NOT NULL, " +
                "dungeon_key VARCHAR(50) NOT NULL, " +
                "entries INT DEFAULT 0, " +
                "completions INT DEFAULT 0, " +
                "fastest_time_seconds INT DEFAULT NULL, " +
                "INDEX idx_player_dungeon (player_uuid, dungeon_key), " +
                "UNIQUE KEY unique_player_dungeon (player_uuid, dungeon_key)" +
                ")";
        
        // Create dungeon_drop_guis table
        String createDropsTable = "CREATE TABLE IF NOT EXISTS dungeon_drop_guis (" +
                "dungeon_key VARCHAR(50) PRIMARY KEY, " +
                "gui_title VARCHAR(255) NOT NULL, " +
                "serialized_inventory TEXT NOT NULL" +
                ")";
        
        // Execute the SQL statements asynchronously
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = getConnection();
                 PreparedStatement statsStmt = connection.prepareStatement(createStatsTable);
                 PreparedStatement dropsStmt = connection.prepareStatement(createDropsTable)) {
                
                statsStmt.executeUpdate();
                dropsStmt.executeUpdate();
                
                plugin.getLogger().info("Database tables created successfully!");
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to create database tables: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            initializeDataSource();
        }
        return dataSource.getConnection();
    }

    public void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            plugin.getLogger().info("Database connection pool closed.");
        }
    }
}