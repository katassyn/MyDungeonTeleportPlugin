package com.maks.mydungeonteleportplugin.commands;

import com.maks.mydungeonteleportplugin.MyDungeonTeleportPlugin;
import com.maks.mydungeonteleportplugin.database.DungeonDropDAO;
import com.maks.mydungeonteleportplugin.database.DungeonKeyUtils;
import com.maks.mydungeonteleportplugin.utils.LayoutSuggestionsHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Command to set up drop preview GUIs for dungeons
 */
public class SetDungeonDropsCommand implements CommandExecutor, Listener {
    private final MyDungeonTeleportPlugin plugin;
    private final DungeonDropDAO dungeonDropDAO;

    // Map to track which players are currently editing which dungeon drops
    private final Map<UUID, String> editingSessions = new HashMap<>();

    // Default inventory size for drop preview GUIs (3 rows)
    private static final int DEFAULT_GUI_SIZE = 27;

    public SetDungeonDropsCommand(MyDungeonTeleportPlugin plugin, DungeonDropDAO dungeonDropDAO) {
        this.plugin = plugin;
        this.dungeonDropDAO = dungeonDropDAO;

        // Register the listener
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        // Check if player has permission
        Player player = (Player) sender;
        if (!player.hasPermission("mydungeonplugin.admin")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        // Check for layouts command
        if (args.length == 1 && args[0].equalsIgnoreCase("layouts")) {
            LayoutSuggestionsHelper.showLayoutSuggestions(player);
            return true;
        }

        // Check if command has the correct number of arguments
        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /setdungeondrops <dungeon_key>");
            player.sendMessage(ChatColor.YELLOW + "Example: /setdungeondrops q1_inf");
            player.sendMessage(ChatColor.AQUA + "For layout tips: /setdungeondrops layouts");
            return true;
        }

        // Get the dungeon key from arguments
        String dungeonKey = args[0].toLowerCase();

        // Validate dungeon key format
        if (!isValidDungeonKey(dungeonKey)) {
            player.sendMessage(ChatColor.RED + "Invalid dungeon key format. Use format like 'q1_inf', 'q2_hell', etc.");
            return true;
        }

        // Create or get existing GUI
        Inventory dropGui = dungeonDropDAO.getDropGui(dungeonKey);
        boolean isNewGui = (dropGui == null);

        if (dropGui == null) {
            // Create a new GUI with white glass panes as default
            String title = ChatColor.DARK_GRAY + "» " + ChatColor.GREEN + "Set Drops: " + 
                          ChatColor.WHITE + DungeonKeyUtils.getFormattedDungeonName(dungeonKey);
            dropGui = Bukkit.createInventory(null, DEFAULT_GUI_SIZE, title);

            // Fill with white glass panes
            ItemStack filler = createFillerItem();
            for (int i = 0; i < DEFAULT_GUI_SIZE; i++) {
                dropGui.setItem(i, filler);
            }
        }

        // Open the GUI for the player
        player.openInventory(dropGui);

        // Store the editing session
        editingSessions.put(player.getUniqueId(), dungeonKey);

        // Send instructions
        sendInstructions(player, dungeonKey, isNewGui);

        return true;
    }

    private void sendInstructions(Player player, String dungeonKey, boolean isNewGui) {
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "§l» Setting up drops for " + 
                          DungeonKeyUtils.getFormattedDungeonName(dungeonKey));
        player.sendMessage(ChatColor.YELLOW + "§l» §r§eArrange items as you want them displayed");
        player.sendMessage(ChatColor.YELLOW + "§l» §r§eYou have 3 rows (27 slots) available");
        player.sendMessage(ChatColor.YELLOW + "§l» §r§eEmpty slots will show as white glass");

