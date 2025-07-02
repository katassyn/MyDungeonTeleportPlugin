package com.maks.mydungeonteleportplugin.database;

import com.maks.mydungeonteleportplugin.MyDungeonTeleportPlugin;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerStatsDAO {
    private final MyDungeonTeleportPlugin plugin;
    private final DatabaseManager databaseManager;

    public PlayerStatsDAO(MyDungeonTeleportPlugin plugin, DatabaseManager databaseManager) {
        this.plugin = plugin;
        this.databaseManager = databaseManager;
    }

    /**
     * Increment the entry count for a player in a specific dungeon
     * @param playerUUID The UUID of the player
     * @param dungeonKey The dungeon key (e.g., "q1_inf")
     */
    public void incrementEntries(UUID playerUUID, String dungeonKey) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String sql = "INSERT INTO players_dungeon_stats (player_uuid, dungeon_key, entries) " +
                    "VALUES (?, ?, 1) " +
                    "ON DUPLICATE KEY UPDATE entries = entries + 1";
            
            try (Connection conn = databaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, playerUUID.toString());
                stmt.setString(2, dungeonKey);
                stmt.executeUpdate();
                
                plugin.getLogger().info("Incremented entry count for player " + playerUUID + " in dungeon " + dungeonKey);
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to increment entry count: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Increment the completion count for a player in a specific dungeon
     * @param playerUUID The UUID of the player
     * @param dungeonKey The dungeon key (e.g., "q1_inf")
     */
    public void incrementCompletions(UUID playerUUID, String dungeonKey) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String sql = "INSERT INTO players_dungeon_stats (player_uuid, dungeon_key, completions) " +
                    "VALUES (?, ?, 1) " +
                    "ON DUPLICATE KEY UPDATE completions = completions + 1";
            
            try (Connection conn = databaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, playerUUID.toString());
                stmt.setString(2, dungeonKey);
                stmt.executeUpdate();
                
                plugin.getLogger().info("Incremented completion count for player " + playerUUID + " in dungeon " + dungeonKey);
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to increment completion count: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Update the fastest completion time for a player in a specific dungeon
     * @param playerUUID The UUID of the player
     * @param dungeonKey The dungeon key (e.g., "q1_inf")
     * @param timeInSeconds The completion time in seconds
     */
    public void updateFastestTime(UUID playerUUID, String dungeonKey, int timeInSeconds) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            // First, check if there's an existing record and if the new time is faster
            String checkSql = "SELECT fastest_time_seconds FROM players_dungeon_stats " +
                    "WHERE player_uuid = ? AND dungeon_key = ?";
            
            try (Connection conn = databaseManager.getConnection();
                 PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                
                checkStmt.setString(1, playerUUID.toString());
                checkStmt.setString(2, dungeonKey);
                
                ResultSet rs = checkStmt.executeQuery();
                
                boolean shouldUpdate = false;
                if (rs.next()) {
                    int currentFastest = rs.getInt("fastest_time_seconds");
                    // Update if current fastest is null (0) or new time is faster
                    if (currentFastest == 0 || timeInSeconds < currentFastest) {
                        shouldUpdate = true;
                    }
                } else {
                    // No record exists, so insert a new one
                    shouldUpdate = true;
                }
                
                if (shouldUpdate) {
                    String updateSql = "INSERT INTO players_dungeon_stats (player_uuid, dungeon_key, fastest_time_seconds) " +
                            "VALUES (?, ?, ?) " +
                            "ON DUPLICATE KEY UPDATE fastest_time_seconds = ?";
                    
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setString(1, playerUUID.toString());
                        updateStmt.setString(2, dungeonKey);
                        updateStmt.setInt(3, timeInSeconds);
                        updateStmt.setInt(4, timeInSeconds);
                        updateStmt.executeUpdate();
                        
                        plugin.getLogger().info("Updated fastest time for player " + playerUUID + 
                                " in dungeon " + dungeonKey + " to " + timeInSeconds + " seconds");
                    }
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to update fastest time: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Get player statistics for a specific dungeon
     * @param playerUUID The UUID of the player
     * @param dungeonKey The dungeon key (e.g., "q1_inf")
     * @return A PlayerDungeonStats object containing the statistics
     */
    public PlayerDungeonStats getPlayerStats(UUID playerUUID, String dungeonKey) {
        String sql = "SELECT entries, completions, fastest_time_seconds FROM players_dungeon_stats " +
                "WHERE player_uuid = ? AND dungeon_key = ?";
        
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, playerUUID.toString());
            stmt.setString(2, dungeonKey);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int entries = rs.getInt("entries");
                int completions = rs.getInt("completions");
                int fastestTime = rs.getInt("fastest_time_seconds");
                
                return new PlayerDungeonStats(playerUUID, dungeonKey, entries, completions, fastestTime);
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to get player stats: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Return default stats if no record exists
        return new PlayerDungeonStats(playerUUID, dungeonKey, 0, 0, 0);
    }

    /**
     * Class to hold player dungeon statistics
     */
    public static class PlayerDungeonStats {
        private final UUID playerUUID;
        private final String dungeonKey;
        private final int entries;
        private final int completions;
        private final int fastestTimeSeconds;

        public PlayerDungeonStats(UUID playerUUID, String dungeonKey, int entries, int completions, int fastestTimeSeconds) {
            this.playerUUID = playerUUID;
            this.dungeonKey = dungeonKey;
            this.entries = entries;
            this.completions = completions;
            this.fastestTimeSeconds = fastestTimeSeconds;
        }

        public UUID getPlayerUUID() {
            return playerUUID;
        }

        public String getDungeonKey() {
            return dungeonKey;
        }

        public int getEntries() {
            return entries;
        }

        public int getCompletions() {
            return completions;
        }

        public int getFastestTimeSeconds() {
            return fastestTimeSeconds;
        }

        /**
         * Format the fastest time as a string (mm:ss)
         * @return The formatted time string
         */
        public String getFormattedFastestTime() {
            if (fastestTimeSeconds == 0) {
                return "N/A";
            }
            
            int minutes = fastestTimeSeconds / 60;
            int seconds = fastestTimeSeconds % 60;
            
            return String.format("%d:%02d", minutes, seconds);
        }
    }
}