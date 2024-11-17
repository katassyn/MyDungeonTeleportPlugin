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

        // Pobieranie wybranej mapy przez gracza
        String selectedMap = plugin.getSelectedMap(player);
        plugin.getLogger().info("Selected map: " + selectedMap);

        // Sprawdzamy, czy gracz był w jakimś queście i czy jest on zajęty
        if (selectedMap != null && plugin.isQuestOccupied(selectedMap)) {
            UUID occupantUUID = plugin.getQuestOccupant(selectedMap);
            if (occupantUUID.equals(player.getUniqueId())) {
                plugin.releaseQuest(selectedMap);
                plugin.clearSelectedMap(player); // Czyścimy wybraną mapę po śmierci gracza
                plugin.getLogger().info("Quest " + selectedMap + " has been released.");
            }
        }
    }
}
