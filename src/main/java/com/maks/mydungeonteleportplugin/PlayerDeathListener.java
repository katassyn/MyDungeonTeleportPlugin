package com.maks.mydungeonteleportplugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

import java.util.UUID;

public class PlayerDeathListener implements Listener {

    private final MyDungeonTeleportPlugin plugin;

    public PlayerDeathListener(MyDungeonTeleportPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        player.sendMessage(ChatColor.RED + "You died!");
        plugin.getLogger().info(player.getName() + " died!");

        // Get player's selected map
        String selectedMap = plugin.getSelectedMap(player);

        // Make sure to release any quest the player was occupying
        UUID playerId = player.getUniqueId();
        plugin.releaseQuestForPlayer(playerId);
        plugin.clearSelectedMap(player);

        plugin.getLogger().info("Player " + player.getName() + " released from any quests due to death");
    }
}