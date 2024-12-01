package com.maks.mydungeonteleportplugin;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MyDungeonTeleportPlugin extends JavaPlugin {
    // In MyDungeonTeleportPlugin class
    private Map<String, UUID> occupiedQuests = new HashMap<>();

    private YamlConfiguration dungeonConfig;
    private final HashMap<UUID, String> selectedMap = new HashMap<>(); // Mapowanie graczy na wybrane mapy
    private final HashMap<String, UUID> questOccupied = new HashMap<>();
    @Override
    public void onEnable() {
        // Ładowanie pliku konfiguracyjnego
        saveResource("dungeon_config.yml", false);
        loadConfig(); // Załaduj konfigurację
        getCommand("whodoq").setExecutor(new ListOccupiedQuestsCommand(this));
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        // Rejestracja komend i eventów dla q1
        getCommand("q1menu").setExecutor(new q1GUIMenuCommand(this));
        getServer().getPluginManager().registerEvents(new ListenerQ1(this), this);
        getServer().getPluginManager().registerEvents(new q1PortalListener(this), this);

        // Rejestracja komend i eventów dla q2
        getCommand("q2menu").setExecutor(new q2GUIMenuCommand(this));
        getServer().getPluginManager().registerEvents(new ListenerQ2(this), this);
        getServer().getPluginManager().registerEvents(new q2PortalListener(this), this);

        // Rejestracja komend i eventów dla q3
        getCommand("q3menu").setExecutor(new q3GUIMenuCommand(this));
        getServer().getPluginManager().registerEvents(new ListenerQ3(this), this);
        getServer().getPluginManager().registerEvents(new q3PortalListener(this), this);

        // Rejestracja komend i eventów dla q4
        getCommand("q4menu").setExecutor(new q4GUIMenuCommand(this));
        getServer().getPluginManager().registerEvents(new ListenerQ4(this), this);
        getServer().getPluginManager().registerEvents(new q4PortalListener(this), this);

        // Rejestracja komend i eventów dla q5
        getCommand("q5menu").setExecutor(new q5GUIMenuCommand(this));
        getServer().getPluginManager().registerEvents(new ListenerQ5(this), this);
        getServer().getPluginManager().registerEvents(new q5PortalListener(this), this);

        // Rejestracja komend i eventów dla q6
        getCommand("q6menu").setExecutor(new q6GUIMenuCommand(this));
        getServer().getPluginManager().registerEvents(new ListenerQ6(this), this);
        getServer().getPluginManager().registerEvents(new q6PortalListener(this), this);

        // Rejestracja komend i eventów dla q7
        getCommand("q7menu").setExecutor(new q7GUIMenuCommand(this));
        getServer().getPluginManager().registerEvents(new ListenerQ7(this), this);
        getServer().getPluginManager().registerEvents(new q7PortalListener(this), this);

        // Rejestracja komend i eventów dla q8
        getCommand("q8menu").setExecutor(new q8GUIMenuCommand(this));
        getServer().getPluginManager().registerEvents(new ListenerQ8(this), this);
        getServer().getPluginManager().registerEvents(new q8PortalListener(this), this);

        // Rejestracja komend i eventów dla q9
        getCommand("q9menu").setExecutor(new q9GUIMenuCommand(this));
        getServer().getPluginManager().registerEvents(new ListenerQ9(this), this);
        getServer().getPluginManager().registerEvents(new q9PortalListener(this), this);

        // Rejestracja komend i eventów dla q10
        getCommand("q10menu").setExecutor(new q10GUIMenuCommand(this));
        getServer().getPluginManager().registerEvents(new ListenerQ10(this), this);
        getServer().getPluginManager().registerEvents(new q10PortalListener(this), this);

        getLogger().info("MyDungeonTeleportPlugin enabled!");
    }

    @Override
    public void onDisable() {
        questOccupied.clear(); // Usuwamy wszystkie wpisy

        getLogger().info("MyDungeonTeleportPlugin disabled! All occupied quests have been cleared.");
        getLogger().info("MyDungeonTeleportPlugin disabled!");
    }

    // Pobieranie konfiguracji dungeonu
    public YamlConfiguration getDungeonConfig() {
        return dungeonConfig;
    }

    // Ładowanie konfiguracji YAML
    public void loadConfig() {
        File configFile = new File(getDataFolder(), "dungeon_config.yml");
        if (!configFile.exists()) {
            saveResource("dungeon_config.yml", false);
        }
        dungeonConfig = YamlConfiguration.loadConfiguration(configFile);
        getLogger().info("Dungeon configuration loaded.");
    }

    // Obsługa komendy do przeładowania konfiguracji
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

    // Ustawienie wybranej mapy dla gracza
    public void setSelectedMap(Player player, String map) {
        selectedMap.put(player.getUniqueId(), map);
    }

    // Pobieranie wybranej mapy gracza
    public String getSelectedMap(Player player) {
        return selectedMap.get(player.getUniqueId());
    }

    // Czyszczenie wybranej mapy po teleportacji
    public void clearSelectedMap(Player player) {
        selectedMap.remove(player.getUniqueId());
    }

    // Usunięcie odpowiedniej ilości fragmentów (IPS)
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

    public boolean isQuestOccupied(String questName) {
        return questOccupied.containsKey(questName);
    }

    public void occupyQuest(String questName, UUID playerUUID) {
        questOccupied.put(questName, playerUUID);
    }

    public void releaseQuest(String questName) {
        questOccupied.remove(questName);
    }

    public UUID getQuestOccupant(String questName) {
        return questOccupied.get(questName);
    }

    public HashMap<String, UUID> getOccupiedQuests() {
        return questOccupied; // Zwracamy mapę zajętych questów
    }
    public void releaseQuestForPlayer(UUID playerId) {
        // Remove any entries where the playerId matches
        occupiedQuests.values().removeIf(id -> id.equals(playerId));
    }

}