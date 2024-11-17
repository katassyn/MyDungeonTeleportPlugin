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

public class q3GUIMenuCommand implements CommandExecutor {

    private final MyDungeonTeleportPlugin plugin;

    public q3GUIMenuCommand(MyDungeonTeleportPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            openq3Menu(player); // Otwieramy GUI dla q3
            return true;
        }
        return false;
    }

    public void openq3Menu(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, ChatColor.RED + "Q3 Menu");

        gui.setItem(10, createGuiItem(Material.WHITE_CONCRETE, ChatColor.BLUE + "Infernal", "Required level: 50", "Cost: 10x Fragment of Infernal Passage (IPS)"));
        gui.setItem(13, createGuiItem(Material.PRISMARINE_BRICKS, ChatColor.DARK_BLUE + "Hell", "Required level: 65", "Cost: 25x Fragment of Infernal Passage (IPS)"));
        gui.setItem(16, createGuiItem(Material.GRAY_CONCRETE, ChatColor.GOLD + "Bloodshed", "Required level: 80", "Cost: 50x Fragment of Infernal Passage (IPS)"));

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
