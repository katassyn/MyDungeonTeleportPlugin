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

/**
 * Elastyczne GUI do podglądu dropów z 3 liniami
 * Pozwala na ręczne układanie itemów
 */
public class FlexibleDropPreviewGUI implements InventoryHolder, Listener {
    private final MyDungeonTeleportPlugin plugin;
    private final String dungeonKey;
    private final Inventory inventory;

    // Rozmiar GUI - 3 linie
    private static final int GUI_SIZE = 27;

    // Kolory dla rzadkości (opcjonalne - do podświetlenia nazw)
    private static final Map<String, ChatColor> RARITY_COLORS = new HashMap<String, ChatColor>() {{
        put("magic", ChatColor.BLUE);
        put("extraordinary", ChatColor.DARK_PURPLE);
        put("legendary", ChatColor.GOLD);
        put("unique", ChatColor.LIGHT_PURPLE);
        put("mythic", ChatColor.DARK_RED);
    }};

    public FlexibleDropPreviewGUI(MyDungeonTeleportPlugin plugin, String dungeonKey, Inventory savedInventory) {
        this.plugin = plugin;
        this.dungeonKey = dungeonKey;

        // Tworzymy GUI z 3 rzędami (27 slotów)
        String title = ChatColor.DARK_GRAY + "» " + ChatColor.GOLD + "Drops: " + 
                       ChatColor.WHITE + DungeonKeyUtils.getFormattedDungeonName(dungeonKey);
        this.inventory = Bukkit.createInventory(this, GUI_SIZE, title);

        setupInventory(savedInventory);
    }

    private void setupInventory(Inventory savedInventory) {
        // Najpierw wypełniamy wszystko białym szkłem
        ItemStack filler = createFillerItem();
        for (int i = 0; i < GUI_SIZE; i++) {
            inventory.setItem(i, filler);
        }

        // Następnie dodajemy zapisane itemy w dokładnie tych samych pozycjach
        if (savedInventory != null) {
            for (int i = 0; i < savedInventory.getSize() && i < GUI_SIZE; i++) {
                ItemStack item = savedInventory.getItem(i);
                if (item != null && item.getType() != Material.AIR) {
                    // Klonujemy item i dodajemy informacje dla gracza
                    ItemStack displayItem = item.clone();
                    enhanceItemDisplay(displayItem);
                    inventory.setItem(i, displayItem);
                }
            }
        }
    }

    private void enhanceItemDisplay(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();

        // Opcjonalnie: podświetl nazwę według rzadkości
        String rarity = getRarityFromLore(item);
        if (rarity != null) {
            ChatColor color = RARITY_COLORS.getOrDefault(rarity.toLowerCase(), ChatColor.WHITE);
            String currentName = meta.hasDisplayName() ? meta.getDisplayName() : getItemDisplayName(item);

            // Dodajemy tylko kolor, bez gwiazdek czy dodatkowych oznaczeń
            if (!currentName.startsWith(color.toString())) {
                meta.setDisplayName(color + currentName);
            }
        }

        // Zachowujemy oryginalne lore bez dodawania dodatkowych informacji
        List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();

        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    private String getRarityFromLore(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;

        ItemMeta meta = item.getItemMeta();
        if (!meta.hasLore()) return null;

        List<String> lore = meta.getLore();
        for (String line : lore) {
            String cleanLine = ChatColor.stripColor(line).toLowerCase();
            if (cleanLine.contains("rarity:")) {
                for (String rarity : RARITY_COLORS.keySet()) {
                    if (cleanLine.contains(rarity)) {
                        return rarity;
                    }
                }
            }
        }

        return null;
    }

    private String getItemDisplayName(ItemStack item) {
        // Ładna nazwa dla itemów bez custom name
        String name = item.getType().toString().toLowerCase().replace("_", " ");
        return capitalize(name);
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;

        for (char c : str.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                capitalizeNext = true;
            } else if (capitalizeNext) {
                c = Character.toUpperCase(c);
                capitalizeNext = false;
            }
            result.append(c);
        }

        return result.toString();
    }

    private ItemStack createFillerItem() {
        ItemStack filler = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
        ItemMeta meta = filler.getItemMeta();
        meta.setDisplayName(" "); // Pusta nazwa
        filler.setItemMeta(meta);
        return filler;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Sprawdzamy czy to nasze GUI
        if (event.getInventory().getHolder() != this) return;

        // Anulujemy wszystkie kliknięcia - gracz nie może nic zabrać
        event.setCancelled(true);

        // Opcjonalnie: efekt dźwiękowy przy kliknięciu na item
        if (event.getCurrentItem() != null && 
            event.getCurrentItem().getType() != Material.AIR && 
            event.getCurrentItem().getType() != Material.WHITE_STAINED_GLASS_PANE) {

            Player player = (Player) event.getWhoClicked();
            player.playSound(player.getLocation(), 
                org.bukkit.Sound.UI_BUTTON_CLICK, 0.3f, 1.2f);
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
