package com.maks.mydungeonteleportplugin.gui;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Global listener for all drop preview GUIs
 * This avoids having to register each GUI instance as a listener
 */
public class DropPreviewGUIListener implements Listener {
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Check if the inventory holder is one of our GUI classes
        if (event.getInventory().getHolder() instanceof DropPreviewGUI || 
            event.getInventory().getHolder() instanceof FlexibleDropPreviewGUI) {
            
            // Cancel the event to prevent taking items
            event.setCancelled(true);
            
            // Handle click on item (not glass pane)
            if (event.getCurrentItem() != null && 
                event.getCurrentItem().getType() != Material.AIR && 
                event.getCurrentItem().getType() != Material.GRAY_STAINED_GLASS_PANE &&
                event.getCurrentItem().getType() != Material.WHITE_STAINED_GLASS_PANE) {
                
                Player player = (Player) event.getWhoClicked();
                ItemStack clickedItem = event.getCurrentItem();
                
                // Play sound effect
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.3f, 1.2f);
                
                // Show item name in action bar if it has a display name
                if (clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasDisplayName()) {
                    player.sendMessage(ChatColor.GRAY + "» " + ChatColor.YELLOW + "Viewing: " + 
                                     clickedItem.getItemMeta().getDisplayName());
                }
            }
        }
    }
    
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        // Check if the inventory holder is one of our GUI classes
        if (event.getInventory().getHolder() instanceof DropPreviewGUI || 
            event.getInventory().getHolder() instanceof FlexibleDropPreviewGUI) {
            
            // Cancel the event to prevent dragging items
            event.setCancelled(true);
        }
    }
}