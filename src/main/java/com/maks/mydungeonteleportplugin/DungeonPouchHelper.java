package com.maks.mydungeonteleportplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Helper class to integrate with IngredientPouch API for checking/consuming IPS
 * Does not directly import IngredientPouch classes - uses reflection through Bukkit
 */
public class DungeonPouchHelper {

    private static Object pouchAPI;
    private static boolean apiAvailable = false;
    private static final int debugFlag = 1; // Set to 0 to disable debug

    static {
        initializeAPI();
    }

    /**
     * Initialize the IngredientPouch API using reflection
     */
    private static void initializeAPI() {
        try {
            // Get the IngredientPouch plugin
            Object ingredientPouchPlugin = Bukkit.getPluginManager().getPlugin("IngredientPouchPlugin");
            if (ingredientPouchPlugin != null) {
                // Get the API through reflection
                pouchAPI = ingredientPouchPlugin.getClass().getMethod("getAPI").invoke(ingredientPouchPlugin);
                apiAvailable = true;

                if (debugFlag == 1) {
                    Bukkit.getLogger().info("[DungeonPouch] Successfully connected to IngredientPouch API");
                }
            } else {
                apiAvailable = false;
                if (debugFlag == 1) {
                    Bukkit.getLogger().info("[DungeonPouch] IngredientPouch plugin not found");
                }
            }
        } catch (Exception e) {
            apiAvailable = false;
            Bukkit.getLogger().warning("[DungeonPouch] Failed to connect to IngredientPouch API: " + e.getMessage());
        }
    }

    /**
     * Check if API is available
     */
    public static boolean isAPIAvailable() {
        return apiAvailable;
    }

    /**
     * Check if a player has enough Fragment of Infernal Passage (IPS)
     * Checks both inventory and pouch
     * @param player The player to check
     * @param requiredAmount Number of IPS required
     * @return True if player has enough IPS, false otherwise
     */
    public static boolean hasEnoughIPS(Player player, int requiredAmount) {
        int totalIPS = 0;

        // First check inventory
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.IRON_NUGGET) {
                // Check if it's actually Fragment of Infernal Passage
                if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                    String displayName = item.getItemMeta().getDisplayName();
                    if (displayName.contains("Fragment of Infernal Passage")) {
                        totalIPS += item.getAmount();
                    }
                } else {
                    // If no display name, assume it's IPS (backwards compatibility)
                    totalIPS += item.getAmount();
                }
            }
        }

        // Then check pouch if API is available
        if (apiAvailable) {
            try {
                // Use reflection to call: pouchAPI.getItemQuantity(playerUUID, "ips")
                Object quantity = pouchAPI.getClass()
                        .getMethod("getItemQuantity", String.class, String.class)
                        .invoke(pouchAPI, player.getUniqueId().toString(), "ips");

                int pouchIPS = (Integer) quantity;
                totalIPS += pouchIPS;

                if (debugFlag == 1) {
                    Bukkit.getLogger().info("[DungeonPouch] Player " + player.getName() +
                            " has " + (totalIPS - pouchIPS) + " IPS in inventory and " +
                            pouchIPS + " IPS in pouch. Total: " + totalIPS +
                            ", Required: " + requiredAmount);
                }
            } catch (Exception e) {
                Bukkit.getLogger().warning("[DungeonPouch] Error checking pouch for IPS: " + e.getMessage());
            }
        }

        return totalIPS >= requiredAmount;
    }

    /**
     * Consume the required amount of IPS from player's inventory and/or pouch
     * @param player The player to consume items from
     * @param requiredAmount Amount of IPS to consume
     * @return True if successful, false if not enough items
     */
    public static boolean consumeIPS(Player player, int requiredAmount) {
        if (!hasEnoughIPS(player, requiredAmount)) {
            return false;
        }

        int remaining = requiredAmount;

        // First consume from inventory
        for (int i = 0; i < player.getInventory().getSize() && remaining > 0; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item != null && item.getType() == Material.IRON_NUGGET) {
                // Check if it's actually Fragment of Infernal Passage
                boolean isIPS = false;
                if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                    String displayName = item.getItemMeta().getDisplayName();
                    if (displayName.contains("Fragment of Infernal Passage")) {
                        isIPS = true;
                    }
                } else {
                    isIPS = true; // Backwards compatibility
                }

                if (isIPS) {
                    int amount = item.getAmount();
                    if (amount <= remaining) {
                        // Remove entire stack
                        player.getInventory().setItem(i, null);
                        remaining -= amount;
                    } else {
                        // Remove partial amount
                        item.setAmount(amount - remaining);
                        remaining = 0;
                    }
                }
            }
        }

        // Then consume from pouch if needed
        if (remaining > 0 && apiAvailable) {
            try {
                // Use reflection to call: pouchAPI.updateItemQuantity(playerUUID, "ips", -remaining)
                Object success = pouchAPI.getClass()
                        .getMethod("updateItemQuantity", String.class, String.class, int.class)
                        .invoke(pouchAPI, player.getUniqueId().toString(), "ips", -remaining);

                boolean result = (Boolean) success;
                if (!result) {
                    Bukkit.getLogger().warning("[DungeonPouch] Failed to remove " + remaining + " IPS from pouch for player " + player.getName());
                    return false;
                }

                if (debugFlag == 1) {
                    Bukkit.getLogger().info("[DungeonPouch] Consumed " + remaining + " IPS from pouch for player " + player.getName());
                }
            } catch (Exception e) {
                Bukkit.getLogger().warning("[DungeonPouch] Error consuming IPS from pouch: " + e.getMessage());
                return false;
            }
        }

        return true;
    }
}