package com.maks.mydungeonteleportplugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class ListOccupiedQuestsCommand implements CommandExecutor {

    private final MyDungeonTeleportPlugin plugin;

    public ListOccupiedQuestsCommand(MyDungeonTeleportPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by a player.");
            return true;
        }

        Player player = (Player) sender;
        HashMap<String, UUID> occupiedQuests = plugin.getOccupiedQuests(); // Pobieramy zajęte questy

        if (occupiedQuests.isEmpty()) {
            player.sendMessage(ChatColor.GREEN + "All quests are currently free!");
            return true;
        }

        player.sendMessage(ChatColor.YELLOW + "Occupied Quests:");

        for (String questName : occupiedQuests.keySet()) {
            UUID playerUUID = occupiedQuests.get(questName);
            String playerName = plugin.getServer().getOfflinePlayer(playerUUID).getName(); // Pobieramy nazwę gracza

            // Formatowanie wiadomości
            String formattedQuestName = questName;
            int firstUnderscore = questName.indexOf('_');
            int secondUnderscore = questName.indexOf('_', firstUnderscore + 1);

            if (firstUnderscore != -1 && secondUnderscore != -1) {
                // Wycinamy fragment pomiędzy pierwszym a drugim znakiem podkreślenia, łącznie z pierwszym podkreśleniem
                formattedQuestName = questName.substring(0, firstUnderscore) + questName.substring(secondUnderscore);
            }

// Wyświetlanie wiadomości z przekształconą nazwą questa
            player.sendMessage(ChatColor.RED + "Quest: " + ChatColor.AQUA + formattedQuestName + ChatColor.RED + " is occupied by " + ChatColor.GOLD + playerName);
        }

        return true;
    }
}
