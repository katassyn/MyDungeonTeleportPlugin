package com.maks.mydungeonteleportplugin.quests;

import com.maks.mydungeonteleportplugin.MyDungeonTeleportPlugin;
import com.maks.mydungeonteleportplugin.database.DungeonKeyUtils;
import com.maks.mydungeonteleportplugin.database.PlayerStatsDAO;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class QuestManager {
    private final MyDungeonTeleportPlugin plugin;
    private final Map<UUID, QuestState> activeQuests = new HashMap<>();
    private final Map<UUID, BukkitTask> questTimers = new HashMap<>();
    private final Map<UUID, Long> questStartTimes = new HashMap<>();
    private PlayerStatsDAO playerStatsDAO;

    // Debug flag
    private int debuggingFlag = 0; // Set to 0 when everything is working

    public QuestManager(MyDungeonTeleportPlugin plugin) {
        this.plugin = plugin;
    }

    public void setPlayerStatsDAO(PlayerStatsDAO playerStatsDAO) {
        this.playerStatsDAO = playerStatsDAO;
    }

    /**
     * Start a new quest for a player
     */
    public boolean startQuest(Player player, String questId) {
        UUID playerId = player.getUniqueId();

        // Check if player already has an active quest
        if (activeQuests.containsKey(playerId)) {
            player.sendMessage(ChatColor.RED + "You already have an active quest!");
            return false;
        }

        // Get quest data
        QuestData.DungeonQuest questData = QuestData.getQuestData(questId);
        if (questData == null) {
            player.sendMessage(ChatColor.RED + "Unknown quest: " + questId);
            return false;
        }

        // Check level requirement
        if (player.getLevel() < questData.getRequiredLevel()) {
            player.sendMessage(ChatColor.RED + "You need to be at least level " + questData.getRequiredLevel() + " to start this quest!");
            return false;
        }

        // Check if quest is already occupied
        if (plugin.isQuestOccupied(questId)) {
            player.sendMessage(ChatColor.RED + "This quest is already occupied by another player!");
            return false;
        }

        // Create new quest state
        QuestState questState = new QuestState(playerId, questId);
        activeQuests.put(playerId, questState);

        // Record quest start time for tracking completion time
        questStartTimes.put(playerId, System.currentTimeMillis());
        if (questId.startsWith("q2_")) {
            questState.setLocationFound(true);
            // Losujemy ilość czerwonych i brązowych grzybów (suma = 5)
            int redMushrooms = ThreadLocalRandom.current().nextInt(1, 5); // 1-4 czerwone
            int brownMushrooms = 5 - redMushrooms; // reszta to brązowe
            questState.setMushroomRequirements(redMushrooms, brownMushrooms);
            questState.advanceToNextObjective();
        }
        // Inside startQuest method, after creating questState
        else if (questId.startsWith("q3_")) {
            questState.setLocationFound(true);

            // Skip to collection phase
            questState.advanceToNextObjective();

            // Set required items based on quest data
            if (questData != null && questData.hasCollectObjectives(1)) {
                Map<String, QuestData.CollectObjective> objectives = questData.getCollectObjectives(1);
                if (!objectives.isEmpty()) {
                    // Just take the first objective's count
                    QuestData.CollectObjective objective = objectives.values().iterator().next();
                    questState.setRequiredItems(objective.getCount());
                }
            }
        } else if (questId.startsWith("q4_")) {
            questState.setLocationFound(true);
            // Skip to kill mobs phase
            questState.advanceToNextObjective();
        }else if (questId.startsWith("q5_")) {
            questState.setLocationFound(true);
            // Skip to kill mobs phase
            questState.advanceToNextObjective();

            if (debuggingFlag == 1) {
                player.sendMessage(ChatColor.GRAY + "DEBUG: Started Q5 quest, skipping to kill phase");
            }
        }else if (questId.startsWith("q6_")) {
            questState.setLocationFound(true);
            // Skip to kill mobs phase
            questState.advanceToNextObjective();
        } else if (questId.startsWith("q7_")) {
            questState.setLocationFound(true);

            // If we're starting directly on m2 (after warp), set up for altar phase
            if (player.getWorld().getName().contains("m2")) {
                // Advance to stage 2
                questState.advanceToNextStage();
                // Set objective to find location (altar)
                questState.setCurrentObjective(QuestState.QuestObjective.FIND_LOCATION);

                // Add this explicit notification
                player.sendTitle(
                    ChatColor.YELLOW + "First Objective:",
                    ChatColor.AQUA + "Find the first altar and activate it",
                    10, 70, 20
                );

                player.sendMessage(ChatColor.GOLD + "§l✦ ════════════════════════ ✦");
                player.sendMessage(ChatColor.YELLOW + "§l» §r§eQ7 Quest Stage 2 Started!");
                player.sendMessage(ChatColor.YELLOW + "§l» §r§eObjectives:");
                player.sendMessage(ChatColor.YELLOW + "  • §r§eFind the first altar and kill surrounding mobs");
                player.sendMessage(ChatColor.YELLOW + "  • §r§eThen find the second altar and kill surrounding mobs");
                player.sendMessage(ChatColor.YELLOW + "  • §r§eDefeat the fortress commander that appears");
                player.sendMessage(ChatColor.GOLD + "§l✦ ════════════════════════ ✦");

                if (debuggingFlag == 1) {
                    player.sendMessage(ChatColor.GRAY + "DEBUG: Started Q7 quest on m2, prepared for altar phases");
                    player.sendMessage(ChatColor.GRAY + "DEBUG: Find Altar 1 to begin");
                }
            } else {
                // Normal m1 start
                questState.setCurrentObjective(QuestState.QuestObjective.COLLECT_FROM_MOBS);

                if (debuggingFlag == 1) {
                    player.sendMessage(ChatColor.GRAY + "DEBUG: Started Q7 quest, skipping to collection phase");
                }
            }
        } else if (questId.startsWith("q8_")) {
            questState.setLocationFound(true);

            // Skip to collection phase - need to set objective explicitly
            questState.setCurrentObjective(QuestState.QuestObjective.COLLECT_FROM_MOBS);

        } else if (questId.startsWith("q9_")) {
            questState.setLocationFound(true);

            // Skip to statue collection phase
            questState.setCurrentObjective(QuestState.QuestObjective.INTERACT_WITH_BLOCKS);

            // Randomly select 4 statues out of 8
            java.util.Set<Integer> selectedStatues = new java.util.HashSet<>();
            while (selectedStatues.size() < 4) {
                selectedStatues.add(ThreadLocalRandom.current().nextInt(0, 8));
            }
            questState.setSelectedStatues(selectedStatues);

            if (debuggingFlag == 1) {
                player.sendMessage(ChatColor.GRAY + "DEBUG: Started Q9 quest, selected statues: " + selectedStatues);
            }

        } else if (questId.startsWith("q10_")) {
            questState.setLocationFound(true);

            // Skip to fragment collection phase
            questState.setCurrentObjective(QuestState.QuestObjective.INTERACT_WITH_BLOCKS);

            if (debuggingFlag == 1) {
                player.sendMessage(ChatColor.GRAY + "DEBUG: Started Q10 quest, skipping to fragment collection phase");
            }
        }
        // Mark quest as occupied
        plugin.occupyQuest(questId, playerId);

        // Schedule timeout task (30 minutes)
        BukkitTask timeoutTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                player.sendMessage(ChatColor.RED + "Time's up! You've failed the quest.");
                // Teleport player to spawn instead of killing them
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spawn " + player.getName());
                cancelQuest(playerId);
            }
        }, 20 * 60 * 30); // 30 minutes in ticks

        questTimers.put(playerId, timeoutTask);

        // Record quest start time for completion time tracking
        questStartTimes.put(playerId, System.currentTimeMillis());

        // Skip showing quest start message for q7_m2 as we've already shown objectives
        if (!(questId.startsWith("q7_") && player.getWorld().getName().contains("m2"))) {
            // Show quest start message for other quests
            showQuestStartMessage(player, questData);
        } else if (debuggingFlag == 1) {
            player.sendMessage(ChatColor.GRAY + "DEBUG: Skipping showQuestStartMessage for q7_m2 as objectives already shown");
        }

        if (debuggingFlag == 1) {
            player.sendMessage(ChatColor.GRAY + "DEBUG: Quest " + questId + " started. First objective: Find the quest location.");
        }

        return true;
    }

    /**
     * Cancel an active quest
     */
    public void cancelQuest(UUID playerId) {
        QuestState state = activeQuests.remove(playerId);
        if (state != null) {
            // Cancel timeout task
            BukkitTask task = questTimers.remove(playerId);
            if (task != null) {
                task.cancel();
            }

            // Remove quest start time
            questStartTimes.remove(playerId);

            // Release quest occupation
            plugin.releaseQuestForPlayer(playerId);

            // Clear selected map
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                plugin.clearSelectedMap(player);
            }

            if (debuggingFlag == 1 && player != null) {
                player.sendMessage(ChatColor.GRAY + "DEBUG: Quest " + state.getQuestId() + " cancelled.");
            }
        }
    }

    /**
     * Complete quest with improved rewards display and delayed teleport
     */
    public void completeQuest(Player player) {
        UUID playerId = player.getUniqueId();
        QuestState state = activeQuests.get(playerId);

        if (state == null) return;

        QuestData.DungeonQuest questData = QuestData.getQuestData(state.getQuestId());
        if (questData == null) return;

        // Give rewards
        double expReward = questData.getExpReward();
        String itemReward = questData.getItemReward();

        // Give experience reward
        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
                "exp_give_p " + expReward + " " + player.getName());

        // Give item reward
        if (itemReward != null && !itemReward.isEmpty()) {
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
                    "el give " + player.getName() + " " + itemReward);
        }

        // Display completion messages
        player.sendTitle(
                ChatColor.GOLD + "Quest Complete!",
                ChatColor.GREEN + questData.getName() + " has been conquered",
                10, 70, 20
        );

        // Format item reward for display
        String formattedReward = questData.getFormattedRewardName();

        // Send fancy chat message
        player.sendMessage(ChatColor.GOLD + "§l✦ ═══════════════════════════ ✦");
        player.sendMessage(ChatColor.GREEN + "§l» §r§aQuest §l" + questData.getName() + "§r§a completed!");
        player.sendMessage(ChatColor.YELLOW + "§l» §r§eRewards:");
        player.sendMessage(ChatColor.AQUA + "  • §r§b" + expReward + "% Experience");
        player.sendMessage(ChatColor.LIGHT_PURPLE + "  • §r§d" + formattedReward);
        player.sendMessage(ChatColor.RED + "§l» §r§cYou will be returned to the main area in 15 seconds.");
        player.sendMessage(ChatColor.GOLD + "§l✦ ═══════════════════════════ ✦");

        if (debuggingFlag == 1) {
            player.sendMessage(ChatColor.GRAY + "DEBUG: Quest " + state.getQuestId() + " completed.");
        }

        // Track quest completion in statistics
        if (playerStatsDAO != null) {
            String questId = state.getQuestId();
            String dungeonKey = questId; // Most quest IDs are already in the correct format (e.g., "q1_inf")

            // Increment completion count
            playerStatsDAO.incrementCompletions(playerId, dungeonKey);

            // Calculate completion time and update fastest time if applicable
            Long startTime = questStartTimes.remove(playerId);
            if (startTime != null) {
                long completionTimeMillis = System.currentTimeMillis() - startTime;
                int completionTimeSeconds = (int) (completionTimeMillis / 1000);

                // Update fastest time
                playerStatsDAO.updateFastestTime(playerId, dungeonKey, completionTimeSeconds);

                if (debuggingFlag == 1) {
                    player.sendMessage(ChatColor.GRAY + "DEBUG: Quest completed in " + completionTimeSeconds + " seconds.");
                }
            }
        }

        // Schedule player return after 15 seconds with countdown
        final int[] countdown = {15};
        final AtomicInteger taskId = new AtomicInteger();

        int id = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (player.isOnline()) {
                countdown[0]--;

                if (countdown[0] <= 0) {
                    // Teleport player to spawn instead of killing them
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spawn " + player.getName());
                    plugin.getServer().getScheduler().cancelTask(taskId.get());
                } else if (countdown[0] <= 5) {
                    player.sendTitle(
                            ChatColor.RED + "Returning in " + countdown[0],
                            ChatColor.YELLOW + "Prepare for teleportation",
                            0, 21, 0
                    );
                }
            } else {
                plugin.getServer().getScheduler().cancelTask(taskId.get());
            }
        }, 20L, 20L);

        taskId.set(id);

        // Cancel quest
        cancelQuest(playerId);
    }

    /**
     * Handle player finding a location objective
     */
    public void handleLocationFound(Player player) {
        UUID playerId = player.getUniqueId();
        QuestState state = activeQuests.get(playerId);

        if (state == null) return;

        // Only process if current objective is FIND_LOCATION
        if (state.getCurrentObjective() != QuestState.QuestObjective.FIND_LOCATION) return;

        // Dodaj specjalną obsługę dla ołtarza 2 w q7 stage 2
        if (state.getQuestId().startsWith("q7_") && state.getCurrentStage() == 2 && 
                state.isAltar1Activated() && player.hasMetadata("altar2_activated")) {
            // Jeśli to ołtarz 2, nie pokazuj "Location Found!"
            return;
        }

        state.setLocationFound(true);

        // Send messages
        player.sendTitle(
                ChatColor.GREEN + "Location Found!",
                ChatColor.AQUA + "You can now continue with your quest",
                10, 70, 20
        );

        player.sendMessage(ChatColor.GOLD + "§l✦ ════════════════════════ ✦");
        player.sendMessage(ChatColor.GREEN + "§l» §r§aYou've found the quest location!");
        player.sendMessage(ChatColor.GOLD + "§l✦ ════════════════════════ ✦");

        if (debuggingFlag == 1) {
            player.sendMessage(ChatColor.GRAY + "DEBUG: Location found for stage " + state.getCurrentStage());
        }

        // Advance to next objective (kill mobs)
        state.advanceToNextObjective();

        // Get quest data
        QuestData.DungeonQuest questData = QuestData.getQuestData(state.getQuestId());

        // Show specific mob kill objectives
        if (questData != null) {
            Map<String, QuestData.KillObjective> killObjectives = questData.getKillObjectiveDetails(state.getCurrentStage());

            if (!killObjectives.isEmpty()) {
                player.sendMessage(ChatColor.YELLOW + "§l» §r§eKill objectives:");

                for (QuestData.KillObjective objective : killObjectives.values()) {
                    player.sendMessage(ChatColor.YELLOW + "  • §r§e" + objective.getObjectiveText());
                }
            } else {
                // If there are no kill objectives, skip to boss
                state.advanceToNextObjective();

                // Show boss objective
                QuestData.BossObjective bossObj = questData.getBossObjectiveDetails(state.getCurrentStage());
                if (bossObj != null) {
                    player.sendMessage(ChatColor.LIGHT_PURPLE + "§l» §r§d" + bossObj.getObjectiveText());
                }
            }
        }
    }

    /**
     * Handle mob kill with improved messages
     */
    public void handleMobKill(Player player, String mobId) {
        UUID playerId = player.getUniqueId();
        QuestState state = activeQuests.get(playerId);

        if (state == null) return;

        QuestData.DungeonQuest questData = QuestData.getQuestData(state.getQuestId());
        if (questData == null) return;

        if (debuggingFlag == 1) {
            player.sendMessage(ChatColor.GRAY + "DEBUG: Mob killed: " + mobId);
        }

        // Handle regular mobs
        Map<String, Integer> killObjectives = questData.getKillObjectives(state.getCurrentStage());

        if (killObjectives.containsKey(mobId)) {
            // Check if we're in the kill mobs phase
            if (state.getCurrentObjective() != QuestState.QuestObjective.KILL_MOBS) {
                return; // Not in kill phase yet
            }

            int required = killObjectives.get(mobId);
            int currentKills = state.getKillCount(mobId);

            // Skip if already at or above required count
            if (currentKills >= required) {
                return;
            }

            int killed = state.incrementKillCount(mobId);

            // Show progress
            QuestData.KillObjective killObj = questData.getKillObjectiveDetails(state.getCurrentStage()).get(mobId);
            String mobName = killObj != null ? killObj.getDisplayName() : formatMobName(mobId);
            String progressBar = createProgressBar(killed, required);

            player.sendMessage(ChatColor.YELLOW + "§l» §r§eDefeated " + ChatColor.GOLD + mobName +
                    ChatColor.YELLOW + ": " + ChatColor.WHITE + killed + "/" + required + " " + progressBar);

            // Show milestone messages
            if (killed == required) {
                player.sendTitle("", ChatColor.GREEN + "Objective Complete: " + mobName, 5, 30, 10);
            } else if (killed == Math.ceil(required / 2.0)) {
                // Halfway point
                player.sendTitle("", ChatColor.YELLOW + "Halfway there! Keep going!", 5, 20, 5);
            }

            // Check if all kills are complete
            if (state.areKillsComplete(killObjectives)) {
                player.sendMessage(ChatColor.GREEN + "§l» §r§aAll enemies defeated!");

                // Move to next objective (kill boss)
                state.advanceToNextObjective();

                // Show boss objective
                QuestData.BossObjective bossObj = questData.getBossObjectiveDetails(state.getCurrentStage());
                if (bossObj != null) {
                    player.sendMessage(ChatColor.LIGHT_PURPLE + "§l» §r§d" + bossObj.getObjectiveText());
                }

                if (debuggingFlag == 1) {
                    player.sendMessage(ChatColor.GRAY + "DEBUG: All kill objectives completed, moving to boss phase");
                }
            }

            return;
        }

        // Handle boss
        String bossId = questData.getBossObjective(state.getCurrentStage());

        if (mobId.equals(bossId)) {
            // Check if we're in the kill boss phase
            if (state.getCurrentObjective() != QuestState.QuestObjective.KILL_BOSS &&
                    !(state.getQuestId().startsWith("q6_") && state.getCurrentStage() == 2 &&
                            mobId.contains("murot_high_priest"))) {

                if (debuggingFlag == 1) {
                    player.sendMessage(ChatColor.GRAY + "DEBUG: Boss killed but not in boss phase. Current objective: " + state.getCurrentObjective());
                }
                return; // Not in boss phase yet
            }

            // Special case for Q6 stage 2 - force objective update if needed
            if (state.getQuestId().startsWith("q6_") && state.getCurrentStage() == 2 &&
                    state.getCurrentObjective() == QuestState.QuestObjective.COLLECT_FROM_MOBS) {

                if (debuggingFlag == 1) {
                    player.sendMessage(ChatColor.GRAY + "DEBUG: Q6 stage 2 boss killed while in collect phase, forcing objective update");
                }
                state.setCurrentObjective(QuestState.QuestObjective.KILL_BOSS);
            }


            // Skip if already killed
            if (state.isBossKilled()) {
                return;
            }

            state.setBossKilled(true);
            QuestData.BossObjective bossObj = questData.getBossObjectiveDetails(state.getCurrentStage());
            String bossName = bossObj != null ? bossObj.getDisplayName() : formatMobName(mobId);

            // Display boss killed message
            player.sendMessage(ChatColor.GOLD + "§l✦ ════════════════════════ ✦");

            boolean isLastStage = !questData.hasStage(state.getCurrentStage() + 1);
            if (!isLastStage) {
                player.sendMessage(ChatColor.LIGHT_PURPLE + "§l» §r§d" + bossName + " has been defeated!");
                player.sendMessage(ChatColor.YELLOW + "§l» §r§eFind the portal to continue your quest!");

                player.sendTitle(
                        ChatColor.LIGHT_PURPLE + "Mini-Boss Defeated!",
                        ChatColor.GOLD + bossName + ChatColor.YELLOW + " has fallen",
                        10, 70, 20
                );

                // Move to next objective (find portal)
                state.advanceToNextObjective();
            } else {
                player.sendMessage(ChatColor.LIGHT_PURPLE + "§l» §r§d" + bossName + " has been defeated!");
                player.sendMessage(ChatColor.YELLOW + "§l» §r§eYou've completed all objectives!");

                player.sendTitle(
                        ChatColor.LIGHT_PURPLE + "Final Boss Defeated!",
                        ChatColor.GOLD + bossName + ChatColor.YELLOW + " has fallen",
                        10, 70, 20
                );

                // Add a delay before completing the quest to give players time to read
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (player.isOnline()) {
                        completeQuest(player);
                    }
                }, 80L); // 4 seconds delay
                return;
            }

            player.sendMessage(ChatColor.GOLD + "§l✦ ════════════════════════ ✦");

            if (debuggingFlag == 1) {
                player.sendMessage(ChatColor.GRAY + "DEBUG: Boss " + bossId + " killed. Moving to portal phase.");
            }
        }
    }

    /**
     * Handle portal found
     */
    public void handlePortalFound(Player player) {
        UUID playerId = player.getUniqueId();
        QuestState state = activeQuests.get(playerId);

        if (state == null) return;

        // Only process if current objective is FIND_PORTAL
        if (state.getCurrentObjective() != QuestState.QuestObjective.FIND_PORTAL) return;

        state.setPortalFound(true);
        state.setWaitingForTeleport(true);

        // Add this special check for Q7 stage 1 transition
        if (state.getQuestId().startsWith("q7_") && state.getCurrentStage() == 1) {
            state.setInQ7SpecialTransition(true);
            if (debuggingFlag == 1) {
                player.sendMessage(ChatColor.GRAY + "DEBUG: Setting Q7 special transition flag");
            }
        }

        // Display portal found message
        player.sendTitle(
                ChatColor.GREEN + "Portal Found!",
                ChatColor.AQUA + "Stand here to proceed to the next area",
                10, 70, 20
        );

        player.sendMessage(ChatColor.GOLD + "§l✦ ════════════════════════ ✦");
        player.sendMessage(ChatColor.GREEN + "§l» §r§aYou've found the exit portal!");
        player.sendMessage(ChatColor.YELLOW + "§l» §r§eStay here for a moment to teleport...");
        player.sendMessage(ChatColor.GOLD + "§l✦ ════════════════════════ ✦");

        if (debuggingFlag == 1) {
            player.sendMessage(ChatColor.GRAY + "DEBUG: Portal found for stage " + state.getCurrentStage());
        }

        // Schedule teleport after 1 second
        QuestData.DungeonQuest questData = QuestData.getQuestData(state.getQuestId());
        if (questData == null) return;

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!player.isOnline()) return;

            // Check if player still has quest and is waiting for teleport
            QuestState currentState = activeQuests.get(playerId);
            if (currentState == null || !currentState.isWaitingForTeleport()) return;

            // Check if we're still in the portal area
            QuestData.LocationInfo portalLocation = questData.getPortalObjective(currentState.getCurrentStage());
            if (portalLocation == null || !portalLocation.isInside(player.getLocation())) {
                player.sendMessage(ChatColor.RED + "§l» §r§cYou moved away from the portal! Return to teleport.");
                currentState.setPortalFound(false);
                currentState.setWaitingForTeleport(false);
                return;
            }

            // Perform stage transition
            handleStageComplete(player);
        }, 20L); // 1 second (20 ticks)
    }

    /**
     * Handle stage completion with improved messages
     */
    public void handleStageComplete(Player player) {
        UUID playerId = player.getUniqueId();
        QuestState state = activeQuests.get(playerId);

        if (state == null) return;

        QuestData.DungeonQuest questData = QuestData.getQuestData(state.getQuestId());
        if (questData == null) return;

        int currentStage = state.getCurrentStage();
        boolean isLastStage = !questData.hasStage(currentStage + 1);

        if (debuggingFlag == 1) {
            player.sendMessage(ChatColor.GRAY + "DEBUG: Stage " + currentStage + " is complete");
        }

        // Title message removed as requested

        // Execute teleport command if available
        String teleportCommand = questData.getStageEndCommand(currentStage);
        if (teleportCommand != null && !teleportCommand.isEmpty()) {
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
                    teleportCommand + " " + player.getName());

            // Message removed as requested

            if (debuggingFlag == 1) {
                player.sendMessage(ChatColor.GRAY + "DEBUG: Warping to " + teleportCommand);
            }

            // Special handling for Q7 transition
            if (state.isInQ7SpecialTransition()) {
                if (debuggingFlag == 1) {
                    player.sendMessage(ChatColor.GRAY + "DEBUG: Removing all summons after teleport");
                }

                // Schedule delayed messages for Q7 Stage 2
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (!player.isOnline()) return;

                    // Message removed as requested

                    player.sendMessage(ChatColor.GOLD + "§l✦ ════════════════════════ ✦");
                    player.sendMessage(ChatColor.GREEN + "§l» §r§aQ7 Quest Stage 2 Started!");
                    player.sendMessage(ChatColor.AQUA + "§l» §r§bObjectives:");
                    player.sendMessage(ChatColor.YELLOW + "  • §r§eFind the first altar and kill surrounding mobs");
                    player.sendMessage(ChatColor.YELLOW + "  • §r§eThen find the second altar and kill surrounding mobs");
                    player.sendMessage(ChatColor.YELLOW + "  • §r§eDefeat the fortress commander that appears");
                    player.sendMessage(ChatColor.GOLD + "§l✦ ════════════════════════ ✦");
                }, 40L); // 2 seconds delay
            }
        }

        // The reset timers code has been moved to QuestListeners.java
        // to ensure mini-boss only spawns after altar 2 mobs are killed

        if (isLastStage) {
            // If this was the final stage, complete the quest
            completeQuest(player);
            return;
        }

        // Advance to next stage
        state.advanceToNextStage();
        int newStage = state.getCurrentStage();

        // Special handling for Q3 quest transitions
        boolean isQ3Quest = state.getQuestId().startsWith("q3_");

        // For Q3, Stage 2 should start with COLLECT_FROM_MOBS objective
        if (isQ3Quest && newStage == 2) {
            // Pokazujemy tylko informację o zbieraniu fragmentu runy
            player.sendMessage(ChatColor.AQUA + "§l» §r§b" + questData.getStageMessage(2));

            Map<String, QuestData.CollectObjective> collectObjectives = questData.getCollectObjectives(newStage);
            if (!collectObjectives.isEmpty()) {
                QuestData.CollectObjective objective = collectObjectives.values().iterator().next();
                player.sendMessage(ChatColor.AQUA + "§l» §r§b" + objective.getObjectiveText());
            }
            // This will set the correct objective in the advanceToNextStage method
        }
        // For Q3, Stage 3 should start with KILL_BOSS objective
        else if (isQ3Quest && newStage == 3) {
            state.setLocationFound(true);
            // Ensure we're at KILL_BOSS
            while (state.getCurrentObjective() != QuestState.QuestObjective.KILL_BOSS) {
                state.advanceToNextObjective();
            }
        }// For Q6, Stage 2 should start with COLLECT_FROM_MOBS objective
        else if (state.getQuestId().startsWith("q6_") && newStage == 2) {
            // Ensure we're at the correct objective
            state.setCurrentObjective(QuestState.QuestObjective.COLLECT_FROM_MOBS);

            player.sendMessage(ChatColor.AQUA + "§l» §r§b" + questData.getStageMessage(2));

            Map<String, QuestData.CollectObjective> collectObjectives = questData.getCollectObjectives(newStage);
            if (!collectObjectives.isEmpty()) {
                player.sendMessage(ChatColor.AQUA + "§l» §r§bYou need to collect:");

                // Show collection objectives
                for (QuestData.CollectObjective objective : collectObjectives.values()) {
                    player.sendMessage(ChatColor.AQUA + "  • §r§b" + objective.getObjectiveText());
                }
            }
        }
        // For Q9, Stage 2 should start with INTERACT_WITH_BLOCKS objective for metronomes
        else if (state.getQuestId().startsWith("q9_") && newStage == 2) {
            // Ensure we're at the correct objective
            state.setCurrentObjective(QuestState.QuestObjective.INTERACT_WITH_BLOCKS);

            player.sendMessage(ChatColor.GOLD + "§l✦ ════════════════════════ ✦");
            player.sendMessage(ChatColor.GREEN + "§l» §r§aStage 1 complete!");
            player.sendMessage(ChatColor.YELLOW + "§l» §r§eStarting Stage 2");
            player.sendMessage(ChatColor.AQUA + "§l» §r§bObjectives:");
            player.sendMessage(ChatColor.AQUA + "§l» §r§bFind and activate 5 ancient metronomes");
            player.sendMessage(ChatColor.AQUA + "§l» §r§bThen defeat Ebicarus to proceed to the final stage");
            player.sendMessage(ChatColor.GOLD + "§l✦ ════════════════════════ ✦");

            // Skip generic messages
            return;
        }
        // For Q7, Stage 2 should start with FIND_LOCATION objective for altars
        // Normal handling for non-Q3 quests or other stages
        else if (newStage > 1 && state.getCurrentObjective() == QuestState.QuestObjective.FIND_LOCATION) {
            // For non-first stages, we skip directly to the KILL_MOBS or KILL_BOSS phase
            state.setLocationFound(true);

            Map<String, Integer> killObjectives = questData.getKillObjectives(newStage);
            if (killObjectives != null && !killObjectives.isEmpty()) {
                // If there are kill objectives, go to kill mobs phase
                state.advanceToNextObjective(); // Move to KILL_MOBS
            } else {
                // If there are no kill objectives, go directly to boss phase
                state.advanceToNextObjective(); // Move to KILL_MOBS
                state.advanceToNextObjective(); // Then to KILL_BOSS
            }
        }

        // Skip all remaining messages if we're in Q7 special transition
        if (state.isInQ7SpecialTransition()) {
            // Reset the flag so future transitions are handled normally
            state.setInQ7SpecialTransition(false);

            if (debuggingFlag == 1) {
                player.sendMessage(ChatColor.GRAY + "DEBUG: Skipping generic stage messages for Q7 transition");
            }

            return; // Exit the method early to skip all regular stage info messages
        }

        // Display new stage info - only reached if NOT in Q7 special transition
        // Add a flag to suppress generic messages for Q7 stage 2
        boolean skipGenericMessages = state.getQuestId().startsWith("q7_") && currentStage == 1;

        if (!skipGenericMessages) {
            player.sendMessage(ChatColor.GOLD + "§l✦ ════════════════════════ ✦");
            player.sendMessage(ChatColor.GREEN + "§l» §r§aStage " + currentStage + " complete!");
            player.sendMessage(ChatColor.YELLOW + "§l» §r§eStarting Stage " + newStage);

            player.sendTitle(
                    ChatColor.GOLD + "Stage " + newStage + " Started",
                    ChatColor.YELLOW + "Complete the objectives to proceed",
                    10, 70, 20
            );

            // Show objectives for new stage
            player.sendMessage(ChatColor.AQUA + "§l» §r§bObjectives:");
        }

        QuestState.QuestObjective currentObjective = state.getCurrentObjective();

        // Wrap the objectives display part in the same conditional check
        if (!skipGenericMessages) {
            // Display correct objectives based on the current objective type
            if (currentObjective == QuestState.QuestObjective.KILL_BOSS) {
                // Boss objective
                QuestData.BossObjective bossObj = questData.getBossObjectiveDetails(newStage);
                if (bossObj != null) {
                    player.sendMessage(ChatColor.AQUA + "§l» §r§b" + bossObj.getObjectiveText());
                }
            } else if (currentObjective == QuestState.QuestObjective.KILL_MOBS) {
                // Kill mobs objectives
                Map<String, QuestData.KillObjective> killObjectives = questData.getKillObjectiveDetails(newStage);

                if (!killObjectives.isEmpty()) {
                    for (QuestData.KillObjective objective : killObjectives.values()) {
                        player.sendMessage(ChatColor.AQUA + "§l» §r§b" + objective.getObjectiveText());
                    }
                }
            } else if (currentObjective == QuestState.QuestObjective.COLLECT_FROM_MOBS) {
                // Collection objectives
                Map<String, QuestData.CollectObjective> collectObjectives = questData.getCollectObjectives(newStage);

                if (!collectObjectives.isEmpty()) {
                    // Display collection message from stage data
                    String stageMsg = questData.getStageMessage(newStage);
                    if (stageMsg != null && !stageMsg.isEmpty()) {
                        player.sendMessage(ChatColor.AQUA + "§l» §r§b" + stageMsg);
                    }

                    // Special handling for Q6 Stage 2 to show all dagger parts
                    if (state.getQuestId().startsWith("q6_") && newStage == 2) {
                        player.sendMessage(ChatColor.AQUA + "§l» §r§bCollect all three parts of the Sacrificial Dagger:");
                        for (Map.Entry<String, QuestData.CollectObjective> entry : collectObjectives.entrySet()) {
                            QuestData.CollectObjective objective = entry.getValue();
                            player.sendMessage(ChatColor.AQUA + "  • §r§b" + objective.getDisplayName() +
                                    " (Drop chance: " + objective.getDropChance() + "%)");
                        }

                        if (debuggingFlag == 1) {
                            player.sendMessage(ChatColor.GRAY + "DEBUG: Showing all dagger parts in objectives list");
                        }
                    } else {
                        // Original behavior for other quests
                        QuestData.CollectObjective objective = collectObjectives.values().iterator().next();
                        player.sendMessage(ChatColor.AQUA + "§l» §r§b" + objective.getObjectiveText());
                    }
                }
            } else if (currentObjective == QuestState.QuestObjective.INTERACT_WITH_BLOCKS) {
                // Interaction objectives
                QuestData.InteractObjective interactObj = questData.getInteractObjective(newStage);
                if (interactObj != null) {
                    player.sendMessage(ChatColor.AQUA + "§l» §r§b" + interactObj.getObjectiveText());
                }
            }

            player.sendMessage(ChatColor.GOLD + "§l✦ ════════════════════════ ✦");
        }

        if (debuggingFlag == 1) {
            player.sendMessage(ChatColor.GRAY + "DEBUG: Advanced to stage " + newStage +
                    " with objective: " + currentObjective);
        }
    }
    /**
     * Show quest start message with improved formatting
     */
    private void showQuestStartMessage(Player player, QuestData.DungeonQuest questData) {
        player.sendTitle(
                ChatColor.GOLD + questData.getName() + " Started",
                ChatColor.YELLOW + "You have 30 minutes to clear it. Good luck!",
                10, 70, 20
        );

        player.sendMessage(ChatColor.GOLD + "§l✦ ═══════════════════════════ ✦");
        player.sendMessage(ChatColor.GREEN + "§l» §r§aQuest Started: §l" + questData.getName());
        player.sendMessage(ChatColor.RED + "§l» §r§cTime Limit: §f30 minutes");
        player.sendMessage(ChatColor.YELLOW + "§l» §r§eObjectives:");

        // Display first objective
        QuestState state = activeQuests.get(player.getUniqueId());
        if (state != null) {
            if (state.getQuestId().startsWith("q1_")) {
                // For Q1 quests, show the location objective
                player.sendMessage(ChatColor.AQUA + "§l» §r§b" + questData.getLocationMessage());
            } else if (state.getQuestId().startsWith("q2_")) {
                player.sendMessage(ChatColor.AQUA + "§l» §r§bYou need to collect:");
                player.sendMessage(ChatColor.AQUA + "  • §r§c" + state.getRequiredRedMushrooms() + " Red Mushrooms");
                player.sendMessage(ChatColor.AQUA + "  • §r§6" + state.getRequiredBrownMushrooms() + " Brown Mushrooms");
                player.sendMessage(ChatColor.AQUA + "§l» §r§bAnd brew an antidote using a cauldron");
                player.sendMessage(ChatColor.RED + "§l» §r§cBeware of poison areas!");
            } else if (state.getQuestId().startsWith("q3_")) {
                if (questData != null) {
                    player.sendMessage(ChatColor.AQUA + "§l» §r§b" + questData.getStageMessage(1));

                    // Pokaż tylko jeden przykład celu kolekcji, żeby uniknąć duplikacji
                    Map<String, QuestData.CollectObjective> objectives = questData.getCollectObjectives(1);
                    if (!objectives.isEmpty()) {
                        // Weź tylko pierwszy cel kolekcji
                        QuestData.CollectObjective objective = objectives.values().iterator().next();
                        player.sendMessage(ChatColor.AQUA + "  • §r§b" + objective.getObjectiveText());
                    }
                }
            } else if (state.getQuestId().startsWith("q4_") || state.getQuestId().startsWith("q5_")) {
                // Display only kill objectives for Q4 and Q5 (no boss objective yet)
                if (questData != null) {
                    Map<String, QuestData.KillObjective> killObjectives = questData.getKillObjectiveDetails(1);

                    if (!killObjectives.isEmpty()) {
                        player.sendMessage(ChatColor.AQUA + "§l» §r§bYou need to defeat:");

                        for (QuestData.KillObjective objective : killObjectives.values()) {
                            player.sendMessage(ChatColor.AQUA + "  • §r§b" + objective.getObjectiveText());
                        }
                    }
                }
            }else if (state.getQuestId().startsWith("q6_")) {
                // Display kill objectives for Q6
                if (questData != null) {
                    Map<String, QuestData.KillObjective> killObjectives = questData.getKillObjectiveDetails(1);

                    if (!killObjectives.isEmpty()) {
                        player.sendMessage(ChatColor.AQUA + "§l» §r§bYou need to defeat:");

                        for (QuestData.KillObjective objective : killObjectives.values()) {
                            player.sendMessage(ChatColor.AQUA + "  • §r§b" + objective.getObjectiveText());
                        }
                    }
                }
            } else if (state.getQuestId().startsWith("q7_")) {
                // Check if we're on stage 1 or stage 2
                if (state.getCurrentStage() == 1) {
                    // Display collection objective for Q7 stage 1
                    player.sendMessage(ChatColor.AQUA + "§l» §r§bCollect 2 Catapult Balls from Combat Mechanoids");
                    player.sendMessage(ChatColor.AQUA + "§l» §r§bThen find and fire two different catapults");
                } else if (state.getCurrentStage() == 2) {
                    // Check if we're starting directly on m2 (after warp)
                    // If so, skip showing objectives here as they're already shown in startQuest
                    if (!player.getWorld().getName().contains("m2")) {
                        // Display altar objectives for Q7 stage 2
                        player.sendMessage(ChatColor.AQUA + "§l» §r§bFind the first altar and kill surrounding mobs");
                        player.sendMessage(ChatColor.AQUA + "§l» §r§bThen find the second altar and kill surrounding mobs");
                        player.sendMessage(ChatColor.AQUA + "§l» §r§bDefeat the fortress commander that appears");

                        // For q7 stage 2, emphasize the first objective
                        player.sendTitle(
                            ChatColor.YELLOW + "First Objective:",
                            ChatColor.AQUA + "Find the first altar and activate it",
                            10, 70, 20
                        );
                    }
                }
            } else if (state.getQuestId().startsWith("q8_")) {
                // Display collection objective for Q8
                player.sendMessage(ChatColor.AQUA + "§l» §r§bCollect 5 Electrical Shards from electrified creatures");
                player.sendMessage(ChatColor.AQUA + "§l» §r§bThen channel the energy into chiseled deepslate");
            } else if (state.getQuestId().startsWith("q9_")) {
                // Display statue collection objective for Q9
                player.sendMessage(ChatColor.AQUA + "§l» §r§bFind and interact with 4 ancient statues to collect their fragments");
                player.sendMessage(ChatColor.AQUA + "§l» §r§bThen defeat Asterion to proceed to the next stage");
            } else if (state.getQuestId().startsWith("q10_")) {
                // Display fragment collection objective for Q10
                player.sendMessage(ChatColor.AQUA + "§l» §r§bCollect ancient fragments from lime shulker boxes");
                player.sendMessage(ChatColor.AQUA + "§l» §r§bDeposit each fragment into a lodestone before collecting the next");
                player.sendMessage(ChatColor.AQUA + "§l» §r§bYou need to collect and deposit 3 fragments total");
                player.sendMessage(ChatColor.AQUA + "§l» §r§bThen defeat Melas the Swift-Footed");
            }
        }

        player.sendMessage(ChatColor.YELLOW + "§l» §r§eRewards:");
        player.sendMessage(ChatColor.AQUA + "  • §r§b" + questData.getExpReward() + "% Experience");
        player.sendMessage(ChatColor.LIGHT_PURPLE + "  • §r§d" + questData.getFormattedRewardName());
        player.sendMessage(ChatColor.GOLD + "§l✦ ═══════════════════════════ ✦");
    }    /**
     * Format mob ID into readable name
     */
    private String formatMobName(String mobId) {
        String[] parts = mobId.split("_");
        StringBuilder name = new StringBuilder();

        for (String part : parts) {
            if (part.equals("inf") || part.equals("hell") || part.equals("blood")) {
                continue; // Skip difficulty indicators
            }

            if (name.length() > 0) {
                name.append(" ");
            }

            // Capitalize first letter
            name.append(Character.toUpperCase(part.charAt(0)));
            name.append(part.substring(1));
        }

        return name.toString();
    }

    /**
     * Create a visual progress bar
     */
    private String createProgressBar(int current, int max) {
        StringBuilder bar = new StringBuilder();
        int totalBars = 10;
        int filledBars = Math.min(totalBars, (int)Math.ceil((double)current / max * totalBars));

        bar.append(ChatColor.GREEN);
        for (int i = 0; i < filledBars; i++) {
            bar.append("■");
        }

        bar.append(ChatColor.GRAY);
        for (int i = filledBars; i < totalBars; i++) {
            bar.append("■");
        }

        return bar.toString();
    }

    /**
     * Get readable name for objective type
     */
    private String getObjectiveName(QuestState.QuestObjective objective) {
        switch (objective) {
            case FIND_LOCATION:
                return "Find the quest location";
            case KILL_MOBS:
                return "Defeat all enemies";
            case KILL_BOSS:
                return "Defeat the boss";
            case FIND_PORTAL:
                return "Find the exit portal";
            default:
                return "Unknown objective";
        }
    }

    /**
     * Get active quest for player
     */
    public QuestState getActiveQuest(UUID playerId) {
        return activeQuests.get(playerId);
    }

    /**
     * Check if a player has an active quest
     */
    public boolean hasActiveQuest(UUID playerId) {
        return activeQuests.containsKey(playerId);
    }
}
