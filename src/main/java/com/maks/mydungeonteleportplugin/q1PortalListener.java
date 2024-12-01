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

public class q1PortalListener implements Listener {

    private final MyDungeonTeleportPlugin plugin;
    private final HashMap<UUID, Long> lastMessageTime = new HashMap<>(); // Mapowanie gracza na czas wysłania wiadomości
    private final HashMap<UUID, Integer> playerTimeoutTasks = new HashMap<>(); // Mapowanie gracza na ID zadania

    public q1PortalListener(MyDungeonTeleportPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location loc = player.getLocation();

        String selectedMap = plugin.getSelectedMap(player);

        // Koordynaty portalu
        int x1 = -854, x2 = -865;
        int y1 = -60, y2 = -48;
        int z1 = -324, z2 = -324;

        // Sprawdzenie, czy gracz jest w obszarze portalu
        if (isInPortalArea(loc, x1, x2, y1, y2, z1, z2)) {
            long currentTime = System.currentTimeMillis();

            // Jeśli gracz nie wybrał mapy, wyświetl wiadomość co 5 sekund
            if (selectedMap == null) {
                if (lastMessageTime.containsKey(player.getUniqueId())) {
                    long lastTime = lastMessageTime.get(player.getUniqueId());
                    if (currentTime - lastTime < 5000) {
                        return; // Oczekiwanie 5 sekund przed kolejną wiadomością
                    }
                }

                lastMessageTime.put(player.getUniqueId(), currentTime);
                player.sendMessage(ChatColor.RED + "You need to select a location before entering the portal!");
                return;
            }

            // Sprawdzanie, czy quest jest zajęty, i wyświetlanie wiadomości co 5 sekund
            if (plugin.isQuestOccupied(selectedMap)) {
                if (lastMessageTime.containsKey(player.getUniqueId())) {
                    long lastTime = lastMessageTime.get(player.getUniqueId());
                    if (currentTime - lastTime < 5000) {
                        return; // Oczekiwanie 5 sekund przed kolejną wiadomością
                    }
                }

                lastMessageTime.put(player.getUniqueId(), currentTime);
                player.sendMessage(ChatColor.RED + "This quest is already occupied by another player!");
                return;
            }

            // Sprawdzanie poziomu gracza
            int playerLevel = player.getLevel();
            int requiredIPS = 0;

            // Variable to store the task ID
            int taskId;

            if (selectedMap.equals("q1_m1_inf") && playerLevel >= 50) {
                requiredIPS = 10;
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "warp q1_m1_inf " + player.getName());
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "mm s resettimers g:q1_inf");
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "quests start " + player.getName() + " 51 -overrideRequirements");
                taskId = scheduleTimeoutTask(player);
                player.sendTitle(ChatColor.GOLD + "Q1 Quest Started", ChatColor.YELLOW + "You have 30 minutes to clear it. Good Luck!", 10, 70, 20);
            } else if (selectedMap.equals("q1_m1_hell") && playerLevel >= 65) {
                requiredIPS = 25;
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "warp q1_m1_hell " + player.getName());
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "mm s resettimers g:q1_hell");
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "quests start " + player.getName() + " 52 -overrideRequirements");
                taskId = scheduleTimeoutTask(player);
                player.sendTitle(ChatColor.GOLD + "Q1 Quest Started", ChatColor.YELLOW + "You have 30 minutes to clear it. Good Luck!", 10, 70, 20);
            } else if (selectedMap.equals("q1_m1_blood") && playerLevel >= 80) {
                requiredIPS = 50;
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "warp q1_m1_blood " + player.getName());
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "mm s resettimers g:q1_blood");
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "quests start " + player.getName() + " 53 -overrideRequirements");
                taskId = scheduleTimeoutTask(player);
                player.sendTitle(ChatColor.GOLD + "Q1 Quest Started", ChatColor.YELLOW + "You have 30 minutes to clear it. Good Luck!", 10, 70, 20);
            } else {
                player.sendMessage(ChatColor.RED + "You do not have the required level for this location!");
                return;
            }

            // Store the task ID
            playerTimeoutTasks.put(player.getUniqueId(), taskId);

            // Zajęcie questa przez gracza
            plugin.occupyQuest(selectedMap, player.getUniqueId());

            // Pobieranie IPS (zamiennika białej wełny) dopiero po teleportacji
            plugin.removeWool(player, requiredIPS);

            // Usunięcie plugin.clearSelectedMap(player); - bo nie chcemy natychmiastowego czyszczenia
        }
    }

    private boolean isInPortalArea(Location loc, int x1, int x2, int y1, int y2, int z1, int z2) {
        return loc.getBlockX() >= Math.min(x1, x2) && loc.getBlockX() <= Math.max(x1, x2) &&
                loc.getBlockY() >= Math.min(y1, y2) && loc.getBlockY() <= Math.max(y1, y2) &&
                loc.getBlockZ() >= Math.min(z1, z2) && loc.getBlockZ() <= Math.max(z1, z2);
    }

    // Method to schedule the timeout task
    private int scheduleTimeoutTask(Player player) {
        return plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if (player.isOnline()) {
                player.setHealth(0); // Zabija gracza
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
}
