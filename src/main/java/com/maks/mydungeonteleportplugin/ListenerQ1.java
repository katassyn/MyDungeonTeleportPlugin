package com.maks.mydungeonteleportplugin;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ListenerQ1 implements Listener {

    private final MyDungeonTeleportPlugin plugin;

    public ListenerQ1(MyDungeonTeleportPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();

            // Check if interacting with "Q1 Menu"
            if (event.getView().getTitle().equals(ChatColor.RED + "Q1 Menu")) {
                event.setCancelled(true); // Cancel interaction in GUI

                if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
                    return;
                }

                Material clickedMaterial = event.getCurrentItem().getType();

                // Block interaction with the glass pane
                if (clickedMaterial == Material.WHITE_STAINED_GLASS_PANE) {
                    return;
                }

                int requiredLevel = 0;
                String selectedMap = "";
                String mapName = "";
                int requiredIPS = 0;

                // Choose map based on clicked block and check player level
                if (clickedMaterial == Material.NETHER_BRICKS) {
                    requiredLevel = 50;
                    selectedMap = "q1_m1_inf";
                    mapName = "Q1 Infernal";
                    requiredIPS = 10;
                } else if (clickedMaterial == Material.OBSIDIAN) {
                    requiredLevel = 65;
                    selectedMap = "q1_m1_hell";
                    mapName = "Q1 Hell";
                    requiredIPS = 25;
                } else if (clickedMaterial == Material.RED_NETHER_BRICKS) {
                    requiredLevel = 80;
                    selectedMap = "q1_m1_blood";
                    mapName = "Q1 Bloodshed";
                    requiredIPS = 50;
                }

                // Check player level
                if (player.getLevel() < requiredLevel) {
                    player.sendMessage(ChatColor.RED + "You need to be at least level " + requiredLevel + " to enter this location.");
                    return;
                }

                // Check if player has enough IPS
                if (!DungeonUtils.hasEnoughNuggets(player, requiredIPS)) {
                    player.sendMessage(ChatColor.RED + "You need at least " + requiredIPS + " Fragments of Infernal Passage to enter this location.");
                    return;
                }

                // Save selected map without taking IPS yet
                plugin.setSelectedMap(player, selectedMap);
                plugin.getLogger().info("Player " + player.getName() + " selected map: " + selectedMap);
                player.sendMessage(ChatColor.GREEN + "Successfully selected " + mapName + ".");

                player.closeInventory(); // Close GUI after selection
            }
        }
    }
}