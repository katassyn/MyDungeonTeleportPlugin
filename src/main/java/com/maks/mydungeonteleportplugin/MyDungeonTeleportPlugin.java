package com.maks.mydungeonteleportplugin;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MyDungeonTeleportPlugin extends JavaPlugin {
    // Use a single map for quest occupation
    private final HashMap<String, UUID> questOccupied = new HashMap<>();
    private final HashMap<UUID, String> selectedMap = new HashMap<>();
    private YamlConfiguration dungeonConfig;

    @Override
    public void onEnable() {
        // Load configuration
        saveResource("dungeon_config.yml", false);
        loadConfig();

        // Register commands
        getCommand("whodoq").setExecutor(new ListOccupiedQuestsCommand(this));

        // Register global listeners
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);

        // Register quest-specific listeners and commands
        registerQuestHandlers();

        getLogger().info("MyDungeonTeleportPlugin enabled!");
    }

    private void registerQuestHandlers() {
        // Q1
        getCommand("q1menu").setExecutor(new q1GUIMenuCommand(this));
        getServer().getPluginManager().registerEvents(new ListenerQ1(this), this);
        getServer().getPluginManager().registerEvents(new q1PortalListener(this), this);

        // Q2
        getCommand("q2menu").setExecutor(new q2GUIMenuCommand(this));
        getServer().getPluginManager().registerEvents(new ListenerQ2(this), this);
        getServer().getPluginManager().registerEvents(new q2PortalListener(this), this);

        // Q3
        getCommand("q3menu").setExecutor(new q3GUIMenuCommand(this));
        getServer().getPluginManager().registerEvents(new ListenerQ3(this), this);
        getServer().getPluginManager().registerEvents(new q3PortalListener(this), this);

        // Q4
        getCommand("q4menu").setExecutor(new q4GUIMenuCommand(this));
        getServer().getPluginManager().registerEvents(new ListenerQ4(this), this);
        getServer().getPluginManager().registerEvents(new q4PortalListener(this), this);

        // Q5
        getCommand("q5menu").setExecutor(new q5GUIMenuCommand(this));
        getServer().getPluginManager().registerEvents(new ListenerQ5(this), this);
        getServer().getPluginManager().registerEvents(new q5PortalListener(this), this);

        // Q6
        getCommand("q6menu").setExecutor(new q6GUIMenuCommand(this));
        getServer().getPluginManager().registerEvents(new ListenerQ6(this), this);
        getServer().getPluginManager().registerEvents(new q6PortalListener(this), this);

        // Q7
        getCommand("q7menu").setExecutor(new q7GUIMenuCommand(this));
        getServer().getPluginManager().registerEvents(new ListenerQ7(this), this);
        getServer().getPluginManager().registerEvents(new q7PortalListener(this), this);

        // Q8
        getCommand("q8menu").setExecutor(new q8GUIMenuCommand(this));
        getServer().getPluginManager().registerEvents(new ListenerQ8(this), this);
        getServer().getPluginManager().registerEvents(new q8PortalListener(this), this);

        // Q9
        getCommand("q9menu").setExecutor(new q9GUIMenuCommand(this));
        getServer().getPluginManager().registerEvents(new ListenerQ9(this), this);
        getServer().getPluginManager().registerEvents(new q9PortalListener(this), this);

        // Q10
        getCommand("q10menu").setExecutor(new q10GUIMenuCommand(this));
        getServer().getPluginManager().registerEvents(new ListenerQ10(this), this);
        getServer().getPluginManager().registerEvents(new q10PortalListener(this), this);
    }

    @Override
    public void onDisable() {
        questOccupied.clear();
        selectedMap.clear();
        getLogger().info("MyDungeonTeleportPlugin disabled! All occupied quests have been cleared.");
    }

    // Loading configuration
    public void loadConfig() {
        File configFile = new File(getDataFolder(), "dungeon_config.yml");
        if (!configFile.exists()) {
            saveResource("dungeon_config.yml", false);
        }
        dungeonConfig = YamlConfiguration.loadConfiguration(configFile);
        getLogger().info("Dungeon configuration loaded.");
    }

    // Get dungeon configuration
    public YamlConfiguration getDungeonConfig() {
        return dungeonConfig;
    }

    // Command for reloading configuration
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("dung_tps")) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                loadConfig();
                sender.sendMessage("Dungeon configuration reloaded!");
                return true;
            }
        }
        return false;
    }

    // Set selected map for player
    public void setSelectedMap(Player player, String map) {
        selectedMap.put(player.getUniqueId(), map);
    }

    // Get selected map for player
    public String getSelectedMap(Player player) {
        return selectedMap.get(player.getUniqueId());
    }

    // Clear selected map
    public void clearSelectedMap(Player player) {
        selectedMap.remove(player.getUniqueId());
    }

    // Remove IPS (wool) from player inventory
    public void removeWool(Player player, int amountToRemove) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.IRON_NUGGET) {
                int itemAmount = item.getAmount();
                if (itemAmount > amountToRemove) {
                    item.setAmount(itemAmount - amountToRemove);
                    break;
                } else {
                    player.getInventory().remove(item);
                    amountToRemove -= itemAmount;
                }
            }
            if (amountToRemove <= 0) {
                break;
            }
        }
    }

    // Check if quest is occupied
    public boolean isQuestOccupied(String questName) {
        return questOccupied.containsKey(questName);
    }

    // Occupy a quest
    public void occupyQuest(String questName, UUID playerUUID) {
        questOccupied.put(questName, playerUUID);
    }

    // Release a quest
    public void releaseQuest(String questName) {
        questOccupied.remove(questName);
    }

    // Get quest occupant
    public UUID getQuestOccupant(String questName) {
        return questOccupied.get(questName);
    }

    // Get all occupied quests
    public HashMap<String, UUID> getOccupiedQuests() {
        return questOccupied;
    }

    // Release all quests for a player
    public void releaseQuestForPlayer(UUID playerId) {
        // Find all quests occupied by the player and remove them
        questOccupied.entrySet().removeIf(entry -> entry.getValue().equals(playerId));
    }

    // Find which quest a player is occupying
    public String getPlayerQuest(UUID playerId) {
        for (Map.Entry<String, UUID> entry : questOccupied.entrySet()) {
            if (entry.getValue().equals(playerId)) {
                return entry.getKey();
            }
        }
        return null;
    }
}