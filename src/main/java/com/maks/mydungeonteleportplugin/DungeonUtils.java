package com.maks.mydungeonteleportplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import me.clip.placeholderapi.PlaceholderAPI;

public class DungeonUtils {

    /**
     * Check if a player has enough Fragment of Infernal Passage (IPS)
     * Now checks both inventory AND pouch using the DungeonPouchHelper
     * @param player The player to check
     * @param requiredNuggets Number of IPS required
     * @return True if player has enough nuggets, false otherwise
     */
    public static boolean hasEnoughNuggets(Player player, int requiredNuggets) {
        return DungeonPouchHelper.hasEnoughIPS(player, requiredNuggets);
    }

    /**
     * Consume the required amount of IPS from player's inventory and/or pouch
     * @param player The player to consume items from
     * @param requiredAmount Amount of IPS to consume
     * @return True if successful, false if not enough items
     */
    public static boolean consumeNuggets(Player player, int requiredAmount) {
        // Check ENDERMAN pet free teleport chance
        double freeTpChance = getPetFreeTeleportChance(player);
        if (freeTpChance > 0 && Math.random() * 100 < freeTpChance) {
            // Free teleport activated!
            player.sendMessage(ChatColor.LIGHT_PURPLE + "✦ ENDERMAN PET EFFECT: Free dungeon teleport!");
            player.sendMessage(ChatColor.GREEN + "You didn't pay any IPS for this teleport!");
            return true; // Skip IPS consumption
        }

        return DungeonPouchHelper.consumeIPS(player, requiredAmount);
    }

    /**
     * Get ENDERMAN pet free teleport chance percentage
     */
    private static double getPetFreeTeleportChance(Player player) {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            return 0.0;
        }

        String placeholder = PlaceholderAPI.setPlaceholders(player, "%petplugin_dungeon_free_tp_chance%");
        try {
            return Double.parseDouble(placeholder);
        } catch (NumberFormatException e) {
            return 0.0;
        }
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