package com.maks.mydungeonteleportplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import me.clip.placeholderapi.PlaceholderAPI;

/**
 * Handles pet damage bonuses in dungeons
 */
public class DungeonDamageListener implements Listener {

    private final MyDungeonTeleportPlugin plugin;

    public DungeonDamageListener(MyDungeonTeleportPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getDamager();

        // Check if player is in a dungeon quest
        String selectedMap = plugin.getSelectedMap(player);
        if (selectedMap == null) {
            return;
        }

        // Extract quest number from map name (e.g., "q1_m1_inf" -> "q1")
        String quest = extractQuestFromMap(selectedMap);
        if (quest == null) {
            return;
        }

        // Get damage bonus for this quest
        double damageBonus = getDungeonDamageBonus(player, quest);
        if (damageBonus > 0) {
            double originalDamage = event.getDamage();
            double bonusDamage = originalDamage * (damageBonus / 100.0);
            double newDamage = originalDamage + bonusDamage;

            event.setDamage(newDamage);

            // Notify player about damage bonus (optional, can be removed if too spammy)
            // player.sendMessage(ChatColor.YELLOW + "Pet bonus: +" + String.format("%.1f", bonusDamage) + " damage!");
        }

        // Check for boss execution at level 75 (10% HP threshold)
        if (event.getEntity() instanceof LivingEntity) {
            LivingEntity target = (LivingEntity) event.getEntity();

            // Check if player has level 75 dungeon pet for this quest
            if (canExecuteBoss(player, quest)) {
                double maxHealth = target.getMaxHealth();
                double currentHealth = target.getHealth() - event.getFinalDamage();
                double healthPercentage = (currentHealth / maxHealth) * 100;

                // Execute boss at 10% HP
                if (healthPercentage <= 10.0 && currentHealth > 0) {
                    target.setHealth(0);
                    player.sendMessage(ChatColor.DARK_RED + "☠ BOSS EXECUTION! ☠");
                    player.sendMessage(ChatColor.YELLOW + "Your level 75 dungeon pet executed the boss!");
                }
            }
        }
    }

    /**
     * Extract quest identifier from map name
     * @param mapName Map name like "q1_m1_inf"
     * @return Quest identifier like "q1"
     */
    private String extractQuestFromMap(String mapName) {
        if (mapName == null) {
            return null;
        }

        String[] parts = mapName.split("_");
        if (parts.length >= 1 && parts[0].startsWith("q")) {
            return parts[0]; // Return "q1", "q2", etc.
        }

        return null;
    }

    /**
     * Get pet damage bonus for specific quest using PlaceholderAPI
     * @param player The player
     * @param quest Quest identifier (q1, q2, etc.)
     * @return Damage bonus percentage
     */
    private double getDungeonDamageBonus(Player player, String quest) {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            return 0.0;
        }

        String placeholder = "%petplugin_dungeon_" + quest + "_damage%";
        String result = PlaceholderAPI.setPlaceholders(player, placeholder);

        try {
            return Double.parseDouble(result);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    /**
     * Check if player can execute bosses (has level 75 dungeon pet for this quest)
     * @param player The player
     * @param quest Quest identifier (q1, q2, etc.)
     * @return True if boss execution is available
     */
    private boolean canExecuteBoss(Player player, String quest) {
        // Check if the damage bonus is high enough to indicate level 75 pet
        // At level 75, damage should be around 24% (3% base * 8 level multiplier)
        double damageBonus = getDungeonDamageBonus(player, quest);
        return damageBonus >= 20.0; // Threshold to ensure level 75 pet
    }
}