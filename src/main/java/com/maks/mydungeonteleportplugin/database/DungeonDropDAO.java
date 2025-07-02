package com.maks.mydungeonteleportplugin.database;

import com.maks.mydungeonteleportplugin.MyDungeonTeleportPlugin;
import com.maks.mydungeonteleportplugin.gui.DropPreviewGUI;
import com.maks.mydungeonteleportplugin.gui.FlexibleDropPreviewGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for dungeon drop GUIs
 */
public class DungeonDropDAO {
    private final MyDungeonTeleportPlugin plugin;
    private final DatabaseManager databaseManager;

    // Default inventory size for drop preview GUIs (3 rows)
    private static final int DEFAULT_GUI_SIZE = 27;

    // Size for compact GUI (1 row)
    private static final int COMPACT_GUI_SIZE = 9;

    public DungeonDropDAO(MyDungeonTeleportPlugin plugin, DatabaseManager databaseManager) {
        this.plugin = plugin;
        this.databaseManager = databaseManager;
    }

    /**
     * Save or update a drop GUI for a dungeon
     * @param dungeonKey The dungeon key (e.g., "q1_inf")
     * @param inventory The inventory containing the drops
     */
    public void saveDropGui(String dungeonKey, Inventory inventory) {
        String guiTitle = DungeonKeyUtils.getDropPreviewTitle(dungeonKey);
        String serializedInventory = ItemSerializer.inventoryToBase64(inventory);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String sql = "INSERT INTO dungeon_drop_guis (dungeon_key, gui_title, serialized_inventory) " +
                    "VALUES (?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE gui_title = ?, serialized_inventory = ?";

            try (Connection conn = databaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, dungeonKey);
                stmt.setString(2, guiTitle);
                stmt.setString(3, serializedInventory);
                stmt.setString(4, guiTitle);
                stmt.setString(5, serializedInventory);

                stmt.executeUpdate();

                plugin.getLogger().info("Saved drop GUI for dungeon " + dungeonKey);
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to save drop GUI: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Get a drop GUI for a dungeon
     * @param dungeonKey The dungeon key (e.g., "q1_inf")
     * @return The inventory containing the drops, or null if not found
     */
    public Inventory getDropGui(String dungeonKey) {
        String sql = "SELECT gui_title, serialized_inventory FROM dungeon_drop_guis WHERE dungeon_key = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, dungeonKey);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String guiTitle = rs.getString("gui_title");
                String serializedInventory = rs.getString("serialized_inventory");

                return ItemSerializer.inventoryFromBase64(serializedInventory, guiTitle, DEFAULT_GUI_SIZE);
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to get drop GUI: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Check if a drop GUI exists for a dungeon
     * @param dungeonKey The dungeon key (e.g., "q1_inf")
     * @return True if a drop GUI exists, false otherwise
     */
    public boolean hasDropGui(String dungeonKey) {
        String sql = "SELECT 1 FROM dungeon_drop_guis WHERE dungeon_key = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, dungeonKey);

            ResultSet rs = stmt.executeQuery();

            return rs.next();
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to check if drop GUI exists: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Delete a drop GUI for a dungeon
     * @param dungeonKey The dungeon key (e.g., "q1_inf")
     */
    public void deleteDropGui(String dungeonKey) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String sql = "DELETE FROM dungeon_drop_guis WHERE dungeon_key = ?";

            try (Connection conn = databaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, dungeonKey);
                stmt.executeUpdate();

                plugin.getLogger().info("Deleted drop GUI for dungeon " + dungeonKey);
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to delete drop GUI: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Save or update a drop GUI using a list of items
     * @param dungeonKey The dungeon key (e.g., "q1_inf")
     * @param drops List of ItemStacks to display
     */
    public void saveDropItems(String dungeonKey, List<ItemStack> drops) {
        String guiTitle = DungeonKeyUtils.getDropPreviewTitle(dungeonKey);
        String serializedDrops = ItemSerializer.itemListToBase64(drops);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String sql = "INSERT INTO dungeon_drop_guis (dungeon_key, gui_title, serialized_inventory) " +
                    "VALUES (?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE gui_title = ?, serialized_inventory = ?";

            try (Connection conn = databaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, dungeonKey);
                stmt.setString(2, guiTitle);
                stmt.setString(3, serializedDrops);
                stmt.setString(4, guiTitle);
                stmt.setString(5, serializedDrops);

                stmt.executeUpdate();

                plugin.getLogger().info("Saved drop items for dungeon " + dungeonKey);
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to save drop items: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Get drops for a dungeon as a list of ItemStacks
     * @param dungeonKey The dungeon key (e.g., "q1_inf")
     * @return List of ItemStacks, or empty list if not found
     */
    public List<ItemStack> getDropItems(String dungeonKey) {
        String sql = "SELECT serialized_inventory FROM dungeon_drop_guis WHERE dungeon_key = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, dungeonKey);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String serializedDrops = rs.getString("serialized_inventory");

                // Try to deserialize as item list first
                try {
                    return ItemSerializer.itemListFromBase64(serializedDrops);
                } catch (Exception e) {
                    // If that fails, it might be an old format inventory
                    plugin.getLogger().info("Converting old format inventory to item list for " + dungeonKey);
                    Inventory oldInventory = getDropGui(dungeonKey);
                    if (oldInventory != null) {
                        List<ItemStack> items = new ArrayList<>();
                        for (ItemStack item : oldInventory.getContents()) {
                            if (item != null && item.getType() != Material.AIR) {
                                items.add(item.clone());
                            }
                        }
                        return items;
                    }
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to get drop items: " + e.getMessage());
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    /**
     * Get a DropPreviewGUI for a dungeon
     * @param dungeonKey The dungeon key (e.g., "q1_inf")
     * @return DropPreviewGUI instance, or null if not found
     */
    public DropPreviewGUI getDropPreviewGui(String dungeonKey) {
        List<ItemStack> drops = getDropItems(dungeonKey);
        if (!drops.isEmpty()) {
            return new DropPreviewGUI(plugin, dungeonKey, drops);
        }
        return null;
    }

    /**
     * Get a FlexibleDropPreviewGUI for a dungeon
     * @param dungeonKey The dungeon key (e.g., "q1_inf")
     * @return FlexibleDropPreviewGUI instance, or null if not found
     */
    public FlexibleDropPreviewGUI getFlexibleDropPreviewGui(String dungeonKey) {
        Inventory savedInventory = getDropGui(dungeonKey);
        if (savedInventory != null) {
            return new FlexibleDropPreviewGUI(plugin, dungeonKey, savedInventory);
        }
        return null;
    }

    /**
     * Open drop preview GUI for a player
     * @param player The player to show the GUI to
     * @param dungeonKey The dungeon key
     */
    public void openDropPreview(Player player, String dungeonKey) {
        // Try to use the flexible GUI first (3 rows)
        FlexibleDropPreviewGUI flexibleGui = getFlexibleDropPreviewGui(dungeonKey);
        if (flexibleGui != null) {
            flexibleGui.open(player);
            return;
        }

        // Fall back to the compact GUI (1 row)
        DropPreviewGUI compactGui = getDropPreviewGui(dungeonKey);
        if (compactGui != null) {
            compactGui.open(player);
            return;
        }

        // No GUI available
        player.sendMessage("§cNo drop information available for this dungeon.");
    }
}
