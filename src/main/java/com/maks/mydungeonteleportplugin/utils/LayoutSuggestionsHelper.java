package com.maks.mydungeonteleportplugin.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Pomocnik do sugerowania układów itemów w GUI dropów
 */
public class LayoutSuggestionsHelper {
    
    /**
     * Wyświetla sugestie układu dla admina
     */
    public static void showLayoutSuggestions(Player player) {
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "§l═══ SUGGESTED LAYOUTS ═══");
        player.sendMessage("");
        
        // Layout 1: By Rarity (Horizontal)
        player.sendMessage(ChatColor.YELLOW + "§l1. By Rarity (Horizontal):");
        player.sendMessage(ChatColor.GRAY + "   Row 1: " + ChatColor.BLUE + "Magic items");
        player.sendMessage(ChatColor.GRAY + "   Row 2: " + ChatColor.DARK_PURPLE + "Extraordinary" + 
                          ChatColor.GRAY + " & " + ChatColor.GOLD + "Legendary");
        player.sendMessage(ChatColor.GRAY + "   Row 3: " + ChatColor.LIGHT_PURPLE + "Unique" + 
                          ChatColor.GRAY + " & " + ChatColor.DARK_RED + "MYTHIC");
        player.sendMessage("");
        
        // Layout 2: By Rarity (Vertical)
        player.sendMessage(ChatColor.YELLOW + "§l2. By Rarity (Vertical):");
        player.sendMessage(ChatColor.GRAY + "   Columns from left to right:");
        player.sendMessage(ChatColor.GRAY + "   " + ChatColor.BLUE + "Magic " + ChatColor.GRAY + "→ " +
                          ChatColor.DARK_PURPLE + "Extra " + ChatColor.GRAY + "→ " +
                          ChatColor.GOLD + "Legend " + ChatColor.GRAY + "→ " +
                          ChatColor.LIGHT_PURPLE + "Unique " + ChatColor.GRAY + "→ " +
                          ChatColor.DARK_RED + "MYTHIC");
        player.sendMessage("");
        
        // Layout 3: Centered Diamond
        player.sendMessage(ChatColor.YELLOW + "§l3. Diamond Pattern (for few items):");
        player.sendMessage(ChatColor.GRAY + "   Place most important item in center (slot 13)");
        player.sendMessage(ChatColor.GRAY + "   Surround with other items in diamond shape");
        player.sendMessage(ChatColor.GRAY + "   Slots: 4, 12, 13, 14, 22");
        player.sendMessage("");
        
        // Layout 4: By Type
        player.sendMessage(ChatColor.YELLOW + "§l4. By Item Type:");
        player.sendMessage(ChatColor.GRAY + "   Row 1: Weapons & Tools");
        player.sendMessage(ChatColor.GRAY + "   Row 2: Armor pieces");
        player.sendMessage(ChatColor.GRAY + "   Row 3: Consumables & Materials");
        player.sendMessage("");
        
        // Visual grid reference
        player.sendMessage(ChatColor.YELLOW + "§l5. Grid Reference:");
        player.sendMessage(ChatColor.DARK_GRAY + "   ┌─────────────────┐");
        player.sendMessage(ChatColor.DARK_GRAY + "   │ " + ChatColor.GRAY + "0  1  2  3  4  5  6  7  8" + ChatColor.DARK_GRAY + " │");
        player.sendMessage(ChatColor.DARK_GRAY + "   │ " + ChatColor.GRAY + "9 10 11 12 13 14 15 16 17" + ChatColor.DARK_GRAY + " │");
        player.sendMessage(ChatColor.DARK_GRAY + "   │ " + ChatColor.GRAY + "18 19 20 21 22 23 24 25 26" + ChatColor.DARK_GRAY + " │");
        player.sendMessage(ChatColor.DARK_GRAY + "   └─────────────────┘");
        player.sendMessage("");
        
        // Tips
        player.sendMessage(ChatColor.AQUA + "§lTIPS:");
        player.sendMessage(ChatColor.GRAY + "• Leave some white glass between item groups");
        player.sendMessage(ChatColor.GRAY + "• Put best items in center or corners");
        player.sendMessage(ChatColor.GRAY + "• Use patterns for visual appeal");
        player.sendMessage(ChatColor.GRAY + "• Consider player scanning patterns (left to right)");
        player.sendMessage("");
    }
    
    /**
     * Sugeruje układ na podstawie liczby itemów
     */
    public static String suggestLayout(int itemCount) {
        if (itemCount <= 5) {
            return "Diamond pattern (centered) - slots 4, 12, 13, 14, 22";
        } else if (itemCount <= 9) {
            return "Single row (middle) - slots 9-17";
        } else if (itemCount <= 15) {
            return "Two rows with spacing - use rows 1 & 3";
        } else {
            return "Full layout - organize by rarity or type";
        }
    }
    
    /**
     * Przykładowe sloty dla układu diamentowego
     */
    public static int[] getDiamondSlots() {
        return new int[] {4, 12, 13, 14, 22};
    }
    
    /**
     * Przykładowe sloty dla układu krzyża
     */
    public static int[] getCrossSlots() {
        return new int[] {1, 3, 5, 7, 10, 12, 13, 14, 16, 19, 21, 23, 25};
    }
    
    /**
     * Sloty środkowego rzędu
     */
    public static int[] getMiddleRowSlots() {
        return new int[] {9, 10, 11, 12, 13, 14, 15, 16, 17};
    }
}