        if (isNewGui) {
            player.sendMessage("");
            player.sendMessage(ChatColor.AQUA + "§l» §r§bSUGGESTED LAYOUTS:");
            player.sendMessage(ChatColor.GRAY + "  • " + ChatColor.WHITE + "By Rarity: " + 
                             ChatColor.BLUE + "Magic " + ChatColor.GRAY + "→ " + 
                             ChatColor.DARK_PURPLE + "Extra " + ChatColor.GRAY + "→ " + 
                             ChatColor.GOLD + "Legend " + ChatColor.GRAY + "→ " + 
                             ChatColor.LIGHT_PURPLE + "Unique " + ChatColor.GRAY + "→ " + 
                             ChatColor.DARK_RED + "MYTHIC");
            player.sendMessage(ChatColor.GRAY + "  • " + ChatColor.WHITE + "Diamond: " + 
                             ChatColor.GRAY + "Center best item (slot 13), surround with others");
            player.sendMessage(ChatColor.GRAY + "  • " + ChatColor.WHITE + "Rows: " + 
                             ChatColor.GRAY + "Group by type (weapons/armor/consumables)");
            player.sendMessage(ChatColor.DARK_GRAY + "  Type §f/setdungeondrops layouts §7for detailed tips!");
        }

        player.sendMessage("");
        player.sendMessage(ChatColor.GREEN + "§l» §r§aClose the inventory to save changes");
        player.sendMessage("");
    }

    private ItemStack createFillerItem() {
        ItemStack filler = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
        ItemMeta meta = filler.getItemMeta();
        meta.setDisplayName(" ");
        filler.setItemMeta(meta);
        return filler;
    }

    /**
     * Handle inventory close event to save drop GUIs
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;

        Player player = (Player) event.getPlayer();
        UUID playerId = player.getUniqueId();

        // Check if player was editing a drop GUI
        if (editingSessions.containsKey(playerId)) {
            String dungeonKey = editingSessions.get(playerId);
            Inventory inventory = event.getInventory();

            // Analyze the layout
            LayoutAnalysis analysis = analyzeLayout(inventory);

            // Save the drop GUI with exact layout
            dungeonDropDAO.saveDropGui(dungeonKey, inventory);

            // Remove the editing session
            editingSessions.remove(playerId);

            // Notify the player with analysis
            showSaveConfirmation(player, dungeonKey, analysis);
        }
    }

    private LayoutAnalysis analyzeLayout(Inventory inventory) {
        LayoutAnalysis analysis = new LayoutAnalysis();

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null && item.getType() != Material.AIR && 
                item.getType() != Material.WHITE_STAINED_GLASS_PANE) {
                analysis.itemCount++;
                analysis.usedSlots.add(i);

                // Analyze rarity distribution
                String rarity = getRarityFromItem(item);
                if (rarity != null) {
                    analysis.rarityCount.merge(rarity, 1, Integer::sum);
                }
            }
        }

        // Detect pattern
        analysis.detectedPattern = detectPattern(analysis.usedSlots);

        return analysis;
    }

    private String getRarityFromItem(ItemStack item) {
        if (!item.hasItemMeta() || !item.getItemMeta().hasLore()) return null;

        List<String> lore = item.getItemMeta().getLore();
        for (String line : lore) {
            String clean = ChatColor.stripColor(line).toLowerCase();
            if (clean.contains("rarity:")) {
                if (clean.contains("mythic")) return "MYTHIC";
                if (clean.contains("unique")) return "Unique";
                if (clean.contains("legendary")) return "Legendary";
                if (clean.contains("extraordinary")) return "Extraordinary";
                if (clean.contains("magic")) return "Magic";
            }
        }
        return null;
    }

    private String detectPattern(Set<Integer> usedSlots) {
        // Check for common patterns
        if (isDiamondPattern(usedSlots)) return "Diamond";
        if (isSingleRow(usedSlots)) return "Single Row";
        if (isVerticalColumns(usedSlots)) return "Vertical Columns";
        if (isCrossPattern(usedSlots)) return "Cross";
        return "Custom";
    }

    private boolean isDiamondPattern(Set<Integer> slots) {
        Set<Integer> diamond = new HashSet<>(Arrays.asList(4, 12, 13, 14, 22));
        return slots.size() <= 5 && slots.containsAll(diamond);
    }

    private boolean isSingleRow(Set<Integer> slots) {
        return slots.stream().allMatch(s -> s >= 9 && s <= 17) ||
               slots.stream().allMatch(s -> s >= 0 && s <= 8) ||
               slots.stream().allMatch(s -> s >= 18 && s <= 26);
    }

    private boolean isVerticalColumns(Set<Integer> slots) {
        // Check if items are arranged in vertical columns
        Map<Integer, Integer> columns = new HashMap<>();
        for (int slot : slots) {
            int col = slot % 9;
            columns.merge(col, 1, Integer::sum);
        }
        return columns.values().stream().anyMatch(count -> count >= 2);
    }

    private boolean isCrossPattern(Set<Integer> slots) {
        Set<Integer> cross = new HashSet<>(Arrays.asList(
            1, 3, 5, 7, 10, 12, 13, 14, 16, 19, 21, 23, 25
        ));
        return slots.size() >= 5 && cross.containsAll(slots);
    }

    private void showSaveConfirmation(Player player, String dungeonKey, LayoutAnalysis analysis) {
        player.sendMessage("");
        player.sendMessage(ChatColor.GREEN + "§l» §r§aDrop preview saved successfully!");
        player.sendMessage(ChatColor.YELLOW + "§l» §r§eDungeon: " + 
                          DungeonKeyUtils.getFormattedDungeonName(dungeonKey));

        if (analysis.itemCount > 0) {
            player.sendMessage(ChatColor.YELLOW + "§l» §r§eItems: " + analysis.itemCount);
            player.sendMessage(ChatColor.YELLOW + "§l» §r§ePattern: " + analysis.detectedPattern);

            // Show rarity distribution if available
            if (!analysis.rarityCount.isEmpty()) {
                player.sendMessage(ChatColor.YELLOW + "§l» §r§eRarity distribution:");
                analysis.rarityCount.forEach((rarity, count) -> {
                    ChatColor color = getRarityColor(rarity);
                    player.sendMessage(ChatColor.GRAY + "  • " + color + rarity + 
                                     ChatColor.GRAY + ": " + count);
                });
            }

            // Give feedback on layout
            if (analysis.itemCount <= 5 && !analysis.detectedPattern.equals("Diamond")) {
                player.sendMessage(ChatColor.GRAY + "§l» §r§7TIP: Try diamond pattern for few items!");
            } else if (analysis.detectedPattern.equals("Custom")) {
                player.sendMessage(ChatColor.GRAY + "§l» §r§7Nice custom layout!");
            } else {
                player.sendMessage(ChatColor.GRAY + "§l» §r§7Great " + analysis.detectedPattern + " layout!");
            }
        } else {
            player.sendMessage(ChatColor.GRAY + "§l» §r§7Empty layout saved (only glass panes)");
        }

        player.sendMessage("");
    }

    private ChatColor getRarityColor(String rarity) {
        switch (rarity.toLowerCase()) {
            case "magic": return ChatColor.BLUE;
            case "extraordinary": return ChatColor.DARK_PURPLE;
            case "legendary": return ChatColor.GOLD;
            case "unique": return ChatColor.LIGHT_PURPLE;
            case "mythic": return ChatColor.DARK_RED;
            default: return ChatColor.WHITE;
        }
    }

    // Inner class for layout analysis
    private static class LayoutAnalysis {
        int itemCount = 0;
        Set<Integer> usedSlots = new HashSet<>();
        Map<String, Integer> rarityCount = new HashMap<>();
        String detectedPattern = "Custom";
    }

    /**
     * Validate dungeon key format
     * @param dungeonKey The dungeon key to validate
     * @return True if the dungeon key is valid, false otherwise
     */
    private boolean isValidDungeonKey(String dungeonKey) {
        // Pattern: q1_inf, q2_hell, q10_blood, etc.
        return dungeonKey.matches("q\\d+_(inf|hell|blood)");
    }
}
