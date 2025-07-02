package com.maks.mydungeonteleportplugin.gui;

import com.maks.mydungeonteleportplugin.MyDungeonTeleportPlugin;
import com.maks.mydungeonteleportplugin.database.DungeonKeyUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Ulepszone GUI do podglądu dropów z dungeonów
 */
public class DropPreviewGUI implements InventoryHolder, Listener {
    private final MyDungeonTeleportPlugin plugin;
    private final String dungeonKey;
    private final Inventory inventory;

    // Kolejność rzadkości (od najniższej do najwyższej)
    private static final List<String> RARITY_ORDER = Arrays.asList(
        "Magic",
        "Extraordinary", 
        "Legendary",
        "Unique",
        "MYTHIC"
    );

    // Kolory dla rzadkości
    private static final Map<String, ChatColor> RARITY_COLORS = new HashMap<String, ChatColor>() {{
        put("Magic", ChatColor.BLUE);
        put("Extraordinary", ChatColor.DARK_PURPLE);
        put("Legendary", ChatColor.GOLD);
        put("Unique", ChatColor.LIGHT_PURPLE);
        put("MYTHIC", ChatColor.DARK_RED);
    }};

    public DropPreviewGUI(MyDungeonTeleportPlugin plugin, String dungeonKey, List<ItemStack> drops) {
        this.plugin = plugin;
        this.dungeonKey = dungeonKey;

        // Tworzymy GUI z tylko 1 rzędem (9 slotów)
        String title = ChatColor.DARK_GRAY + "» " + ChatColor.GOLD + "Drops: " + 
                       ChatColor.WHITE + DungeonKeyUtils.getFormattedDungeonName(dungeonKey);
        this.inventory = Bukkit.createInventory(this, 9, title);

        setupInventory(drops);
    }

    private void setupInventory(List<ItemStack> drops) {
        // Sortujemy itemy według rzadkości
        List<ItemStack> sortedDrops = sortDropsByRarity(drops);

        // Dodajemy posortowane itemy do GUI (maksymalnie 9)
        int slot = 0;
        for (ItemStack drop : sortedDrops) {
            if (slot >= 9) break; // Tylko 9 slotów

            if (drop != null && drop.getType() != Material.AIR) {
                // Klonujemy item i dodajemy informację o rzadkości do nazwy
                ItemStack displayItem = drop.clone();
                addRarityToDisplayName(displayItem);
                inventory.setItem(slot, displayItem);
                slot++;
            }
        }

        // Wypełniamy puste sloty szarym szkłem
        ItemStack filler = createFillerItem();
        for (int i = slot; i < 9; i++) {
            inventory.setItem(i, filler);
        }
    }

    private List<ItemStack> sortDropsByRarity(List<ItemStack> drops) {
        return drops.stream()
            .filter(item -> item != null && item.getType() != Material.AIR)
            .sorted((item1, item2) -> {
                String rarity1 = getRarityFromLore(item1);
                String rarity2 = getRarityFromLore(item2);

                int index1 = RARITY_ORDER.indexOf(rarity1);
                int index2 = RARITY_ORDER.indexOf(rarity2);

                // Jeśli nie znaleziono rzadkości, daj na koniec
                if (index1 == -1) index1 = RARITY_ORDER.size();
                if (index2 == -1) index2 = RARITY_ORDER.size();

                return Integer.compare(index1, index2);
            })
            .collect(Collectors.toList());
    }

    private String getRarityFromLore(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;

        ItemMeta meta = item.getItemMeta();
        if (!meta.hasLore()) return null;

        List<String> lore = meta.getLore();
        for (String line : lore) {
            // Usuwamy kolory i sprawdzamy czy linia zawiera "rarity:"
            String cleanLine = ChatColor.stripColor(line).toLowerCase();
            if (cleanLine.contains("rarity:")) {
                // Sprawdzamy każdą rzadkość
                for (String rarity : RARITY_ORDER) {
                    if (cleanLine.contains(rarity.toLowerCase())) {
                        return rarity;
                    }
                }
            }
        }

        return null;
    }

    private void addRarityToDisplayName(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        String rarity = getRarityFromLore(item);

        if (rarity != null) {
            ChatColor color = RARITY_COLORS.getOrDefault(rarity, ChatColor.WHITE);
            String currentName = meta.hasDisplayName() ? meta.getDisplayName() : item.getType().toString();

            // Dodajemy gwiazdki i rzadkość do nazwy
            String stars = getStarsForRarity(rarity);
            meta.setDisplayName(color + stars + " " + currentName + " " + color + "[" + rarity + "]");

            // Zachowujemy oryginalne lore bez dodawania dodatkowych informacji
            List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
            meta.setLore(lore);
        }

        item.setItemMeta(meta);
    }

    private String getStarsForRarity(String rarity) {
        switch (rarity) {
            case "Magic": return "★";
            case "Extraordinary": return "★★";
            case "Legendary": return "★★★";
            case "Unique": return "★★★★";
            case "MYTHIC": return "★★★★★";
            default: return "";
        }
    }

    private ItemStack createFillerItem() {
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = filler.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_GRAY + "»");
        meta.setLore(Arrays.asList(
            ChatColor.GRAY + "No more drops to display"
        ));
        filler.setItemMeta(meta);
        return filler;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Sprawdzamy czy to nasze GUI
        if (event.getInventory().getHolder() != this) return;

        // Anulujemy wszystkie kliknięcia - gracz nie może nic zabrać
        event.setCancelled(true);

        // Opcjonalnie: pokazujemy szczegóły itemu po kliknięciu
        if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR 
            && event.getCurrentItem().getType() != Material.GRAY_STAINED_GLASS_PANE) {

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            // Możesz tutaj dodać dodatkową funkcjonalność, np. pokazanie statystyk
            player.sendMessage(ChatColor.GRAY + "» " + ChatColor.YELLOW + "Viewing: " + 
                             clickedItem.getItemMeta().getDisplayName());
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        // Blokujemy przeciąganie itemów do naszego GUI
        if (event.getInventory().getHolder() == this) {
            event.setCancelled(true);
        }
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
