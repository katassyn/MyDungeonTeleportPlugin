//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.maks.mydungeonteleportplugin;

import com.maks.mydungeonteleportplugin.database.DungeonKeyUtils;
import com.maks.mydungeonteleportplugin.database.PlayerStatsDAO;
import com.maks.mydungeonteleportplugin.quests.QuestManager;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class q10PortalListener implements Listener {
    private final MyDungeonTeleportPlugin plugin;
    private final HashMap<UUID, Long> lastMessageTime = new HashMap();
    private final QuestManager questManager;
    private PlayerStatsDAO playerStatsDAO;
    private int debuggingFlag = 0;
    private final int x1 = -805;
    private final int x2 = -805;
    private final int y1 = -60;
    private final int y2 = -48;
    private final int z1 = -602;
    private final int z2 = -613;

    public q10PortalListener(MyDungeonTeleportPlugin plugin) {
        this.plugin = plugin;
        this.questManager = plugin.getQuestManager();
        // playerStatsDAO will be set by the main plugin class
    }

    public void setPlayerStatsDAO(PlayerStatsDAO playerStatsDAO) {
        this.playerStatsDAO = playerStatsDAO;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location loc = player.getLocation();
        if (Math.random() < 0.01) {
            this.cleanupLastMessageTimeMap();
        }

        String selectedMap = this.plugin.getSelectedMap(player);
        if (DungeonUtils.isInPortalArea(loc, -805, -805, -60, -48, -602, -613)) {
            if (this.debuggingFlag == 1) {
                player.sendMessage(ChatColor.GRAY + "DEBUG: Player in q10 portal area. Selected map: " + selectedMap);
            }

            long currentTime = System.currentTimeMillis();
            if (selectedMap == null) {
                if (this.lastMessageTime.containsKey(player.getUniqueId())) {
                    long lastTime = (Long)this.lastMessageTime.get(player.getUniqueId());
                    if (currentTime - lastTime < 5000L) {
                        return;
                    }
                }

                this.lastMessageTime.put(player.getUniqueId(), currentTime);
                player.sendMessage(ChatColor.RED + "You need to select a location before entering the portal!");
                return;
            }

            if (this.plugin.isQuestOccupied(selectedMap)) {
                if (this.lastMessageTime.containsKey(player.getUniqueId())) {
                    long lastTime = (Long)this.lastMessageTime.get(player.getUniqueId());
                    if (currentTime - lastTime < 5000L) {
                        return;
                    }
                }

                this.lastMessageTime.put(player.getUniqueId(), currentTime);
                player.sendMessage(ChatColor.RED + "This quest is already occupied by another player!");
                return;
            }

            int playerLevel = player.getLevel();
            int requiredIPS = 0;
            boolean questStarted = false;
            if (selectedMap.equals("q10_m1_inf") && playerLevel >= 50) {
                requiredIPS = 10;

                // Check if player has enough IPS before starting quest
                if (!DungeonUtils.hasEnoughNuggets(player, requiredIPS)) {
                    player.sendMessage(ChatColor.RED + "You need at least " + requiredIPS + " Fragments of Infernal Passage to enter this location.");
                    return;
                }

                questStarted = this.questManager.startQuest(player, "q10_inf");
                if (!questStarted) {
                    return;
                }

                // Remove IPS from player
                if (!DungeonUtils.consumeNuggets(player, requiredIPS)) {
                    player.sendMessage(ChatColor.RED + "Failed to consume IPS items!");
                    this.questManager.cancelQuest(player.getUniqueId());
                    return;
                }

                this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), "warp q10_m1_inf " + player.getName());
                this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), "mm s resettimers g:q10_inf");
                if (this.debuggingFlag == 1) {
                    player.sendMessage(ChatColor.GRAY + "DEBUG: Started Q10 Infernal quest");
                }
            } else if (selectedMap.equals("q10_m1_hell") && playerLevel >= 65) {
                requiredIPS = 25;

                // Check if player has enough IPS before starting quest
                if (!DungeonUtils.hasEnoughNuggets(player, requiredIPS)) {
                    player.sendMessage(ChatColor.RED + "You need at least " + requiredIPS + " Fragments of Infernal Passage to enter this location.");
                    return;
                }

                questStarted = this.questManager.startQuest(player, "q10_hell");
                if (!questStarted) {
                    return;
                }

                // Remove IPS from player
                if (!DungeonUtils.consumeNuggets(player, requiredIPS)) {
                    player.sendMessage(ChatColor.RED + "Failed to consume IPS items!");
                    this.questManager.cancelQuest(player.getUniqueId());
                    return;
                }

                this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), "warp q10_m1_hell " + player.getName());
                this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), "mm s resettimers g:q10_hell");
                if (this.debuggingFlag == 1) {
                    player.sendMessage(ChatColor.GRAY + "DEBUG: Started Q10 Hell quest");
                }
            } else {
                if (!selectedMap.equals("q10_m1_blood") || playerLevel < 80) {
                    player.sendMessage(ChatColor.RED + "You do not have the required level for this location!");
                    return;
                }

                requiredIPS = 50;

                // Check if player has enough IPS before starting quest
                if (!DungeonUtils.hasEnoughNuggets(player, requiredIPS)) {
                    player.sendMessage(ChatColor.RED + "You need at least " + requiredIPS + " Fragments of Infernal Passage to enter this location.");
                    return;
                }

                questStarted = this.questManager.startQuest(player, "q10_blood");
                if (!questStarted) {
                    return;
                }

                // Remove IPS from player
                if (!DungeonUtils.consumeNuggets(player, requiredIPS)) {
                    player.sendMessage(ChatColor.RED + "Failed to consume IPS items!");
                    this.questManager.cancelQuest(player.getUniqueId());
                    return;
                }

                this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), "warp q10_m1_blood " + player.getName());
                this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), "mm s resettimers g:q10_blood");
                if (this.debuggingFlag == 1) {
                    player.sendMessage(ChatColor.GRAY + "DEBUG: Started Q10 Bloodshed quest");
                }
            }

            // Track dungeon entry in statistics
            if (playerStatsDAO != null) {
                String dungeonKey = DungeonKeyUtils.getDungeonKeyFromSelectedMap(selectedMap);
                if (dungeonKey != null) {
                    playerStatsDAO.incrementEntries(player.getUniqueId(), dungeonKey);
                }
            }
        }

    }

    protected void cleanupLastMessageTimeMap() {
        long currentTime = System.currentTimeMillis();
        this.lastMessageTime.entrySet().removeIf((entry) -> currentTime - (Long)entry.getValue() > 300000L);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        this.questManager.cancelQuest(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        this.questManager.cancelQuest(player.getUniqueId());
    }
}
