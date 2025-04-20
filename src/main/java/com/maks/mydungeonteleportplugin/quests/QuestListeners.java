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
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.entity.LivingEntity;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class QuestListeners implements Listener {
    private final MyDungeonTeleportPlugin plugin;
    private final QuestManager questManager;
    private final QuestInteractionListener interactionListener;
    // Debug flag
    private final Map<UUID, Long> lastWarningTime = new HashMap<>();

    private int debuggingFlag = 1; // Set to 0 when everything is working

    public QuestListeners(MyDungeonTeleportPlugin plugin, QuestManager questManager,
                          QuestInteractionListener interactionListener) {
        this.plugin = plugin;
        this.questManager = questManager;
        this.interactionListener = interactionListener;
    }
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof LivingEntity)) return;

        Player player = (Player) event.getDamager();
        UUID entityUUID = event.getEntity().getUniqueId();

        // Pobierz aktywny quest gracza
        QuestState state = questManager.getActiveQuest(player.getUniqueId());
        if (state == null) return;

        // Sprawdź tylko dla questów Q3
        if (!state.getQuestId().startsWith("q3_")) return;

        // Sprawdź czy jesteśmy w pierwszym etapie
        if (state.getCurrentStage() == 1) {
            QuestData.DungeonQuest questData = QuestData.getQuestData(state.getQuestId());
            if (questData == null) return;

            // Pobierz nazwę bossa z questu
            String bossId = questData.getBossObjective(1);
            if (bossId == null) return;

            // Sprawdź, czy entity ma odpowiedni display name (uproszczone sprawdzenie)
            LivingEntity target = (LivingEntity) event.getEntity();
            String entityName = target.getCustomName();

            // Sprawdź czy nazwa zawiera kluczowy fragment z ID bossa
            // (to uproszczone podejście, możliwe do zmiany zależnie od struktury nazw MythicMobs)
            if (entityName != null && matchesBossName(entityName, bossId) && state.isMiniBossInvulnerable()) {
                // Anuluj obrażenia i pokaż wiadomość
                event.setCancelled(true);

                long now = System.currentTimeMillis();
                if (!lastWarningTime.containsKey(player.getUniqueId()) ||
                        now - lastWarningTime.getOrDefault(player.getUniqueId(), 0L) > 5000) {

                    player.sendMessage(ChatColor.RED + "§l» §r§cThe Evil Miller is protected by a spell! You need to collect and grind undead bones first!");
                    lastWarningTime.put(player.getUniqueId(), now);

                    if (debuggingFlag == 1) {
                        player.sendMessage(ChatColor.GRAY + "DEBUG: Damage to mini-boss cancelled - still protected");
                    }
                }
            }
        }
    }

    // Pomocnicza metoda do porównywania nazwy bossa
    private boolean matchesBossName(String entityName, String bossId) {
        // Przekształć ID bossa na bardziej czytelną formę (podobnie jak w formatMobName)
        String bossName = formatBossNameFromId(bossId);
        return entityName.contains(bossName);
    }

    private String formatBossNameFromId(String bossId) {
        // Podobna logika do formatMobName, ale uproszczona
        String[] parts = bossId.split("_");
        StringBuilder name = new StringBuilder();

        for (String part : parts) {
            if (part.equals("inf") || part.equals("hell") || part.equals("blood")) {
                continue;
            }

            if (name.length() > 0) {
                name.append(" ");
            }

            name.append(Character.toUpperCase(part.charAt(0)));
            name.append(part.substring(1));
        }

        return name.toString();
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

            // Special debug for Q6 mini-boss
            if (mobId.contains("murot_high_priest")) {
                killer.sendMessage(ChatColor.GRAY + "DEBUG: Q6 mini-boss killed: " + mobId);

                // Get quest state and check if we're in the right phase
                QuestState state = questManager.getActiveQuest(killer.getUniqueId());
                if (state != null && state.getQuestId().startsWith("q6_") && state.getCurrentStage() == 2) {
                    killer.sendMessage(ChatColor.GRAY + "DEBUG: Current objective: " + state.getCurrentObjective());

                    // Force transition to portal phase if needed
                    if (state.getCurrentObjective() == QuestState.QuestObjective.KILL_BOSS) {
                        state.setBossKilled(true);
                        state.advanceToNextObjective();
                        killer.sendMessage(ChatColor.GRAY + "DEBUG: Forced transition to: " + state.getCurrentObjective());

                        // Show portal instructions
                        QuestData.DungeonQuest questData = QuestData.getQuestData(state.getQuestId());
                        if (questData != null) {
                            QuestData.LocationInfo portalInfo = questData.getPortalObjective(2);
                            if (portalInfo != null) {
                                killer.sendMessage(ChatColor.GRAY + "DEBUG: Portal location: " +
                                        portalInfo.getX1() + "," + portalInfo.getY1() + "," + portalInfo.getZ1());
                            }
                        }
                    }
                }
            }
        }

        // Handle item collection for Q3/Q6 quest
        interactionListener.handlePossibleItemDrop(killer, mobId);

        // Handle regular mob kill objectives
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