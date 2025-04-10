package com.maks.mydungeonteleportplugin.quests;

import com.maks.mydungeonteleportplugin.MyDungeonTeleportPlugin;
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

    // Debug flag
    private int debuggingFlag = 1; // Set to 0 when everything is working

    public QuestManager(MyDungeonTeleportPlugin plugin) {
        this.plugin = plugin;
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
        }
        // Mark quest as occupied
        plugin.occupyQuest(questId, playerId);

        // Schedule timeout task (30 minutes)
        BukkitTask timeoutTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                player.sendMessage(ChatColor.RED + "Time's up! You've failed the quest.");
                player.setHealth(0);
                cancelQuest(playerId);
            }
        }, 20 * 60 * 30); // 30 minutes in ticks

        questTimers.put(playerId, timeoutTask);

        // Show quest start message
        showQuestStartMessage(player, questData);

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

        // Schedule player return after 15 seconds with countdown
        final int[] countdown = {15};
        final AtomicInteger taskId = new AtomicInteger();

        int id = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (player.isOnline()) {
                countdown[0]--;

                if (countdown[0] <= 0) {
                    // Use player.performCommand instead of console command to execute as player
                    player.performCommand("suicide");
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
            if (state.getCurrentObjective() != QuestState.QuestObjective.KILL_BOSS) {
                return; // Not in boss phase yet
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

        // Display completion message
        player.sendTitle(
                ChatColor.GOLD + "Stage " + currentStage + " Complete!",
                ChatColor.GREEN + "Moving to the next area",
                10, 70, 20
        );

        // Execute teleport command if available
        String teleportCommand = questData.getStageEndCommand(currentStage);
        if (teleportCommand != null && !teleportCommand.isEmpty()) {
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
                    teleportCommand + " " + player.getName());

            player.sendMessage(ChatColor.GREEN + "§l» §r§aTeleporting to the next area...");

            if (debuggingFlag == 1) {
                player.sendMessage(ChatColor.GRAY + "DEBUG: Warping to " + teleportCommand);
            }
        }

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
        }
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

        // Display new stage info
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

        QuestState.QuestObjective currentObjective = state.getCurrentObjective();

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

                // Get just one example - to avoid duplicates
                QuestData.CollectObjective objective = collectObjectives.values().iterator().next();
                player.sendMessage(ChatColor.AQUA + "§l» §r§b" + objective.getObjectiveText());
            }
        } else if (currentObjective == QuestState.QuestObjective.INTERACT_WITH_BLOCKS) {
            // Interaction objectives
            QuestData.InteractObjective interactObj = questData.getInteractObjective(newStage);
            if (interactObj != null) {
                player.sendMessage(ChatColor.AQUA + "§l» §r§b" + interactObj.getObjectiveText());
            }
        }

        player.sendMessage(ChatColor.GOLD + "§l✦ ════════════════════════ ✦");

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
            if (state.getQuestId().startsWith("q2_")) {
                player.sendMessage(ChatColor.AQUA + "§l» §r§bYou need to collect:");
                player.sendMessage(ChatColor.AQUA + "  • §r§c" + state.getRequiredRedMushrooms() + " Red Mushrooms");
                player.sendMessage(ChatColor.AQUA + "  • §r§6" + state.getRequiredBrownMushrooms() + " Brown Mushrooms");
                player.sendMessage(ChatColor.AQUA + "§l» §r§bAnd brew an antidote using a cauldron");
                player.sendMessage(ChatColor.RED + "§l» §r§cBeware of poison areas!");
            }else if (state.getQuestId().startsWith("q3_")) {
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
            }
        }

        player.sendMessage(ChatColor.YELLOW + "§l» §r§eRewards:");
        player.sendMessage(ChatColor.AQUA + "  • §r§b" + questData.getExpReward() + "% Experience");
        player.sendMessage(ChatColor.LIGHT_PURPLE + "  • §r§d" + questData.getFormattedRewardName());
        player.sendMessage(ChatColor.GOLD + "§l✦ ═══════════════════════════ ✦");
    }
    /**
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