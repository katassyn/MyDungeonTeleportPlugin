package com.maks.mydungeonteleportplugin;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class q5PortalListener extends AbstractPortalListener {

    public q5PortalListener(MyDungeonTeleportPlugin plugin) {
        super(plugin, "q5", -945, -934, -60, -48, -616, -616);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location loc = player.getLocation();

        // Clean up last message time map periodically
        if (Math.random() < 0.01) { // 1% chance each move event to clean up
            cleanupLastMessageTimeMap();
        }

        // Check if player is in portal area
        if (DungeonUtils.isInPortalArea(loc, x1, x2, y1, y2, z1, z2)) {
            String selectedMap = plugin.getSelectedMap(player);
            long currentTime = System.currentTimeMillis();

            // Check if player has selected a map
            if (selectedMap == null) {
                if (lastMessageTime.containsKey(player.getUniqueId())) {
                    long lastTime = lastMessageTime.get(player.getUniqueId());
                    if (currentTime - lastTime < 5000) {
                        return; // Don't spam messages, wait 5 seconds
                    }
                }

                lastMessageTime.put(player.getUniqueId(), currentTime);
                player.sendMessage(ChatColor.RED + "You need to select a location before entering the portal!");
                return;
            }

            // Check if quest is already occupied
            if (plugin.isQuestOccupied(selectedMap)) {
                if (lastMessageTime.containsKey(player.getUniqueId())) {
                    long lastTime = lastMessageTime.get(player.getUniqueId());
                    if (currentTime - lastTime < 5000) {
                        return; // Don't spam messages, wait 5 seconds
                    }
                }

                lastMessageTime.put(player.getUniqueId(), currentTime);
                player.sendMessage(ChatColor.RED + "This quest is already occupied by another player!");
                return;
            }

            // Check level requirements and teleport accordingly
            int playerLevel = player.getLevel();
            int requiredIPS = 0;
            int taskId;

            if (selectedMap.equals("q5_m1_inf") && playerLevel >= 50) {
                requiredIPS = 10;
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "warp q5_m1_inf " + player.getName());
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "mm s resettimers g:q5_inf");
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "quests start " + player.getName() + " 91 -overrideRequirements");
                taskId = scheduleTimeoutTask(player);
                player.sendTitle(ChatColor.GOLD + "Q5 Quest Started", ChatColor.YELLOW + "You have 30 minutes to clear it. Good luck!", 10, 70, 20);
            } else if (selectedMap.equals("q5_m1_hell") && playerLevel >= 65) {
                requiredIPS = 25;
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "warp q5_m1_hell " + player.getName());
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "mm s resettimers g:q5_hell");
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "quests start " + player.getName() + " 92 -overrideRequirements");
                taskId = scheduleTimeoutTask(player);
                player.sendTitle(ChatColor.GOLD + "Q5 Quest Started", ChatColor.YELLOW + "You have 30 minutes to clear it. Good luck!", 10, 70, 20);
            } else if (selectedMap.equals("q5_m1_blood") && playerLevel >= 80) {
                requiredIPS = 50;
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "warp q5_m1_blood " + player.getName());
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "mm s resettimers g:q5_blood");
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "quests start " + player.getName() + " 93 -overrideRequirements");
                taskId = scheduleTimeoutTask(player);
                player.sendTitle(ChatColor.GOLD + "Q5 Quest Started", ChatColor.YELLOW + "You have 30 minutes to clear it. Good luck!", 10, 70, 20);
            } else {
                player.sendMessage(ChatColor.RED + "You do not have the required level for this location!");
                return;
            }

            // Store task ID, occupy quest, and remove required IPS
            playerTimeoutTasks.put(player.getUniqueId(), taskId);
            plugin.occupyQuest(selectedMap, player.getUniqueId());
            plugin.removeWool(player, requiredIPS);
        }
    }
}