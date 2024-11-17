package com.maks.mydungeonteleportplugin;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.UUID;

public class q8PortalListener implements Listener {

    private final MyDungeonTeleportPlugin plugin;
    private final HashMap<UUID, Long> lastMessageTime = new HashMap<>(); // Mapowanie gracza na czas wysłania wiadomości

    public q8PortalListener(MyDungeonTeleportPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location loc = player.getLocation();

        String selectedMap = plugin.getSelectedMap(player);

        // Koordynaty portalu dla q8
        int x1 = -810, x2 = -799;
        int y1 = -60, y2 = -48;
        int z1 = -437, z2 = -437;

        // Sprawdzenie, czy gracz jest w obszarze portalu
        if (isInPortalArea(loc, x1, x2, y1, y2, z1, z2)) {
            long currentTime = System.currentTimeMillis();

            // Jeśli gracz nie wybrał mapy, wyświetl wiadomość co 5 sekund
            if (selectedMap == null) {
                if (lastMessageTime.containsKey(player.getUniqueId())) {
                    long lastTime = lastMessageTime.get(player.getUniqueId());
                    if (currentTime - lastTime < 5000) {
                        return; // Oczekiwanie 5 sekund przed kolejną wiadomością
                    }
                }

                lastMessageTime.put(player.getUniqueId(), currentTime);
                player.sendMessage(ChatColor.RED + "You need to select a location before entering the portal!");
                return;
            }

            // Sprawdzanie poziomu gracza
            int playerLevel = player.getLevel();
            int requiredIPS = 0;

            if (selectedMap.equals("q8_m1_inf") && playerLevel >= 50) {
                requiredIPS = 10;
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "warp "  + " q8_m1_inf " + player.getName());
            } else if (selectedMap.equals("q8_m1_hell") && playerLevel >= 65) {
                requiredIPS = 25;
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "warp "  + " q8_m1_hell " + player.getName());
            } else if (selectedMap.equals("q8_m1_blood") && playerLevel >= 80) {
                requiredIPS = 50;
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "warp "  + " q8_m1_blood " + player.getName());
            } else {
                player.sendMessage(ChatColor.RED + "You do not have the required level for this location!");
                return;
            }

            // Zajęcie questa przez gracza
            plugin.occupyQuest(selectedMap, player.getUniqueId());

            // Pobieranie IPS (zamiennika białej wełny) dopiero po teleportacji
            plugin.removeWool(player, requiredIPS);

            // Usunięcie plugin.clearSelectedMap(player); - bo nie chcemy natychmiastowego czyszczenia
        }
    }

    private boolean isInPortalArea(Location loc, int x1, int x2, int y1, int y2, int z1, int z2) {
        return loc.getBlockX() >= Math.min(x1, x2) && loc.getBlockX() <= Math.max(x1, x2) &&
                loc.getBlockY() >= Math.min(y1, y2) && loc.getBlockY() <= Math.max(y1, y2) &&
                loc.getBlockZ() >= Math.min(z1, z2) && loc.getBlockZ() <= Math.max(z1, z2);
    }
}
