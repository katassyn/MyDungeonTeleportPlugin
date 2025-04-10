package com.maks.mydungeonteleportplugin;

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

    @Override
    public void onEnable() {
        questManager = new QuestManager(this);

        // Register commands
        getCommand("whodoq").setExecutor(new ListOccupiedQuestsCommand(this));

        // Utworzenie listenerów
        QuestInteractionListener interactionListener = new QuestInteractionListener(this, questManager);
        QuestListeners questListeners = new QuestListeners(this, questManager, interactionListener);

        // Rejestracja listenerów
        getServer().getPluginManager().registerEvents(questListeners, this);
        getServer().getPluginManager().registerEvents(interactionListener, this);

        // Register menu and portal listeners for each quest
        registerQuestHandlers();

        getLogger().info("MyDungeonTeleportPlugin enabled with new quest system!");
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
}