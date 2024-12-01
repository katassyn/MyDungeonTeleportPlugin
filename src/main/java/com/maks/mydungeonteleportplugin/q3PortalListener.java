package com.maks.mydungeonteleportplugin;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

import java.util.HashMap;
import java.util.UUID;

public class q3PortalListener implements Listener {

    private final MyDungeonTeleportPlugin plugin;
    private final HashMap<UUID, Long> lastMessageTime = new HashMap<>();
    private final HashMap<UUID, Integer> playerTimeoutTasks = new HashMap<>();

    public q3PortalListener(MyDungeonTeleportPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location loc = player.getLocation();

        String selectedMap = plugin.getSelectedMap(player);

        // Portal coordinates for q3
        int x1 = -978, x2 = -989;
        int y1 = -60, y2 = -48;
        int z1 = -338, z2 = -338;

        // Check if player is in portal area
        if (isInPortalArea(loc, x1, x2, y1, y2, z1, z2)) {
            long currentTime = System.currentTimeMillis();

            // If player hasn't selected a map, show message every 5 seconds
            if (selectedMap == null) {
                if (lastMessageTime.containsKey(player.getUniqueId())) {
                    long lastTime = lastMessageTime.get(player.getUniqueId());
                    if (currentTime - lastTime < 5000) {
                        return;
                    }
                }

                lastMessageTime.put(player.getUniqueId(), currentTime);
                player.sendMessage(ChatColor.RED + "You need to select a location before entering the portal!");
                return;
            }

            // Check if quest is occupied
            if (plugin.isQuestOccupied(selectedMap)) {
                if (lastMessageTime.containsKey(player.getUniqueId())) {
                    long lastTime = lastMessageTime.get(player.getUniqueId());
                    if (currentTime - lastTime < 5000) {
                        return;
                    }
                }

                lastMessageTime.put(player.getUniqueId(), currentTime);
                player.sendMessage(ChatColor.RED + "This quest is already occupied by another player!");
                return;
            }

            // Check player's level
            int playerLevel = player.getLevel();
            int requiredIPS = 0;

            int taskId;

            if (selectedMap.equals("q3_m1_inf") && playerLevel >= 50) {
                requiredIPS = 10;
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "warp q3_m1_inf " + player.getName());
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "mm s resettimers g:q3_inf");
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "quests start " + player.getName() + " 71 -overrideRequirements");
                taskId = scheduleTimeoutTask(player);
                player.sendTitle(ChatColor.GOLD + "Q3 Quest Started", ChatColor.YELLOW + "You have 30 minutes to clear it. Good Luck!", 10, 70, 20);
            } else if (selectedMap.equals("q3_m1_hell") && playerLevel >= 65) {
                requiredIPS = 25;
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "warp q3_m1_hell " + player.getName());
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "mm s resettimers g:q3_hell");
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "quests start " + player.getName() + " 72 -overrideRequirements");
                taskId = scheduleTimeoutTask(player);
                player.sendTitle(ChatColor.GOLD + "Q3 Quest Started", ChatColor.YELLOW + "You have 30 minutes to clear it. Good Luck!", 10, 70, 20);
            } else if (selectedMap.equals("q3_m1_blood") && playerLevel >= 80) {
                requiredIPS = 50;
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "warp q3_m1_blood " + player.getName());
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "mm s resettimers g:q3_blood");
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "quests start " + player.getName() + " 73 -overrideRequirements");
                taskId = scheduleTimeoutTask(player);
                player.sendTitle(ChatColor.GOLD + "Q3 Quest Started", ChatColor.YELLOW + "You have 30 minutes to clear it. Good Luck!", 10, 70, 20);
            } else {
                player.sendMessage(ChatColor.RED + "You do not have the required level for this location!");
                return;
            }

            // Store the task ID
            playerTimeoutTasks.put(player.getUniqueId(), taskId);

            // Occupy the quest
            plugin.occupyQuest(selectedMap, player.getUniqueId());

            // Remove IPS after teleporting
            plugin.removeWool(player, requiredIPS);
        }
    }

    // ... (Include the rest of the methods similar to q2PortalListener)
    private boolean isInPortalArea(Location loc, int x1, int x2, int y1, int y2, int z1, int z2) {
        return loc.getBlockX() >= Math.min(x1, x2) && loc.getBlockX() <= Math.max(x1, x2) &&
                loc.getBlockY() >= Math.min(y1, y2) && loc.getBlockY() <= Math.max(y1, y2) &&
                loc.getBlockZ() >= Math.min(z1, z2) && loc.getBlockZ() <= Math.max(z1, z2);
    }

    // Method to schedule the timeout task
    private int scheduleTimeoutTask(Player player) {
        return plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if (player.isOnline()) {
                player.setHealth(0); // Kill the player
                player.sendMessage(ChatColor.RED + "Time's Up! You're Dead!");
                // Release the quest
                plugin.releaseQuestForPlayer(player.getUniqueId());
                // Remove the task from the map
                playerTimeoutTasks.remove(player.getUniqueId());
            }
        }, 36000L); // 30 minutes in ticks
    }

    // Event handler for player death
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UUID playerId = player.getUniqueId();
        Integer taskId = playerTimeoutTasks.remove(playerId);
        if (taskId != null) {
            plugin.getServer().getScheduler().cancelTask(taskId);
        }
        // Release the quest occupation
        plugin.releaseQuestForPlayer(playerId);
        // Clear the selected map
        plugin.clearSelectedMap(player);
    }

    // Event handler for player logout
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        Integer taskId = playerTimeoutTasks.remove(playerId);
        if (taskId != null) {
            plugin.getServer().getScheduler().cancelTask(taskId);
        }
        // Release the quest occupation
        plugin.releaseQuestForPlayer(playerId);
        // Clear the selected map
        plugin.clearSelectedMap(player);
    }

    // Method to handle quest completion
    public void onQuestComplete(Player player) {
        UUID playerId = player.getUniqueId();
        Integer taskId = playerTimeoutTasks.remove(playerId);
        if (taskId != null) {
            plugin.getServer().getScheduler().cancelTask(taskId);
        }
        // Release the quest occupation
        plugin.releaseQuestForPlayer(playerId);
        // Clear the selected map
        plugin.clearSelectedMap(player);
    }
    // Copy the isInPortalArea, scheduleTimeoutTask, onPlayerDeath, onPlayerQuit, and onQuestComplete methods here
}
