package com.maks.mydungeonteleportplugin;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.UUID;

public abstract class AbstractPortalListener implements Listener {

    protected final MyDungeonTeleportPlugin plugin;
    protected final HashMap<UUID, Long> lastMessageTime = new HashMap<>();
    protected final HashMap<UUID, Integer> playerTimeoutTasks = new HashMap<>();

    // Portal coordinates
    protected final int x1, x2, y1, y2, z1, z2;
    protected final String questPrefix;

    public AbstractPortalListener(MyDungeonTeleportPlugin plugin, String questPrefix,
                                  int x1, int x2, int y1, int y2, int z1, int z2) {
        this.plugin = plugin;
        this.questPrefix = questPrefix;
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        this.z1 = z1;
        this.z2 = z2;
    }

    /**
     * Schedule a timeout task for the player (30 min kill timer)
     * @param player The player to schedule the task for
     * @return The task ID
     */
    protected int scheduleTimeoutTask(Player player) {
        return plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if (player.isOnline()) {
                player.setHealth(0);
                player.sendMessage(ChatColor.RED + "Time's Up! You're Dead!");
                plugin.releaseQuestForPlayer(player.getUniqueId());
                playerTimeoutTasks.remove(player.getUniqueId());
            }
        }, 36000L); // 30 minutes in ticks
    }

    /**
     * Event handler for player death
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UUID playerId = player.getUniqueId();
        Integer taskId = playerTimeoutTasks.remove(playerId);
        if (taskId != null) {
            plugin.getServer().getScheduler().cancelTask(taskId);
        }
        plugin.releaseQuestForPlayer(playerId);
        plugin.clearSelectedMap(player);
    }

    /**
     * Event handler for player logout
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        Integer taskId = playerTimeoutTasks.remove(playerId);
        if (taskId != null) {
            plugin.getServer().getScheduler().cancelTask(taskId);
        }

        // Pobierz quest gracza i anuluj go w BeautyQuests
        String playerQuest = plugin.getPlayerQuest(playerId);
        if (playerQuest != null) {
            // Wyciągnij ID questa z nazwy mapy (np. q10_m1_inf -> 141)
            int questId = getQuestIdFromMap(playerQuest);
            if (questId > 0) {
                // Anuluj quest w BeautyQuests
                plugin.getServer().dispatchCommand(
                        plugin.getServer().getConsoleSender(),
                        "quests cancel " + player.getName() + " " + questId
                );
                plugin.getLogger().info("Cancelled quest " + questId + " for player " + player.getName());
            }
        }

        plugin.releaseQuestForPlayer(playerId);
        plugin.clearSelectedMap(player);
    }

    // Metoda pomocnicza do pobrania ID questa z nazwy mapy
    private int getQuestIdFromMap(String mapName) {
        // q1
        if (mapName.equals("q1_m1_inf")) return 51;
        if (mapName.equals("q1_m1_hell")) return 52;
        if (mapName.equals("q1_m1_blood")) return 53;

        // q2
        if (mapName.equals("q2_m1_inf")) return 61;
        if (mapName.equals("q2_m1_hell")) return 62;
        if (mapName.equals("q2_m1_blood")) return 63;

        // q3
        if (mapName.equals("q3_m1_inf")) return 71;
        if (mapName.equals("q3_m1_hell")) return 72;
        if (mapName.equals("q3_m1_blood")) return 73;

        // q4
        if (mapName.equals("q4_m1_inf")) return 81;
        if (mapName.equals("q4_m1_hell")) return 82;
        if (mapName.equals("q4_m1_blood")) return 83;

        // q5
        if (mapName.equals("q5_m1_inf")) return 91;
        if (mapName.equals("q5_m1_hell")) return 92;
        if (mapName.equals("q5_m1_blood")) return 93;

        // q6
        if (mapName.equals("q6_m1_inf")) return 101;
        if (mapName.equals("q6_m1_hell")) return 102;
        if (mapName.equals("q6_m1_blood")) return 103;

        // q7
        if (mapName.equals("q7_m1_inf")) return 111;
        if (mapName.equals("q7_m1_hell")) return 112;
        if (mapName.equals("q7_m1_blood")) return 113;

        // q8
        if (mapName.equals("q8_m1_inf")) return 121;
        if (mapName.equals("q8_m1_hell")) return 122;
        if (mapName.equals("q8_m1_blood")) return 123;

        // q9
        if (mapName.equals("q9_m1_inf")) return 131;
        if (mapName.equals("q9_m1_hell")) return 132;
        if (mapName.equals("q9_m1_blood")) return 133;

        // q10
        if (mapName.equals("q10_m1_inf")) return 141;
        if (mapName.equals("q10_m1_hell")) return 142;
        if (mapName.equals("q10_m1_blood")) return 143;

        return -1; // Nie znaleziono
    }

    /**
     * Method for quest completion
     */
    public void onQuestComplete(Player player) {
        UUID playerId = player.getUniqueId();
        Integer taskId = playerTimeoutTasks.remove(playerId);
        if (taskId != null) {
            plugin.getServer().getScheduler().cancelTask(taskId);
        }
        plugin.releaseQuestForPlayer(playerId);
        plugin.clearSelectedMap(player);
    }

    /**
     * Periodic cleanup method to prevent memory leaks
     */
    protected void cleanupLastMessageTimeMap() {
        long currentTime = System.currentTimeMillis();
        lastMessageTime.entrySet().removeIf(entry -> currentTime - entry.getValue() > 300000); // 5 minutes
    }
}