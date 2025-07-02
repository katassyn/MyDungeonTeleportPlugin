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

public class ListenerQ8 implements Listener {

    private final MyDungeonTeleportPlugin plugin;
    private DungeonDropDAO dungeonDropDAO;

    public ListenerQ8(MyDungeonTeleportPlugin plugin) {
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

            if (event.getView().getTitle().equals(ChatColor.RED + "Q8 Menu")) {
                event.setCancelled(true);

                if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
                    return;
                }

                Material clickedMaterial = event.getCurrentItem().getType();

                if (clickedMaterial == Material.WHITE_STAINED_GLASS_PANE) {
                    return;
                }

                int requiredLevel = 0;
                String selectedMap = "";
                String mapName = "";
                int requiredIPS = 0;
                String dungeonKey = "";

                if (clickedMaterial == Material.SNOW_BLOCK) {
                    requiredLevel = 50;
                    selectedMap = "q8_m1_inf";
                    mapName = "Q8 Infernal";
                    requiredIPS = 10;
                    dungeonKey = "q8_inf";
                } else if (clickedMaterial == Material.LIGHT_BLUE_CONCRETE) {
                    requiredLevel = 65;
                    selectedMap = "q8_m1_hell";
                    mapName = "Q8 Hell";
                    requiredIPS = 25;
                    dungeonKey = "q8_hell";
                } else if (clickedMaterial == Material.PACKED_ICE) {
                    requiredLevel = 80;
                    selectedMap = "q8_m1_blood";
                    mapName = "Q8 Bloodshed";
                    requiredIPS = 50;
                    dungeonKey = "q8_blood";
                }

                // Check if right-click (show drops) or left-click (select map)
                if (event.isRightClick() && dungeonDropDAO != null) {
                    // Show drop preview GUI using the new method
                    dungeonDropDAO.openDropPreview(player, dungeonKey);
                    return;
                }

                // Left-click - select map
                if (player.getLevel() < requiredLevel) {
                    player.sendMessage(ChatColor.RED + "You need to be at least level " + requiredLevel + " to enter this location.");
                    return;
                }

                if (!DungeonUtils.hasEnoughNuggets(player, requiredIPS)) {
                    player.sendMessage(ChatColor.RED + "You need at least " + requiredIPS + " Fragments of Infernal Passage to enter this location.");
                    return;
                }

                plugin.setSelectedMap(player, selectedMap);
                plugin.getLogger().info("Player " + player.getName() + " selected map: " + selectedMap);
                player.sendMessage(ChatColor.GREEN + "Successfully selected " + mapName + ".");

                player.closeInventory();
            }
        }
    }
}
