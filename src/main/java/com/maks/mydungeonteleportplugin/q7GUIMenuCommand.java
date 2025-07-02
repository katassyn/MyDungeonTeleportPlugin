package com.maks.mydungeonteleportplugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.ChatColor;

public class q7GUIMenuCommand implements CommandExecutor {

    private final MyDungeonTeleportPlugin plugin;

    public q7GUIMenuCommand(MyDungeonTeleportPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            openq7Menu(player); // Otwieramy GUI dla q7
            return true;
        }
        return false;
    }

    public void openq7Menu(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, ChatColor.RED + "Q7 Menu");

        gui.setItem(10, createGuiItem(Material.NETHERRACK, ChatColor.BLUE + "Infernal", 
            "Required level: 50", 
            "Cost: 10x Fragment of Infernal Passage (IPS)",
            "",
            ChatColor.YELLOW + "Left-Click: " + ChatColor.WHITE + "Select dungeon",
            ChatColor.YELLOW + "Right-Click: " + ChatColor.WHITE + "View possible drops"));

        gui.setItem(13, createGuiItem(Material.GRAY_CONCRETE, ChatColor.DARK_BLUE + "Hell", 
            "Required level: 65", 
            "Cost: 25x Fragment of Infernal Passage (IPS)",
            "",
            ChatColor.YELLOW + "Left-Click: " + ChatColor.WHITE + "Select dungeon",
            ChatColor.YELLOW + "Right-Click: " + ChatColor.WHITE + "View possible drops"));

        gui.setItem(16, createGuiItem(Material.MAGMA_BLOCK, ChatColor.GOLD + "Bloodshed", 
            "Required level: 80", 
            "Cost: 50x Fragment of Infernal Passage (IPS)",
            "",
            ChatColor.YELLOW + "Left-Click: " + ChatColor.WHITE + "Select dungeon",
            ChatColor.YELLOW + "Right-Click: " + ChatColor.WHITE + "View possible drops"));

        // Wypełnienie pustych miejsc szybami
        for (int i = 0; i < gui.getSize(); i++) {
            if (gui.getItem(i) == null) {
                gui.setItem(i, createGuiItem(Material.WHITE_STAINED_GLASS_PANE, " "));
            }
        }

        player.openInventory(gui);
    }

    private ItemStack createGuiItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(java.util.Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }
}
