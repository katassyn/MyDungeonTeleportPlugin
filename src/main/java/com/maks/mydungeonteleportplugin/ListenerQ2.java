package com.maks.mydungeonteleportplugin;

import com.maks.mydungeonteleportplugin.database.DungeonDropDAO;
import com.maks.mydungeonteleportplugin.database.DungeonKeyUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class ListenerQ2 implements Listener {

    private final MyDungeonTeleportPlugin plugin;
    private DungeonDropDAO dungeonDropDAO;

    public ListenerQ2(MyDungeonTeleportPlugin plugin) {
        this.plugin = plugin;
        // dungeonDropDAO will be set by the main plugin class
    }

    public void setDungeonDropDAO(DungeonDropDAO dungeonDropDAO) {
        this.dungeonDropDAO = dungeonDropDAO;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();

            // Check if interacting with "Q2 Menu"
            if (event.getView().getTitle().equals(ChatColor.RED + "Q2 Menu")) {
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
                String dungeonKey = "";

                // Choose map based on clicked block and check player level
                if (clickedMaterial == Material.STRIPPED_JUNGLE_WOOD) {
                    requiredLevel = 50;
                    selectedMap = "q2_m1_inf";
                    mapName = "Q2 Infernal";
                    requiredIPS = 10;
                    dungeonKey = "q2_inf";
                } else if (clickedMaterial == Material.MANGROVE_WOOD) {
                    requiredLevel = 65;
                    selectedMap = "q2_m1_hell";
                    mapName = "Q2 Hell";
                    requiredIPS = 25;
                    dungeonKey = "q2_hell";
                } else if (clickedMaterial == Material.GREEN_TERRACOTTA) {
                    requiredLevel = 80;
                    selectedMap = "q2_m1_blood";
                    mapName = "Q2 Bloodshed";
                    requiredIPS = 50;
                    dungeonKey = "q2_blood";
                }

                // Check if right-click (show drops) or left-click (select map)
                if (event.isRightClick() && dungeonDropDAO != null) {
                    // Show drop preview GUI using the new method
                    dungeonDropDAO.openDropPreview(player, dungeonKey);
                    return;
                }

                // Left-click - select map
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
