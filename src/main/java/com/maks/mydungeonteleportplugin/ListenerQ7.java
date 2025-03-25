package com.maks.mydungeonteleportplugin;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ListenerQ7 implements Listener {

    private final MyDungeonTeleportPlugin plugin;

    public ListenerQ7(MyDungeonTeleportPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();

            if (event.getView().getTitle().equals(ChatColor.RED + "Q7 Menu")) {
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

                if (clickedMaterial == Material.NETHERRACK) {
                    requiredLevel = 50;
                    selectedMap = "q7_m1_inf";
                    mapName = "Q7 Infernal";
                    requiredIPS = 10;
                } else if (clickedMaterial == Material.GRAY_CONCRETE) {
                    requiredLevel = 65;
                    selectedMap = "q7_m1_hell";
                    mapName = "Q7 Hell";
                    requiredIPS = 25;
                } else if (clickedMaterial == Material.MAGMA_BLOCK) {
                    requiredLevel = 80;
                    selectedMap = "q7_m1_blood";
                    mapName = "Q7 Bloodshed";
                    requiredIPS = 50;
                }

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
