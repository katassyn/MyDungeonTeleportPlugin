package com.maks.mydungeonteleportplugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

public class DungeonUtils {

    private static Object pouchAPI;
    private static boolean apiAvailable = false;
    private static Method getItemQuantityMethod;
    private static Method updateItemQuantityMethod;

    static {
        // Initialize API when class is loaded
        Plugin ingredientPouchPlugin = Bukkit.getPluginManager().getPlugin("IngredientPouchPlugin");
        if (ingredientPouchPlugin != null) {
            try {
                Method getAPIMethod = ingredientPouchPlugin.getClass().getMethod("getAPI");
                pouchAPI = getAPIMethod.invoke(ingredientPouchPlugin);

                if (pouchAPI != null) {
                    getItemQuantityMethod = pouchAPI.getClass().getMethod("getItemQuantity", String.class, String.class);
                    updateItemQuantityMethod = pouchAPI.getClass().getMethod("updateItemQuantity", String.class, String.class, int.class);
                    apiAvailable = true;
                    Bukkit.getLogger().info("[MyDungeonTeleportPlugin] Successfully connected to IngredientPouchPlugin API");
                }
            } catch (Exception e) {
                Bukkit.getLogger().warning("[MyDungeonTeleportPlugin] Failed to connect to IngredientPouchPlugin API: " + e.getMessage());
                apiAvailable = false;
            }
        } else {
            Bukkit.getLogger().info("[MyDungeonTeleportPlugin] IngredientPouchPlugin not found, pouch integration disabled");
        }
    }

    /**
     * Check if a player has enough Fragment of Infernal Passage (IPS)
     * @param player The player to check
     * @param requiredNuggets Number of IPS required
     * @return True if player has enough nuggets, false otherwise
     */
    public static boolean hasEnoughNuggets(Player player, int requiredNuggets) {
        // Check inventory first (priority)
        int nuggetCount = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.IRON_NUGGET) {
                nuggetCount += item.getAmount();
            }
        }

        // If inventory has enough, return true
        if (nuggetCount >= requiredNuggets) {
            return true;
        }

        // If not, check pouch as well
        if (apiAvailable && pouchAPI != null && getItemQuantityMethod != null) {
            try {
                int pouchCount = (int) getItemQuantityMethod.invoke(pouchAPI, player.getUniqueId().toString(), "iron_nugget");
                return (nuggetCount + pouchCount) >= requiredNuggets;
            } catch (Exception e) {
                Bukkit.getLogger().warning("[MyDungeonTeleportPlugin] Error checking pouch contents: " + e.getMessage());
            }
        }

        return nuggetCount >= requiredNuggets;
    }

    /**
     * Remove IPS from player (first from inventory, then from pouch if needed)
     * @param player The player
     * @param amount Amount to remove
     * @return True if successfully removed, false otherwise
     */
    public static boolean removeNuggets(Player player, int amount) {
        // First remove from inventory
        int removed = 0;

        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.IRON_NUGGET) {
                int stackAmount = item.getAmount();

                if (stackAmount <= (amount - removed)) {
                    player.getInventory().remove(item);
                    removed += stackAmount;
                } else {
                    item.setAmount(stackAmount - (amount - removed));
                    removed = amount;
                }

                if (removed >= amount) {
                    player.updateInventory();
                    return true;
                }
            }
        }

        player.updateInventory();

        // If needed, remove the rest from pouch
        int toRemoveFromPouch = amount - removed;
        if (toRemoveFromPouch > 0 && apiAvailable && pouchAPI != null && updateItemQuantityMethod != null) {
            try {
                boolean success = (boolean) updateItemQuantityMethod.invoke(pouchAPI, player.getUniqueId().toString(), "iron_nugget", -toRemoveFromPouch);
                return success;
            } catch (Exception e) {
                Bukkit.getLogger().warning("[MyDungeonTeleportPlugin] Error removing items from pouch: " + e.getMessage());
                return false;
            }
        }

        return removed >= amount;
    }

    /**
     * Check if a location is within the specified portal area bounds
     * @param loc The location to check
     * @param x1 X coordinate of first corner
     * @param x2 X coordinate of second corner
     * @param y1 Y coordinate of first corner
     * @param y2 Y coordinate of second corner
     * @param z1 Z coordinate of first corner
     * @param z2 Z coordinate of second corner
     * @return True if the location is within the bounds, false otherwise
     */
    public static boolean isInPortalArea(Location loc, int x1, int x2, int y1, int y2, int z1, int z2) {
        return loc.getBlockX() >= Math.min(x1, x2) && loc.getBlockX() <= Math.max(x1, x2)
                && loc.getBlockY() >= Math.min(y1, y2) && loc.getBlockY() <= Math.max(y1, y2)
                && loc.getBlockZ() >= Math.min(z1, z2) && loc.getBlockZ() <= Math.max(z1, z2);
    }
}
