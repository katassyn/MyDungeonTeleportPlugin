//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.maks.mydungeonteleportplugin;

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

public class q1PortalListener implements Listener {
    private final MyDungeonTeleportPlugin plugin;
    private final HashMap<UUID, Long> lastMessageTime = new HashMap();
    private final QuestManager questManager;

    public q1PortalListener(MyDungeonTeleportPlugin plugin) {
        this.plugin = plugin;
        this.questManager = plugin.getQuestManager();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location loc = player.getLocation();
        String selectedMap = this.plugin.getSelectedMap(player);
        int x1 = -854;
        int x2 = -865;
        int y1 = -60;
        int y2 = -48;
        int z1 = -324;
        int z2 = -324;
        if (this.isInPortalArea(loc, x1, x2, y1, y2, z1, z2)) {
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
            if (selectedMap.equals("q1_m1_inf") && playerLevel >= 50) {
                requiredIPS = 10;
                questStarted = this.questManager.startQuest(player, "q1_inf");
                if (!questStarted) {
                    return;
                }

                this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), "warp q1_m1_inf " + player.getName());
                this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), "mm s resettimers g:q1_inf");
            } else if (selectedMap.equals("q1_m1_hell") && playerLevel >= 65) {
                requiredIPS = 25;
                questStarted = this.questManager.startQuest(player, "q1_hell");
                if (!questStarted) {
                    return;
                }

                this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), "warp q1_m1_hell " + player.getName());
                this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), "mm s resettimers g:q1_hell");
            } else {
                if (!selectedMap.equals("q1_m1_blood") || playerLevel < 80) {
                    player.sendMessage(ChatColor.RED + "You do not have the required level for this location!");
                    return;
                }

                requiredIPS = 50;
                questStarted = this.questManager.startQuest(player, "q1_blood");
                if (!questStarted) {
                    return;
                }

                this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), "warp q1_m1_blood " + player.getName());
                this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), "mm s resettimers g:q1_blood");
            }

            this.plugin.removeWool(player, requiredIPS);
        }

    }

    private boolean isInPortalArea(Location loc, int x1, int x2, int y1, int y2, int z1, int z2) {
        return loc.getBlockX() >= Math.min(x1, x2) && loc.getBlockX() <= Math.max(x1, x2) && loc.getBlockY() >= Math.min(y1, y2) && loc.getBlockY() <= Math.max(y1, y2) && loc.getBlockZ() >= Math.min(z1, z2) && loc.getBlockZ() <= Math.max(z1, z2);
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
