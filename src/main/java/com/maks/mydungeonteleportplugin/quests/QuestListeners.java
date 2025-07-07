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
import org.bukkit.metadata.FixedMetadataValue;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class QuestListeners implements Listener {
    private final MyDungeonTeleportPlugin plugin;
    private final QuestManager questManager;
    private final QuestInteractionListener interactionListener;
    // Debug flag
    private final Map<UUID, Long> lastWarningTime = new HashMap<>();
    private final Map<UUID, Long> lastDebugTime = new HashMap<>();
    private final Map<UUID, Long> lastAltarDebugTime = new HashMap<>();

    // Add this as a class field near the top of QuestListeners class
    private static final String ALTAR1_COMPLETION_KEY = "altar1_completion_message";
    private static final String ALTAR2_COMPLETION_KEY = "altar2_completion_message";

    private int debuggingFlag = 0; // Set to 0 to disable debug messages

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

        // Get entity name
        LivingEntity target = (LivingEntity) event.getEntity();
        String entityName = target.getCustomName();
        if (entityName == null) return;

        // Sprawdź dla questów Q3
        if (state.getQuestId().startsWith("q3_")) {
            // Sprawdź czy jesteśmy w pierwszym etapie
            if (state.getCurrentStage() == 1) {
                QuestData.DungeonQuest questData = QuestData.getQuestData(state.getQuestId());
                if (questData == null) return;

                // Pobierz nazwę bossa z questu
                String bossId = questData.getBossObjective(1);
                if (bossId == null) return;

                // Sprawdź czy nazwa zawiera kluczowy fragment z ID bossa
                // (to uproszczone podejście, możliwe do zmiany zależnie od struktury nazw MythicMobs)
                if (matchesBossName(entityName, bossId) && state.isMiniBossInvulnerable()) {
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
        // Check for Q9 quests
        else if (state.getQuestId().startsWith("q9_")) {
            // Stage 1 - Asterion protection
            if (state.getCurrentStage() == 1 && entityName.toLowerCase().contains("asterion")) {
                if (!state.hasCollectedAllStatues()) {
                    event.setCancelled(true);

                    long now = System.currentTimeMillis();
                    if (!lastWarningTime.containsKey(player.getUniqueId()) ||
                            now - lastWarningTime.getOrDefault(player.getUniqueId(), 0L) > 5000) {

                        player.sendMessage(ChatColor.RED + "§l» §r§cAsterion is protected by ancient magic! Collect all statue fragments first!");
                        player.sendMessage(ChatColor.YELLOW + "§l» §r§eStatue fragments: " + 
                                         state.getStatueFragmentsCollected() + "/4");
                        lastWarningTime.put(player.getUniqueId(), now);

                        if (debuggingFlag == 1) {
                            player.sendMessage(ChatColor.GRAY + "DEBUG: Damage to Asterion cancelled - need statue fragments");
                        }
                    }
                }
            }
            // Stage 2 - Ebicarus protection
            else if (state.getCurrentStage() == 2 && entityName.toLowerCase().contains("ebicarus")) {
                if (!state.hasActivatedAllAltars()) {
                    event.setCancelled(true);

                    long now = System.currentTimeMillis();
                    if (!lastWarningTime.containsKey(player.getUniqueId()) ||
                            now - lastWarningTime.getOrDefault(player.getUniqueId(), 0L) > 5000) {

                        player.sendMessage(ChatColor.RED + "§l» §r§cEbicarus is protected by metronome seals! Activate all metronomes first!");
                        player.sendMessage(ChatColor.YELLOW + "§l» §r§eMetronomes activated: " + 
                                         state.getAltarsActivated() + "/5");
                        lastWarningTime.put(player.getUniqueId(), now);

                        if (debuggingFlag == 1) {
                            player.sendMessage(ChatColor.GRAY + "DEBUG: Damage to Ebicarus cancelled - need metronome activations");
                        }
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

        // Add this at the beginning of the onPlayerMove method, after getting the QuestState
        if (state != null && state.getQuestId().startsWith("q7_") && state.getCurrentStage() == 2 && debuggingFlag == 1) {
            // Only log periodically to avoid spam (every 20 seconds)
            long now = System.currentTimeMillis();
            if (!lastDebugTime.containsKey(player.getUniqueId()) || 
                    now - lastDebugTime.getOrDefault(player.getUniqueId(), 0L) > 20000) {
                lastDebugTime.put(player.getUniqueId(), now);

                player.sendMessage(ChatColor.GRAY + "DEBUG: Q7 Stage 2 current state:");
                player.sendMessage(ChatColor.GRAY + "DEBUG: - Objective: " + state.getCurrentObjective());
                player.sendMessage(ChatColor.GRAY + "DEBUG: - Altar1 activated: " + state.isAltar1Activated());
                player.sendMessage(ChatColor.GRAY + "DEBUG: - Altar2 activated: " + state.isAltar2Activated());

                String difficulty = state.getQuestId().substring(3);
                player.sendMessage(ChatColor.GRAY + "DEBUG: - Kills: " + 
                    state.getKillCount("wild_razorclaw_" + difficulty) + " wild, " +
                    state.getKillCount("flamescale_defender_" + difficulty) + " flamescale, " +
                    state.getKillCount("fireclaw_mercenary_" + difficulty) + " fireclaw");

                // Calculate distance to both altars
                org.bukkit.Location playerLoc = player.getLocation();
                double distToAltar1 = getDistanceToAltar(playerLoc, difficulty, 1);
                double distToAltar2 = getDistanceToAltar(playerLoc, difficulty, 2);

                player.sendMessage(ChatColor.GRAY + "DEBUG: Player location: " + 
                    playerLoc.getBlockX() + "," + playerLoc.getBlockY() + "," + playerLoc.getBlockZ());
                player.sendMessage(ChatColor.GRAY + "DEBUG: Distance to altar1: " + distToAltar1 + 
                    " blocks (needs < 10 blocks to activate)");
                player.sendMessage(ChatColor.GRAY + "DEBUG: Distance to altar2: " + distToAltar2 + 
                    " blocks (needs < 10 blocks to activate)");
            }
        }

        // Handle Q7 altar areas
        if (state.getQuestId().startsWith("q7_") && state.getCurrentStage() == 2) {
            String difficulty = state.getQuestId().substring(3); // inf, hell, or blood

            // Check if player is near an altar
            if (isNearAltar1(player.getLocation(), difficulty)) {
                if (!state.isAltar1Activated()) {
                    // Activate altar 1 and spawn mobs
                    spawnMobsAtAltar(player, 1, difficulty, 5);
                    state.setAltar1Activated(true);

                    player.sendTitle(
                        ChatColor.RED + "Altar 1 Activated!",
                        ChatColor.YELLOW + "Defeat the summoned enemies",
                        10, 70, 20
                    );

                    player.sendMessage(ChatColor.GOLD + "§l✦ ════════════════════════ ✦");
                    player.sendMessage(ChatColor.RED + "§l» §r§cThe first altar has been activated!");
                    player.sendMessage(ChatColor.YELLOW + "§l» §r§eDefeat all enemies to proceed.");
                    player.sendMessage(ChatColor.GOLD + "§l✦ ════════════════════════ ✦");

                    if (debuggingFlag == 1) {
                        player.sendMessage(ChatColor.GRAY + "DEBUG: Altar 1 activated, spawned 5 of each mob type");
                        player.sendMessage(ChatColor.GRAY + "DEBUG: Required kills: 5 of each mob type");
                    }
                }
            } else if (isNearAltar2(player.getLocation(), difficulty)) {
                // Only activate Altar 2 if Altar 1 was completed
                if (state.isAltar1Activated() && areAltar1KillsComplete(state, difficulty) && 
                    !state.isAltar2Activated()) {

                    // Activate altar 2 and spawn mobs
                    spawnMobsAtAltar(player, 2, difficulty, 10);
                    state.setAltar2Activated(true);

                    player.sendTitle(
                        ChatColor.RED + "Altar 2 Activated!",
                        ChatColor.YELLOW + "Defeat the summoned enemies",
                        10, 70, 20
                    );

                    player.sendMessage(ChatColor.GOLD + "§l✦ ════════════════════════ ✦");
                    player.sendMessage(ChatColor.RED + "§l» §r§cThe second altar has been activated!");
                    player.sendMessage(ChatColor.YELLOW + "§l» §r§eDefeat all enemies to proceed.");
                    player.sendMessage(ChatColor.GOLD + "§l✦ ════════════════════════ ✦");

                    // Dodaj tę flagę, aby zapobiec pokazywaniu "Location Found!"
                    player.setMetadata("altar2_activated", new FixedMetadataValue(plugin, true));

                    if (debuggingFlag == 1) {
                        player.sendMessage(ChatColor.GRAY + "DEBUG: Altar 2 activated, spawned 10 of each mob type");
                        player.sendMessage(ChatColor.GRAY + "DEBUG: Required total kills: 15 of each mob type");
                        player.sendMessage(ChatColor.GRAY + "DEBUG: Current kills: " + 
                            state.getKillCount("wild_razorclaw_" + difficulty) + " wild, " +
                            state.getKillCount("flamescale_defender_" + difficulty) + " flamescale, " +
                            state.getKillCount("fireclaw_mercenary_" + difficulty) + " fireclaw");
                    }
                } else if (!state.isAltar1Activated() || !areAltar1KillsComplete(state, difficulty)) {
                    // Only show message every 5 seconds to avoid spam
                    long now = System.currentTimeMillis();
                    if (!lastWarningTime.containsKey(player.getUniqueId()) ||
                            now - lastWarningTime.getOrDefault(player.getUniqueId(), 0L) > 5000) {

                        player.sendMessage(ChatColor.RED + "You must activate and complete the first altar before this one!");

                        if (debuggingFlag == 1) {
                            player.sendMessage(ChatColor.GRAY + "DEBUG: Altar 1 status: " + 
                                (state.isAltar1Activated() ? "Activated" : "Not activated"));
                            if (state.isAltar1Activated()) {
                                player.sendMessage(ChatColor.GRAY + "DEBUG: Altar 1 kills: " + 
                                    state.getKillCount("wild_razorclaw_" + difficulty) + "/5 wild, " +
                                    state.getKillCount("flamescale_defender_" + difficulty) + "/5 flamescale, " +
                                    state.getKillCount("fireclaw_mercenary_" + difficulty) + "/5 fireclaw");
                            }
                        }

                        lastWarningTime.put(player.getUniqueId(), now);
                    }
                }
            }

            // Check if altar 1 mobs are all killed and show message to find altar 2
            if (state.getQuestId().startsWith("q7_") && state.getCurrentStage() == 2 
                    && state.isAltar1Activated() && !state.isAltar2Activated() 
                    && areAltar1KillsComplete(state, difficulty)) {

                // Prevent message spam by using metadata
                if (!player.hasMetadata(ALTAR1_COMPLETION_KEY)) {
                    player.setMetadata(ALTAR1_COMPLETION_KEY, 
                            new FixedMetadataValue(plugin, true));

                    player.sendTitle(
                        ChatColor.GREEN + "Altar 1 Cleared!",
                        ChatColor.YELLOW + "Find the second altar",
                        10, 70, 20
                    );

                    player.sendMessage(ChatColor.GOLD + "§l✦ ════════════════════════ ✦");
                    player.sendMessage(ChatColor.GREEN + "§l» §r§aYou've cleared all enemies from the first altar!");
                    player.sendMessage(ChatColor.YELLOW + "§l» §r§eNow find the second altar to continue.");
                    player.sendMessage(ChatColor.GOLD + "§l✦ ════════════════════════ ✦");

                    if (debuggingFlag == 1) {
                        player.sendMessage(ChatColor.GRAY + "DEBUG: Altar 1 mobs all killed, player should find altar 2");
                        player.sendMessage(ChatColor.GRAY + "DEBUG: Kill counts: " + 
                            state.getKillCount("wild_razorclaw_" + difficulty) + " wild, " +
                            state.getKillCount("flamescale_defender_" + difficulty) + " flamescale, " +
                            state.getKillCount("fireclaw_mercenary_" + difficulty) + " fireclaw");
                    }
                }
            }

            // Only show debug info about altar 2 completion status
            if (debuggingFlag == 1) {
                long now = System.currentTimeMillis();
                if (!lastAltarDebugTime.containsKey(player.getUniqueId()) || 
                        now - lastAltarDebugTime.getOrDefault(player.getUniqueId(), 0L) > 30000) {
                    lastAltarDebugTime.put(player.getUniqueId(), now);
                    player.sendMessage(ChatColor.GRAY + "DEBUG: Checking altar status in onPlayerMove:");
                    player.sendMessage(ChatColor.GRAY + "DEBUG: - Altar 1 activated: " + state.isAltar1Activated());
                    player.sendMessage(ChatColor.GRAY + "DEBUG: - Altar 1 kills complete: " + areAltar1KillsComplete(state, difficulty));
                    player.sendMessage(ChatColor.GRAY + "DEBUG: - Altar 2 activated: " + state.isAltar2Activated());
                    player.sendMessage(ChatColor.GRAY + "DEBUG: - Altar 2 kills complete: " + areAltar2KillsComplete(state, difficulty));
                    player.sendMessage(ChatColor.GRAY + "DEBUG: - Current objective: " + state.getCurrentObjective());
                    player.sendMessage(ChatColor.GRAY + "DEBUG: - Kill counts: " + 
                        state.getKillCount("wild_razorclaw_" + difficulty) + " wild, " +
                        state.getKillCount("flamescale_defender_" + difficulty) + " flamescale, " +
                        state.getKillCount("fireclaw_mercenary_" + difficulty) + " fireclaw");
                }
            }

            // Note: The mini-boss spawning logic has been moved to onMythicMobDeath
            // to ensure it only happens once when the required number of mobs are killed
        }

        QuestData.DungeonQuest questData = QuestData.getQuestData(state.getQuestId());
        if (questData == null) return;

        // Check objectives based on current objective type
        switch (state.getCurrentObjective()) {
            case FIND_LOCATION:
                QuestData.LocationInfo locationObj = questData.getLocationObjective(state.getCurrentStage());
                if (locationObj != null && locationObj.isInside(player.getLocation())) {
                    if (!state.isLocationFound()) {
                        // Special handling for Q7 Altar areas
                        if (state.getQuestId().startsWith("q7_") && state.getCurrentStage() == 2) {
                            // Location handling is done above, just mark as found
                            if (debuggingFlag == 1) {
                                player.sendMessage(ChatColor.GRAY + "DEBUG: Q7 location found, handled by altar logic");
                            }
                        }

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

    // Helper methods for Q7 quest
    private boolean isNearAltar1(org.bukkit.Location loc, String difficulty) {
        if (difficulty.equals("inf")) {
            return isNearLocation(loc, -3679, -920, 10);
        } else if (difficulty.equals("hell")) {
            return isNearLocation(loc, -3702, -1693, 10);
        } else if (difficulty.equals("blood")) {
            return isNearLocation(loc, -3724, -2462, 10);
        }
        return false;
    }

    private boolean isNearAltar2(org.bukkit.Location loc, String difficulty) {
        if (difficulty.equals("inf")) {
            return isNearLocation(loc, -3759, -881, 10);
        } else if (difficulty.equals("hell")) {
            return isNearLocation(loc, -3782, -1652, 10);
        } else if (difficulty.equals("blood")) {
            return isNearLocation(loc, -3804, -2418, 10);
        }
        return false;
    }

    private boolean isNearLocation(org.bukkit.Location loc, int x, int z, int radius) {
        int playerX = loc.getBlockX();
        int playerZ = loc.getBlockZ();
        return Math.pow(playerX - x, 2) + Math.pow(playerZ - z, 2) <= Math.pow(radius, 2);
    }

    private double getDistanceToAltar(org.bukkit.Location playerLoc, String difficulty, int altarNum) {
        int altarX, altarZ;

        if (difficulty.equals("inf")) {
            if (altarNum == 1) {
                altarX = -3679; altarZ = -920;
            } else {
                altarX = -3759; altarZ = -881;
            }
        } else if (difficulty.equals("hell")) {
            if (altarNum == 1) {
                altarX = -3702; altarZ = -1693;
            } else {
                altarX = -3782; altarZ = -1652;
            }
        } else { // blood
            if (altarNum == 1) {
                altarX = -3724; altarZ = -2462;
            } else {
                altarX = -3804; altarZ = -2418;
            }
        }

        double dx = playerLoc.getBlockX() - altarX;
        double dz = playerLoc.getBlockZ() - altarZ;

        return Math.sqrt(dx*dx + dz*dz);
    }

    private boolean areAltar1KillsComplete(QuestState state, String difficulty) {
        // Pobierz gracza i dodaj flagę czasową, aby ograniczyć wywołania
        Player player = org.bukkit.Bukkit.getPlayer(state.getPlayerId());

        if (player != null) {
            // Sprawdź czy minęło wystarczająco dużo czasu od ostatniego sprawdzenia
            long now = System.currentTimeMillis();
            if (player.hasMetadata("altar1_check_time")) {
                long lastCheck = player.getMetadata("altar1_check_time").get(0).asLong();
                if (now - lastCheck < 2000) { // 2 sekundy
                    // Użyj zapisanego wyniku zamiast ponownego obliczania
                    return player.hasMetadata("altar1_complete") && 
                           player.getMetadata("altar1_complete").get(0).asBoolean();
                }
            }

            // Zapisz czas sprawdzenia
            player.setMetadata("altar1_check_time", new FixedMetadataValue(plugin, now));
        }

        // For altar 1, we need 80% of 15 mobs killed (12 mobs)
        // We only count kills if altar 1 is activated
        if (!state.isAltar1Activated()) {
            return false;
        }

        String wildRazorclawId = "wild_razorclaw_" + difficulty;
        String flamescaleDefenderId = "flamescale_defender_" + difficulty;
        String fireclawMercenaryId = "fireclaw_mercenary_" + difficulty;

        int wildKills = state.getKillCount(wildRazorclawId);
        int flamescaleKills = state.getKillCount(flamescaleDefenderId);
        int fireclawKills = state.getKillCount(fireclawMercenaryId);

        // Total kills across all mob types
        int totalKills = wildKills + flamescaleKills + fireclawKills;

        // Require 80% of total mobs (12 out of 15 total)
        boolean complete = totalKills >= 12;

        // Zapisz wynik sprawdzenia
        if (player != null) {
            player.setMetadata("altar1_complete", new FixedMetadataValue(plugin, complete));
        }

        // Always log for debugging
        if (debuggingFlag == 1 && player != null) {
            player.sendMessage(ChatColor.GRAY + "DEBUG: areAltar1KillsComplete check - " + 
                totalKills + "/15 total kills (" + wildKills + " wild, " + 
                flamescaleKills + " flamescale, " + fireclawKills + " fireclaw) - Complete: " + complete);
        }

        return complete;
    }

    private boolean areAltar2KillsComplete(QuestState state, String difficulty) {
        // Pobierz gracza i dodaj flagę czasową, aby ograniczyć wywołania
        Player player = org.bukkit.Bukkit.getPlayer(state.getPlayerId());

        if (player != null) {
            // Sprawdź czy minęło wystarczająco dużo czasu od ostatniego sprawdzenia
            long now = System.currentTimeMillis();
            if (player.hasMetadata("altar2_check_time")) {
                long lastCheck = player.getMetadata("altar2_check_time").get(0).asLong();
                if (now - lastCheck < 2000) { // 2 sekundy
                    // Użyj zapisanego wyniku zamiast ponownego obliczania
                    return player.hasMetadata("altar2_complete") && 
                           player.getMetadata("altar2_complete").get(0).asBoolean();
                }
            }

            // Zapisz czas sprawdzenia
            player.setMetadata("altar2_check_time", new FixedMetadataValue(plugin, now));
        }

        // For altar 2, we need 80% of 30 mobs killed (24 mobs)
        // We only count kills if altar 2 is activated and altar 1 is completed
        if (!state.isAltar2Activated() || !areAltar1KillsComplete(state, difficulty)) {
            return false;
        }

        String wildRazorclawId = "wild_razorclaw_" + difficulty;
        String flamescaleDefenderId = "flamescale_defender_" + difficulty;
        String fireclawMercenaryId = "fireclaw_mercenary_" + difficulty;

        int wildKills = state.getKillCount(wildRazorclawId);
        int flamescaleKills = state.getKillCount(flamescaleDefenderId);
        int fireclawKills = state.getKillCount(fireclawMercenaryId);

        // Total kills across all mob types
        int totalAllKills = wildKills + flamescaleKills + fireclawKills;

        // For altar 2, we need to count kills after altar 1 was completed
        // We know altar 1 needed 12 kills, so we subtract those from the total
        int altar2Kills = Math.max(0, totalAllKills - 15); // Subtract the 15 kills from altar 1

        // Require 80% of altar 2 mobs (24 out of 30)
        boolean complete = altar2Kills >= 24;

        // Zapisz wynik sprawdzenia
        if (player != null) {
            player.setMetadata("altar2_complete", new FixedMetadataValue(plugin, complete));
        }

        // Always log for debugging
        if (debuggingFlag == 1 && player != null) {
            player.sendMessage(ChatColor.GRAY + "DEBUG: areAltar2KillsComplete check - " + 
                altar2Kills + "/30 altar 2 kills (total: " + totalAllKills + " kills, " +
                wildKills + " wild, " + flamescaleKills + " flamescale, " + 
                fireclawKills + " fireclaw) - Complete: " + complete);
        }

        return complete;
    }

    private void showAltarProgress(Player player, QuestState state, String difficulty, int altarNum) {
        // Check if we've already shown 100% progress for this altar
        String metadataKey = "altar" + altarNum + "_progress_complete";
        if (player.hasMetadata(metadataKey)) {
            return; // Don't show progress messages anymore for this altar
        }

        // Check if we've already shown the completion message for this altar
        String completionMsgKey = altarNum == 1 ? ALTAR1_COMPLETION_KEY : ALTAR2_COMPLETION_KEY;

        String wildRazorclawId = "wild_razorclaw_" + difficulty;
        String flamescaleDefenderId = "flamescale_defender_" + difficulty;
        String fireclawMercenaryId = "fireclaw_mercenary_" + difficulty;

        int wildKills = state.getKillCount(wildRazorclawId);
        int flamescaleKills = state.getKillCount(flamescaleDefenderId);
        int fireclawKills = state.getKillCount(fireclawMercenaryId);

        // Calculate total kills and required kills
        int totalKills;
        int requiredKills;
        int maxKills;

        if (altarNum == 1) {
            // For altar 1, we count all kills
            totalKills = wildKills + flamescaleKills + fireclawKills;
            requiredKills = 12; // 80% of 15 total mobs
            maxKills = 15;
        } else {
            // For altar 2, we need to count kills after altar 1 was completed
            int totalAllKills = wildKills + flamescaleKills + fireclawKills;
            totalKills = Math.max(0, totalAllKills - 15); // Subtract the 15 kills from altar 1
            requiredKills = 24; // 80% of 30 total mobs
            maxKills = 30;
        }

        // Calculate actual percentage (max 100%)
        double actualPercent = Math.min(100, (totalKills * 100.0) / maxKills);

        // For display, scale from 0-80% to 0-100% for a smoother visual experience
        // If we've reached the required threshold (80%), show 100%
        double displayPercent;
        if (totalKills >= requiredKills) {
            displayPercent = 100;
        } else {
            // Scale from 0-requiredKills to 0-100%
            displayPercent = (totalKills * 100.0) / requiredKills;
        }
        int roundedPercent = (int) Math.round(displayPercent);

        // Create a visual progress bar similar to other quests
        StringBuilder bar = new StringBuilder();
        int totalBars = 10;
        int filledBars = Math.min(totalBars, (int)Math.ceil((double)roundedPercent / 100 * totalBars));

        bar.append(ChatColor.GREEN);
        for (int i = 0; i < filledBars; i++) {
            bar.append("■");
        }

        bar.append(ChatColor.GRAY);
        for (int i = filledBars; i < totalBars; i++) {
            bar.append("■");
        }

        // Show progress message
        player.sendMessage(ChatColor.GOLD + "§l» §r§eAltar " + altarNum + " Clearing Progress: " + 
                          ChatColor.GREEN + roundedPercent + "% " + bar.toString());

        // Show detailed progress if needed
        if (totalKills >= requiredKills && !player.hasMetadata(completionMsgKey)) {
            player.sendMessage(ChatColor.GREEN + "§l» §r§aThe altar is sufficiently cleared! You can proceed.");

            // Set metadata to indicate we've shown the completion message
            player.setMetadata(completionMsgKey, new FixedMetadataValue(plugin, true));

            // Set metadata to indicate we've reached 100% for this altar
            player.setMetadata(metadataKey, new FixedMetadataValue(plugin, true));

            if (debuggingFlag == 1) {
                player.sendMessage(ChatColor.GRAY + "DEBUG: Actual progress: " + 
                    (int)Math.round(actualPercent) + "%, showing 100% to player");
                player.sendMessage(ChatColor.GRAY + "DEBUG: Altar " + altarNum + " kills: " + totalKills + 
                    "/" + maxKills + " (" + (altarNum == 2 ? "Including kills after altar 1: " : "") +
                    wildKills + " wild, " + flamescaleKills + " flamescale, " + fireclawKills + " fireclaw)");
            }
        }
    }

    private void spawnMobsAtAltar(Player player, int altarNum, String difficulty, int count) {
        // Get coordinates based on altar number and difficulty
        int x, y, z;

        if (difficulty.equals("inf")) {
            if (altarNum == 1) {
                x = -3679; y = -61; z = -920;
            } else {
                x = -3759; y = -61; z = -881;
            }
        } else if (difficulty.equals("hell")) {
            if (altarNum == 1) {
                x = -3702; y = -61; z = -1693;
            } else {
                x = -3782; y = -61; z = -1652;
            }
        } else { // blood
            if (altarNum == 1) {
                x = -3724; y = -61; z = -2462;
            } else {
                x = -3804; y = -61; z = -2418;
            }
        }

        // Spawn mobs with better positioning
        String world = player.getWorld().getName();

        if (debuggingFlag == 1) {
            player.sendMessage(ChatColor.GRAY + "DEBUG: Spawning mobs at altar " + altarNum + 
                    " (" + x + "," + y + "," + z + ") in world " + world);
            player.sendMessage(ChatColor.GRAY + "DEBUG: Difficulty: " + difficulty + 
                    ", spawning " + count + " of each mob type");
        }

        // Spawn each mob individually with varied positions
        for (int i = 0; i < count; i++) {
            // Spawn wild_razorclaw with random offset
            double angle1 = Math.random() * 2 * Math.PI;
            double radius1 = 3 + Math.random() * 2; // 3-5 blocks radius
            int offsetX1 = (int)(Math.cos(angle1) * radius1);
            int offsetZ1 = (int)(Math.sin(angle1) * radius1);
            int elevation1 = 1 + (int)(Math.random() * 2); // 1-2 blocks elevation

            String cmd1 = "mm mobs spawn wild_razorclaw_" + difficulty + ":1 1 " + 
                    world + "," + (x + offsetX1) + "," + (y + elevation1) + "," + (z + offsetZ1) + ",0,0";
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), cmd1);

            // Spawn flamescale_defender with different random offset
            double angle2 = Math.random() * 2 * Math.PI;
            double radius2 = 3 + Math.random() * 2;
            int offsetX2 = (int)(Math.cos(angle2) * radius2);
            int offsetZ2 = (int)(Math.sin(angle2) * radius2);
            int elevation2 = 1 + (int)(Math.random() * 2);

            String cmd2 = "mm mobs spawn flamescale_defender_" + difficulty + ":1 1 " + 
                    world + "," + (x + offsetX2) + "," + (y + elevation2) + "," + (z + offsetZ2) + ",0,0";
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), cmd2);

            // Spawn fireclaw_mercenary with another random offset
            double angle3 = Math.random() * 2 * Math.PI;
            double radius3 = 3 + Math.random() * 2;
            int offsetX3 = (int)(Math.cos(angle3) * radius3);
            int offsetZ3 = (int)(Math.sin(angle3) * radius3);
            int elevation3 = 1 + (int)(Math.random() * 2);

            String cmd3 = "mm mobs spawn fireclaw_mercenary_" + difficulty + ":1 1 " + 
                    world + "," + (x + offsetX3) + "," + (y + elevation3) + "," + (z + offsetZ3) + ",0,0";
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), cmd3);

            if (debuggingFlag == 1 && i == 0) {
                // Only log the first spawn to avoid spam
                player.sendMessage(ChatColor.GRAY + "DEBUG: Spawned mobs with commands:");
                player.sendMessage(ChatColor.GRAY + cmd1);
                player.sendMessage(ChatColor.GRAY + cmd2);
                player.sendMessage(ChatColor.GRAY + cmd3);
            }
        }

        if (debuggingFlag == 1) {
            player.sendMessage(ChatColor.GRAY + "DEBUG: Spawned " + (count * 3) + " mobs at altar " + altarNum);
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

            // Special debug for Q7 quest tracking
            QuestState q7State = questManager.getActiveQuest(killer.getUniqueId());
            if (q7State != null && q7State.getQuestId().startsWith("q7_") && q7State.getCurrentStage() == 2) {
                String difficulty = q7State.getQuestId().substring(3);

                // Check if this is a Q7 altar mob
                if (mobId.startsWith("wild_razorclaw_") || 
                    mobId.startsWith("flamescale_defender_") || 
                    mobId.startsWith("fireclaw_mercenary_")) {

                    // Show current counts BEFORE this kill is processed
                    killer.sendMessage(ChatColor.GRAY + "DEBUG: Before kill - Altar 1: " + 
                        (q7State.isAltar1Activated() ? "Active" : "Inactive") + 
                        ", Altar 2: " + (q7State.isAltar2Activated() ? "Active" : "Inactive"));

                    killer.sendMessage(ChatColor.GRAY + "DEBUG: Before kill - Counts: " + 
                        q7State.getKillCount("wild_razorclaw_" + difficulty) + " wild, " +
                        q7State.getKillCount("flamescale_defender_" + difficulty) + " flamescale, " +
                        q7State.getKillCount("fireclaw_mercenary_" + difficulty) + " fireclaw");
                }
            }

            // Special debug for Q7 mini-boss
            if (mobId.contains("commander_embersword")) {
                killer.sendMessage(ChatColor.GRAY + "DEBUG: Q7 mini-boss killed: " + mobId);
            }

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

        // Handle item collection for Q3, Q6, Q7, and Q8 quests
        interactionListener.handlePossibleItemDrop(killer, mobId);

        // Special handling for Q7 quest mobs - only count kills if the corresponding altar is activated
        QuestState state = questManager.getActiveQuest(killer.getUniqueId());
        if (state != null && state.getQuestId().startsWith("q7_") && state.getCurrentStage() == 2) {
            String difficulty = state.getQuestId().substring(3); // inf, hell, or blood

            // Check if this is a Q7 altar mob
            if (mobId.startsWith("wild_razorclaw_") || 
                mobId.startsWith("flamescale_defender_") || 
                mobId.startsWith("fireclaw_mercenary_")) {

                // Only count kills if at least one altar is activated
                if (!state.isAltar1Activated() && !state.isAltar2Activated()) {
                    if (debuggingFlag == 1) {
                        killer.sendMessage(ChatColor.GRAY + "DEBUG: Kill not counted - no altars activated yet");
                    }
                    return; // Don't count the kill
                }

                // Count the kill and show progress
                state.incrementKillCount(mobId);

                // Add debug message to show the kill count
                if (debuggingFlag == 1) {
                    killer.sendMessage(ChatColor.GRAY + "DEBUG: Incremented kill count for " + mobId + 
                                      " to " + state.getKillCount(mobId));
                }

                // Show altar progress after each kill
                if (state.isAltar1Activated() && !state.isAltar2Activated()) {
                    // Show altar 1 progress
                    showAltarProgress(killer, state, difficulty, 1);

                    // Check if altar 1 is complete - we'll handle the message in the dedicated section below
                    if (areAltar1KillsComplete(state, difficulty)) {
                        // Just mark it as complete, the message will be shown below
                        if (!killer.hasMetadata(ALTAR1_COMPLETION_KEY)) {
                            killer.setMetadata(ALTAR1_COMPLETION_KEY, 
                                    new FixedMetadataValue(plugin, true));

                            if (debuggingFlag == 1) {
                                killer.sendMessage(ChatColor.GRAY + "DEBUG: Altar 1 completion detected in progress check");
                            }
                        }
                    }
                } else if (state.isAltar2Activated()) {
                    // Show altar 2 progress
                    showAltarProgress(killer, state, difficulty, 2);
                }

                // Check if altar 1 is now complete
                if (state.isAltar1Activated() && !state.isAltar2Activated() && 
                    areAltar1KillsComplete(state, difficulty)) {

                    // Show completed altar 1 message (only once)
                    if (!killer.hasMetadata(ALTAR1_COMPLETION_KEY)) {
                        killer.setMetadata(ALTAR1_COMPLETION_KEY, new FixedMetadataValue(plugin, true));

                        killer.sendTitle(
                            ChatColor.GREEN + "Altar 1 Cleared!",
                            ChatColor.YELLOW + "Find the second altar",
                            10, 70, 20
                        );

                        killer.sendMessage(ChatColor.GOLD + "§l✦ ════════════════════════ ✦");
                        killer.sendMessage(ChatColor.GREEN + "§l» §r§aYou've cleared all enemies from the first altar!");
                        killer.sendMessage(ChatColor.YELLOW + "§l» §r§eNow find the second altar to continue.");
                        killer.sendMessage(ChatColor.GOLD + "§l✦ ════════════════════════ ✦");

                        if (debuggingFlag == 1) {
                            killer.sendMessage(ChatColor.GRAY + "DEBUG: Altar 1 kill threshold reached: " + 
                                state.getKillCount("wild_razorclaw_" + difficulty) + " wild, " +
                                state.getKillCount("flamescale_defender_" + difficulty) + " flamescale, " +
                                state.getKillCount("fireclaw_mercenary_" + difficulty) + " fireclaw");
                        }
                    }
                }

                // Check if altar 2 is now complete
                if (state.isAltar2Activated() && areAltar2KillsComplete(state, difficulty) &&
                        !killer.hasMetadata("altar2_complete_boss_spawned")) {

                    killer.setMetadata("altar2_complete_boss_spawned", new FixedMetadataValue(plugin, true));

                    // Reset mob timers to spawn mini-boss
                    String resetCommand = "mm s resettimers g:q7_" + difficulty + "_m2_mini";
                    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), resetCommand);

                    // Change objective to kill boss
                    state.setCurrentObjective(QuestState.QuestObjective.KILL_BOSS);

                    // Get boss details from quest data
                    QuestData.DungeonQuest questData = QuestData.getQuestData(state.getQuestId());
                    if (questData != null) {
                        QuestData.BossObjective bossObj = questData.getBossObjectiveDetails(state.getCurrentStage());

                        killer.sendTitle(
                            ChatColor.GREEN + "Altars Cleared!",
                            ChatColor.RED + "Defeat " + (bossObj != null ? bossObj.getDisplayName() : "the boss"),
                            10, 70, 20
                        );

                        killer.sendMessage(ChatColor.GOLD + "§l✦ ════════════════════════ ✦");
                        killer.sendMessage(ChatColor.GREEN + "§l» §r§aYou've cleared all enemies from both altars!");
                        if (bossObj != null) {
                            killer.sendMessage(ChatColor.RED + "§l» §r§c" + bossObj.getObjectiveText());
                        } else {
                            killer.sendMessage(ChatColor.RED + "§l» §r§cDefeat the fortress commander!");
                        }
                        killer.sendMessage(ChatColor.GOLD + "§l✦ ════════════════════════ ✦");

                        if (debuggingFlag == 1) {
                            killer.sendMessage(ChatColor.GRAY + "DEBUG: Altar 2 kill threshold reached, spawning mini-boss");
                            killer.sendMessage(ChatColor.GRAY + "DEBUG: Executed command: " + resetCommand);
                        }
                    }
                }

                return; // We've already handled the kill
            }
        }

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
