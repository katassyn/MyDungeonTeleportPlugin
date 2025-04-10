package com.maks.mydungeonteleportplugin.quests;

import com.maks.mydungeonteleportplugin.MyDungeonTeleportPlugin;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class QuestListeners implements Listener {
    private final MyDungeonTeleportPlugin plugin;
    private final QuestManager questManager;
    private final QuestInteractionListener interactionListener;
    // Debug flag
    private int debuggingFlag = 1; // Set to 0 when everything is working

    public QuestListeners(MyDungeonTeleportPlugin plugin, QuestManager questManager,
                          QuestInteractionListener interactionListener) {
        this.plugin = plugin;
        this.questManager = questManager;
        this.interactionListener = interactionListener;
    }


    /**
     * Handle player movement for location/portal objectives
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // Only check if the player has actually moved to a new block
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
                event.getFrom().getBlockY() == event.getTo().getBlockY() &&
                event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        Player player = event.getPlayer();
        QuestState state = questManager.getActiveQuest(player.getUniqueId());

        if (state == null) return;

        QuestData.DungeonQuest questData = QuestData.getQuestData(state.getQuestId());
        if (questData == null) return;

        // Check objectives based on current objective type
        switch (state.getCurrentObjective()) {
            case FIND_LOCATION:
                QuestData.LocationInfo locationObj = questData.getLocationObjective(state.getCurrentStage());
                if (locationObj != null && locationObj.isInside(player.getLocation())) {
                    if (!state.isLocationFound()) {
                        questManager.handleLocationFound(player);
                    }
                }
                break;

            case FIND_PORTAL:
                QuestData.LocationInfo portalObj = questData.getPortalObjective(state.getCurrentStage());
                if (portalObj != null && portalObj.isInside(player.getLocation())) {
                    if (!state.isPortalFound()) {
                        questManager.handlePortalFound(player);
                    }
                } else if (state.isWaitingForTeleport()) {
                    // Player left the portal area while waiting
                    state.setPortalFound(false);
                    state.setWaitingForTeleport(false);
                    player.sendMessage(ChatColor.RED + "§l» §r§cYou moved away from the portal! Return to teleport.");
                }
                break;
        }
    }

    /**
     * Handle MythicMobs deaths for quest objectives
     */
    @EventHandler
    public void onMythicMobDeath(MythicMobDeathEvent event) {
        if (!(event.getKiller() instanceof Player)) return;

        Player killer = (Player) event.getKiller();
        String mobId = event.getMobType().getInternalName().toLowerCase();

        if (debuggingFlag == 1) {
            killer.sendMessage(ChatColor.GRAY + "DEBUG: Mob killed: " + mobId);
        }

        questManager.handleMobKill(killer, mobId);
    }

    /**
     * Handle player death during quest
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UUID playerId = player.getUniqueId();

        if (questManager.hasActiveQuest(playerId)) {
            player.sendMessage(ChatColor.RED + "§l» §r§cYou died! Quest failed.");
            questManager.cancelQuest(playerId);

            // Dodaj to wywołanie
            interactionListener.clearPlayerData(playerId);
        }
    }

    /**
     * Handle player quitting during quest
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        if (questManager.hasActiveQuest(playerId)) {
            questManager.cancelQuest(playerId);

            // Dodaj to wywołanie
            interactionListener.clearPlayerData(playerId);
        }
    }
}