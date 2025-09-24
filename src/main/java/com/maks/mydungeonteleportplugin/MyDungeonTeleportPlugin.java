package com.maks.mydungeonteleportplugin;

import com.maks.mydungeonteleportplugin.commands.SetDungeonDropsCommand;
import com.maks.mydungeonteleportplugin.database.DatabaseManager;
import com.maks.mydungeonteleportplugin.database.DungeonDropDAO;
import com.maks.mydungeonteleportplugin.database.PlayerStatsDAO;
import com.maks.mydungeonteleportplugin.gui.DropPreviewGUIListener;
import com.maks.mydungeonteleportplugin.quests.QuestInteractionListener;
import com.maks.mydungeonteleportplugin.quests.QuestListeners;
import com.maks.mydungeonteleportplugin.quests.QuestManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MyDungeonTeleportPlugin extends JavaPlugin {
    // Quest occupation mapping
    private final HashMap<String, UUID> questOccupied = new HashMap<>();

    // Selected map for each player
    private final HashMap<UUID, String> selectedMap = new HashMap<>();

    // Quest manager
    private QuestManager questManager;

    // Database components
    private DatabaseManager databaseManager;
    private PlayerStatsDAO playerStatsDAO;
    private DungeonDropDAO dungeonDropDAO;

    // Listeners that need DAOs
    private ListenerQ1 listenerQ1;
    private ListenerQ2 listenerQ2;
    private ListenerQ3 listenerQ3;
    private ListenerQ4 listenerQ4;
    private ListenerQ5 listenerQ5;
    private ListenerQ6 listenerQ6;
    private ListenerQ7 listenerQ7;
    private ListenerQ8 listenerQ8;
    private ListenerQ9 listenerQ9;
    private ListenerQ10 listenerQ10;

    private q1PortalListener q1PortalListener;
    private q2PortalListener q2PortalListener;
    private q3PortalListener q3PortalListener;
    private q4PortalListener q4PortalListener;
    private q5PortalListener q5PortalListener;
    private q6PortalListener q6PortalListener;
    private q7PortalListener q7PortalListener;
    private q8PortalListener q8PortalListener;
    private q9PortalListener q9PortalListener;
    private q10PortalListener q10PortalListener;

    @Override
    public void onEnable() {
        // Load configuration
        saveDefaultConfig();

        // Initialize database components
        initializeDatabaseComponents();

        questManager = new QuestManager(this);
        questManager.setPlayerStatsDAO(playerStatsDAO);

        // Register commands
        getCommand("whodoq").setExecutor(new ListOccupiedQuestsCommand(this));
        getCommand("setdungeondrops").setExecutor(new SetDungeonDropsCommand(this, dungeonDropDAO));

        // Utworzenie listenerów
        QuestInteractionListener interactionListener = new QuestInteractionListener(this, questManager);
        QuestListeners questListeners = new QuestListeners(this, questManager, interactionListener);

        // Rejestracja listenerów
        getServer().getPluginManager().registerEvents(questListeners, this);
        getServer().getPluginManager().registerEvents(interactionListener, this);

        // Register global listener for drop preview GUIs
        getServer().getPluginManager().registerEvents(new DropPreviewGUIListener(), this);

        // Register pet damage bonus listener
        getServer().getPluginManager().registerEvents(new DungeonDamageListener(this), this);

        // Register menu and portal listeners for each quest
        registerQuestHandlers();

        getLogger().info("MyDungeonTeleportPlugin enabled with new quest system and database integration!");
    }

    /**
     * Initialize database components
     */
    private void initializeDatabaseComponents() {
        try {
            // Initialize database manager
            databaseManager = new DatabaseManager(this);

            // Initialize DAOs
            playerStatsDAO = new PlayerStatsDAO(this, databaseManager);
            dungeonDropDAO = new DungeonDropDAO(this, databaseManager);

            getLogger().info("Database components initialized successfully!");
        } catch (Exception e) {
            getLogger().severe("Failed to initialize database components: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void registerQuestHandlers() {
        // Q1
        getCommand("q1menu").setExecutor(new q1GUIMenuCommand(this));
        listenerQ1 = new ListenerQ1(this);
        q1PortalListener = new q1PortalListener(this);

        // Set DAOs if available
        if (dungeonDropDAO != null) {
            listenerQ1.setDungeonDropDAO(dungeonDropDAO);
        }
        if (playerStatsDAO != null) {
            q1PortalListener.setPlayerStatsDAO(playerStatsDAO);
        }

        getServer().getPluginManager().registerEvents(listenerQ1, this);
        getServer().getPluginManager().registerEvents(q1PortalListener, this);

        // Q2
        getCommand("q2menu").setExecutor(new q2GUIMenuCommand(this));
        listenerQ2 = new ListenerQ2(this);
        q2PortalListener = new q2PortalListener(this);

        // Set DAOs if available
        if (dungeonDropDAO != null) {
            listenerQ2.setDungeonDropDAO(dungeonDropDAO);
        }
        if (playerStatsDAO != null) {
            q2PortalListener.setPlayerStatsDAO(playerStatsDAO);
        }

        getServer().getPluginManager().registerEvents(listenerQ2, this);
        getServer().getPluginManager().registerEvents(q2PortalListener, this);

        // Q3
        getCommand("q3menu").setExecutor(new q3GUIMenuCommand(this));
        listenerQ3 = new ListenerQ3(this);
        q3PortalListener = new q3PortalListener(this);

        // Set DAOs if available
        if (dungeonDropDAO != null) {
            listenerQ3.setDungeonDropDAO(dungeonDropDAO);
        }
        if (playerStatsDAO != null) {
            q3PortalListener.setPlayerStatsDAO(playerStatsDAO);
        }

        getServer().getPluginManager().registerEvents(listenerQ3, this);
        getServer().getPluginManager().registerEvents(q3PortalListener, this);

        // Q4
        getCommand("q4menu").setExecutor(new q4GUIMenuCommand(this));
        listenerQ4 = new ListenerQ4(this);
        q4PortalListener = new q4PortalListener(this);

        // Set DAOs if available
        if (dungeonDropDAO != null) {
            listenerQ4.setDungeonDropDAO(dungeonDropDAO);
        }
        if (playerStatsDAO != null) {
            q4PortalListener.setPlayerStatsDAO(playerStatsDAO);
        }

        getServer().getPluginManager().registerEvents(listenerQ4, this);
        getServer().getPluginManager().registerEvents(q4PortalListener, this);

        // Q5
        getCommand("q5menu").setExecutor(new q5GUIMenuCommand(this));
        listenerQ5 = new ListenerQ5(this);
        q5PortalListener = new q5PortalListener(this);

        // Set DAOs if available
        if (dungeonDropDAO != null) {
            listenerQ5.setDungeonDropDAO(dungeonDropDAO);
        }
        if (playerStatsDAO != null) {
            q5PortalListener.setPlayerStatsDAO(playerStatsDAO);
        }

        getServer().getPluginManager().registerEvents(listenerQ5, this);
        getServer().getPluginManager().registerEvents(q5PortalListener, this);

        // Q6
        getCommand("q6menu").setExecutor(new q6GUIMenuCommand(this));
        listenerQ6 = new ListenerQ6(this);
        q6PortalListener = new q6PortalListener(this);

        // Set DAOs if available
        if (dungeonDropDAO != null) {
            listenerQ6.setDungeonDropDAO(dungeonDropDAO);
        }
        if (playerStatsDAO != null) {
            q6PortalListener.setPlayerStatsDAO(playerStatsDAO);
        }

        getServer().getPluginManager().registerEvents(listenerQ6, this);
        getServer().getPluginManager().registerEvents(q6PortalListener, this);

        // Q7
        getCommand("q7menu").setExecutor(new q7GUIMenuCommand(this));
        listenerQ7 = new ListenerQ7(this);
        q7PortalListener = new q7PortalListener(this);

        // Set DAOs if available
        if (dungeonDropDAO != null) {
            listenerQ7.setDungeonDropDAO(dungeonDropDAO);
        }
        if (playerStatsDAO != null) {
            q7PortalListener.setPlayerStatsDAO(playerStatsDAO);
        }

        getServer().getPluginManager().registerEvents(listenerQ7, this);
        getServer().getPluginManager().registerEvents(q7PortalListener, this);

        // Q8
        getCommand("q8menu").setExecutor(new q8GUIMenuCommand(this));
        listenerQ8 = new ListenerQ8(this);
        q8PortalListener = new q8PortalListener(this);

        // Set DAOs if available
        if (dungeonDropDAO != null) {
            listenerQ8.setDungeonDropDAO(dungeonDropDAO);
        }
        if (playerStatsDAO != null) {
            q8PortalListener.setPlayerStatsDAO(playerStatsDAO);
        }

        getServer().getPluginManager().registerEvents(listenerQ8, this);
        getServer().getPluginManager().registerEvents(q8PortalListener, this);

        // Q9
        getCommand("q9menu").setExecutor(new q9GUIMenuCommand(this));
        listenerQ9 = new ListenerQ9(this);
        q9PortalListener = new q9PortalListener(this);

        // Set DAOs if available
        if (dungeonDropDAO != null) {
            listenerQ9.setDungeonDropDAO(dungeonDropDAO);
        }
        if (playerStatsDAO != null) {
            q9PortalListener.setPlayerStatsDAO(playerStatsDAO);
        }

        getServer().getPluginManager().registerEvents(listenerQ9, this);
        getServer().getPluginManager().registerEvents(q9PortalListener, this);

        // Q10
        getCommand("q10menu").setExecutor(new q10GUIMenuCommand(this));
        listenerQ10 = new ListenerQ10(this);
        q10PortalListener = new q10PortalListener(this);

        // Set DAOs if available
        if (dungeonDropDAO != null) {
            listenerQ10.setDungeonDropDAO(dungeonDropDAO);
        }
        if (playerStatsDAO != null) {
            q10PortalListener.setPlayerStatsDAO(playerStatsDAO);
        }

        getServer().getPluginManager().registerEvents(listenerQ10, this);
        getServer().getPluginManager().registerEvents(q10PortalListener, this);
    }

    @Override
    public void onDisable() {
        questOccupied.clear();
        selectedMap.clear();

        // Close database connection
        if (databaseManager != null) {
            databaseManager.closePool();
        }

        getLogger().info("MyDungeonTeleportPlugin disabled! All occupied quests have been cleared and database connections closed.");
    }

    // Get the quest manager
    public QuestManager getQuestManager() {
        return questManager;
    }

    // Quest selection methods

    public void setSelectedMap(Player player, String map) {
        selectedMap.put(player.getUniqueId(), map);
    }

    public String getSelectedMap(Player player) {
        return selectedMap.get(player.getUniqueId());
    }

    public void clearSelectedMap(Player player) {
        selectedMap.remove(player.getUniqueId());
    }

    // IPS (wool) handling

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

    // Quest occupation methods

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
        return questOccupied;
    }

    public void releaseQuestForPlayer(UUID playerId) {
        // Find all quests occupied by the player and remove them
        questOccupied.entrySet().removeIf(entry -> entry.getValue().equals(playerId));
    }

    public String getPlayerQuest(UUID playerId) {
        for (Map.Entry<String, UUID> entry : questOccupied.entrySet()) {
            if (entry.getValue().equals(playerId)) {
                return entry.getKey();
            }
        }
        return null;
    }

    // Database component getters

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public PlayerStatsDAO getPlayerStatsDAO() {
        return playerStatsDAO;
    }

    public DungeonDropDAO getDungeonDropDAO() {
        return dungeonDropDAO;
    }
}
