package com.maks.mydungeonteleportplugin;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ListenerQ8 implements Listener {

    private final MyDungeonTeleportPlugin plugin;

    public ListenerQ8(MyDungeonTeleportPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();

            // Sprawdzamy, czy gracz wchodzi w interakcję z GUI o nazwie "Q8 Menu"
            if (event.getView().getTitle().equals(ChatColor.RED + "Q8 Menu")) {
                event.setCancelled(true); // Anulujemy interakcję w GUI

                if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
                    return;
                }

                Material clickedMaterial = event.getCurrentItem().getType();

                // Blokada na interakcję z szybą
                if (clickedMaterial == Material.WHITE_STAINED_GLASS_PANE) {
                    return; // Blokujemy kliknięcie
                }

                int requiredLevel = 0;
                String selectedMap = "";
                String mapName = "";
                int requiredIPS = 0;

                // Wybór mapy na podstawie klikniętego bloku i sprawdzenie poziomu gracza
                if (clickedMaterial == Material.SNOW_BLOCK) {
                    requiredLevel = 50;
                    selectedMap = "q8_m1_inf";
                    mapName = "Q8 Infernal";
                    requiredIPS = 10;
                } else if (clickedMaterial == Material.LIGHT_BLUE_CONCRETE) {
                    requiredLevel = 65;
                    selectedMap = "q8_m1_hell";
                    mapName = "Q8 Hell";
                    requiredIPS = 25;
                } else if (clickedMaterial == Material.PACKED_ICE) {
                    requiredLevel = 80;
                    selectedMap = "q8_m1_blood";
                    mapName = "Q8 Bloodshed";
                    requiredIPS = 50;
                }

                // Sprawdzamy poziom gracza
                if (player.getLevel() < requiredLevel) {
                    player.sendMessage(ChatColor.RED + "You need to be at least level " + requiredLevel + " to enter this location.");
                    return;
                }
                // Sprawdzamy, czy gracz ma wystarczającą ilość Fragment of Infernal Passage (IPS)
                if (!hasEnoughNuggets(player, requiredIPS)) {
                    player.sendMessage(ChatColor.RED + "You need at least " + requiredIPS + " Fragments of Infernal Passage to enter this location.");
                    return;
                }
                // Zapisujemy wybraną mapę bez pobierania IPS
                plugin.setSelectedMap(player, selectedMap);
                plugin.getLogger().info("Player " + player.getName() + " selected map: " + selectedMap);

                player.sendMessage(ChatColor.GREEN + "Successfully selected " + mapName + ".");

                player.closeInventory(); // Zamknięcie GUI po dokonaniu wyboru
            }
        }
    }
    private boolean hasEnoughNuggets(Player player, int requiredNuggets) {
        int nuggetCount = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.IRON_NUGGET) {
                nuggetCount += item.getAmount();
            }
        }
        return nuggetCount >= requiredNuggets;
    }
}
