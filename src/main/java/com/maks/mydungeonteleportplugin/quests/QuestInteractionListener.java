package com.maks.mydungeonteleportplugin.quests;

import com.maks.mydungeonteleportplugin.MyDungeonTeleportPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class QuestInteractionListener implements Listener {
    private final MyDungeonTeleportPlugin plugin;
    private final QuestManager questManager;
    private final Map<UUID, Long> lastPoisonWarningTime = new HashMap<>();
    private final Map<UUID, Integer> poisonTimers = new HashMap<>();
    private final Map<UUID, Map<String, Long>> lastMushroomInteractions = new HashMap<>();

    // Debug flag
    private int debuggingFlag = 0; // Debug disabled

    public QuestInteractionListener(MyDungeonTeleportPlugin plugin, QuestManager questManager) {
        this.plugin = plugin;
        this.questManager = questManager;

        // Czyszczenie danych co 30 minut (bardziej jako zabezpieczenie)
        plugin.getServer().getScheduler().runTaskTimer(plugin, this::cleanupInactivePlayerData, 36000L, 36000L);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();

        if (clickedBlock == null) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        QuestState state = questManager.getActiveQuest(player.getUniqueId());
        if (state == null) return;

        // Add these debug lines for Q7 quests
        if (state.getQuestId().startsWith("q7_")) {
            if (debuggingFlag == 1) {
                player.sendMessage(ChatColor.GRAY + "DEBUG: Q7 interaction with " + clickedBlock.getType() + 
                        " at " + clickedBlock.getX() + "," + clickedBlock.getY() + "," + clickedBlock.getZ());
                player.sendMessage(ChatColor.GRAY + "DEBUG: Current objective: " + state.getCurrentObjective());
            }
        }

        // Check if this is a q7 quest and handle lever interactions
        if (state.getQuestId().startsWith("q7_") && 
            state.getCurrentObjective() == QuestState.QuestObjective.INTERACT_WITH_BLOCKS) {

            Material blockType = clickedBlock.getType();

            if (debuggingFlag == 1) {
                player.sendMessage(ChatColor.GRAY + "DEBUG: Interaction with " + blockType + " at " +
                        clickedBlock.getX() + ", " + clickedBlock.getY() + ", " + clickedBlock.getZ());
            }

            if (blockType == Material.LEVER) {
                // Generate unique ID for this lever
                String leverId = "lever_" + clickedBlock.getX() + "_" + 
                                 clickedBlock.getY() + "_" + clickedBlock.getZ();

                // Check if this lever was already used
                if (state.hasUsedLever(leverId)) {
                    player.sendMessage(ChatColor.RED + "You've already fired this catapult! Find another one.");
                    return;
                }

                // Mark this lever as used
                state.useLever(leverId);
                int leverCount = state.getLeverCount();

                player.sendMessage(ChatColor.GREEN + "§l» §r§aYou've fired a catapult! (" + leverCount + "/2)");

                // Play sound effect
                player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);

                // If both catapults fired, advance to next objective
                if (leverCount >= 2) {
                    player.sendTitle(
                        ChatColor.GREEN + "Catapults Fired!",
                        ChatColor.YELLOW + "The fortress gate is weakened. Find and defeat the gate guard.",
                        10, 70, 20
                    );

                    player.sendMessage(ChatColor.GOLD + "§l✦ ════════════════════════ ✦");
                    player.sendMessage(ChatColor.GREEN + "§l» §r§aBoth catapults successfully fired!");
                    player.sendMessage(ChatColor.YELLOW + "§l» §r§eThe fortress gate is now weakened.");
                    player.sendMessage(ChatColor.YELLOW + "§l» §r§eFind and defeat the Iron Creeper Gate Guard.");
                    player.sendMessage(ChatColor.GOLD + "§l✦ ════════════════════════ ✦");

                    // Move to boss phase
                    state.advanceToNextObjective();

                    if (debuggingFlag == 1) {
                        player.sendMessage(ChatColor.GRAY + "DEBUG: Both catapults fired, advancing to KILL_BOSS objective");
                    }
                }

                return; // Handled lever interaction
            }
        }

        else if (state.getQuestId().startsWith("q8_") &&
                state.getCurrentObjective() == QuestState.QuestObjective.INTERACT_WITH_BLOCKS) {

            Material blockType = clickedBlock.getType();

            if (debuggingFlag == 1) {
                player.sendMessage(ChatColor.GRAY + "DEBUG: Interaction with " + blockType + " at " +
                        clickedBlock.getX() + ", " + clickedBlock.getY() + ", " + clickedBlock.getZ());
            }

            if (blockType == Material.CHISELED_DEEPSLATE) {
                // Player interacted with chiseled deepslate
                state.setInteractedWithGrindstone(true); // Reusing this flag for Q8

                player.sendTitle(
                        ChatColor.YELLOW + "Energy Channeled!",
                        ChatColor.GREEN + "The electrical power has been absorbed",
                        10, 70, 20
                );

                player.sendMessage(ChatColor.GOLD + "§l✦ ════════════════════════ ✦");
                player.sendMessage(ChatColor.GREEN + "§l» §r§aYou've successfully channeled the electrical energy!");
                player.sendMessage(ChatColor.YELLOW + "§l» §r§eThe power surges through the deepslate.");
                player.sendMessage(ChatColor.YELLOW + "§l» §r§eFind and defeat Shocking Forocity now.");
                player.sendMessage(ChatColor.GOLD + "§l✦ ════════════════════════ ✦");

                // Play lightning sound effect
                player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.0f);

                // Zamknij inventory po interakcji
                player.closeInventory(org.bukkit.event.inventory.InventoryCloseEvent.Reason.PLUGIN);

                // Move to next objective
                state.advanceToNextObjective();

                if (debuggingFlag == 1) {
                    player.sendMessage(ChatColor.GRAY + "DEBUG: Chiseled deepslate interaction complete, advancing to KILL_BOSS objective");
                }
            }
        }

        // Sprawdź czy to q2 quest
        if (state.getQuestId().startsWith("q2_")) {

            // Sprawdź czy jesteśmy w fazie interakcji z blokami
            if (state.getCurrentObjective() != QuestState.QuestObjective.INTERACT_WITH_BLOCKS) return;

            if (debuggingFlag == 1) {
                player.sendMessage(ChatColor.GRAY + "DEBUG: Interaction with " + clickedBlock.getType() + " at " +
                        clickedBlock.getX() + ", " + clickedBlock.getY() + ", " + clickedBlock.getZ());
            }

            // Pierwsza faza - zbieranie grzybów
            if (!state.hasCollectedAllMushrooms()) {
                Material blockType = clickedBlock.getType();

                if (blockType != Material.RED_MUSHROOM && blockType != Material.BROWN_MUSHROOM) {
                    return; // Jeśli to nie grzyb, nie kontynuuj
                }

                // Generujemy unikalny ID dla grzyba na podstawie jego położenia
                String mushroomId = blockType.name() + "_" + clickedBlock.getX() + "_" +
                        clickedBlock.getY() + "_" + clickedBlock.getZ();

                // Pobierz mapę interakcji dla gracza lub stwórz nową
                Map<String, Long> playerInteractions = lastMushroomInteractions
                        .computeIfAbsent(player.getUniqueId(), k -> new HashMap<>());

                // Sprawdź czas ostatniej interakcji z tym grzybem
                long currentTime = System.currentTimeMillis();
                long lastInteractionTime = playerInteractions.getOrDefault(mushroomId, 0L);

                // Jeśli minęła mniej niż 1 sekunda od ostatniej interakcji, zignoruj
                if (currentTime - lastInteractionTime < 1000) {
                    return;
                }

                // Zapisz czas tej interakcji
                playerInteractions.put(mushroomId, currentTime);

                // Sprawdzamy czy grzyb został już zebrany
                if (state.isMushroomCollected(mushroomId)) {
                    player.sendMessage(ChatColor.RED + "You've already collected this mushroom!");
                    return;
                }

                if (blockType == Material.RED_MUSHROOM) {
                    // Sprawdzamy czy gracz potrzebuje więcej czerwonych grzybów
                    if (state.getRedMushroomsCollected() >= state.getRequiredRedMushrooms()) {
                        player.sendMessage(ChatColor.RED + "You don't need any more Red Mushrooms!");
                        return;
                    }

                    // Zbieramy grzyb
                    state.collectMushroom(mushroomId, true);
                    player.sendMessage(ChatColor.GREEN + "You've collected a Red Mushroom! (" +
                            state.getRedMushroomsCollected() + "/" + state.getRequiredRedMushrooms() + ")");
                    updateMushroomProgress(player, state);

                    // Zamknij inventory po interakcji
                    player.closeInventory(org.bukkit.event.inventory.InventoryCloseEvent.Reason.PLUGIN);
                } else if (blockType == Material.BROWN_MUSHROOM) {
                    // Sprawdzamy czy gracz potrzebuje więcej brązowych grzybów
                    if (state.getBrownMushroomsCollected() >= state.getRequiredBrownMushrooms()) {
                        player.sendMessage(ChatColor.RED + "You don't need any more Brown Mushrooms!");
                        return;
                    }

                    // Zbieramy grzyb
                    state.collectMushroom(mushroomId, false);
                    player.sendMessage(ChatColor.GREEN + "You've collected a Brown Mushroom! (" +
                            state.getBrownMushroomsCollected() + "/" + state.getRequiredBrownMushrooms() + ")");
                    updateMushroomProgress(player, state);

                    // Zamknij inventory po interakcji
                    player.closeInventory(org.bukkit.event.inventory.InventoryCloseEvent.Reason.PLUGIN);
                }
            }

            // Druga faza - warzenie mikstury
            else if (!state.hasBrewedPotion()) {
                Material blockType = clickedBlock.getType();

                if (blockType == Material.CAULDRON || blockType == Material.WATER_CAULDRON) {
                    state.brewPotion();

                    player.sendTitle(
                            ChatColor.GREEN + "Antidote Brewed!",
                            ChatColor.YELLOW + "You are now immune to the poison",
                            10, 70, 20
                    );

                    player.sendMessage(ChatColor.GOLD + "§l✦ ════════════════════════ ✦");
                    player.sendMessage(ChatColor.GREEN + "§l» §r§aYou've successfully brewed the antidote!");
                    player.sendMessage(ChatColor.YELLOW + "§l» §r§eYou are now immune to the poison areas.");
                    player.sendMessage(ChatColor.YELLOW + "§l» §r§eFind and defeat Xerib the Hunchback.");
                    player.sendMessage(ChatColor.GOLD + "§l✦ ════════════════════════ ✦");

                    // Zamknij inventory po interakcji
                    player.closeInventory(org.bukkit.event.inventory.InventoryCloseEvent.Reason.PLUGIN);

                    // Przejdź do kolejnego kroku
                    state.advanceToNextObjective();

                    if (debuggingFlag == 1) {
                        player.sendMessage(ChatColor.GRAY + "DEBUG: Antidote brewed, advancing to KILL_BOSS objective");
                    }
                }
            }
        }
        else if (state.getQuestId().startsWith("q3_") &&
                state.getCurrentObjective() == QuestState.QuestObjective.INTERACT_WITH_BLOCKS) {

            Material blockType = clickedBlock.getType();

            if (debuggingFlag == 1) {
                player.sendMessage(ChatColor.GRAY + "DEBUG: Interaction with " + blockType + " at " +
                        clickedBlock.getX() + ", " + clickedBlock.getY() + ", " + clickedBlock.getZ());
            }

            QuestData.DungeonQuest questData = QuestData.getQuestData(state.getQuestId());
            if (questData == null) return;

            QuestData.InteractObjective objective = questData.getInteractObjective(state.getCurrentStage());
            if (objective == null) return;

            if (blockType == objective.getBlockType()) {
                // Player interacted with the correct block
                state.setInteractedWithGrindstone(true);

                player.sendTitle(
                        ChatColor.GREEN + "Bones Grinded!",
                        ChatColor.YELLOW + "The protective spell has been weakened",
                        10, 70, 20
                );

                player.sendMessage(ChatColor.GOLD + "§l✦ ════════════════════════ ✦");
                player.sendMessage(ChatColor.GREEN + "§l» §r§aYou've successfully grinded the undead bones!");
                player.sendMessage(ChatColor.YELLOW + "§l» §r§eThe protective spell has been weakened.");
                player.sendMessage(ChatColor.YELLOW + "§l» §r§eFind and defeat the Evil Miller now.");
                player.sendMessage(ChatColor.GOLD + "§l✦ ════════════════════════ ✦");

                // Zamknij inventory po interakcji
                player.closeInventory(org.bukkit.event.inventory.InventoryCloseEvent.Reason.PLUGIN);

                // Move to next objective
                state.advanceToNextObjective();

                if (debuggingFlag == 1) {
                    player.sendMessage(ChatColor.GRAY + "DEBUG: Grindstone interaction complete, advancing to KILL_BOSS objective");
                }
            }
        }
        // Dla wszystkich innych typów questów lub interakcji w przyszłości
        else {
            // Możemy tutaj dodać zamykanie inventory dla przyszłych interakcji
            // Jeśli nastąpiła interakcja z blokiem, który otwiera GUI, zamknij je
            if (clickedBlock.getType().isInteractable()) {
                // Dodajemy małe opóźnienie, aby najpierw otworzyło się GUI, a potem je zamknęło
                plugin.getServer().getScheduler().runTaskLater(plugin, () ->
                        player.closeInventory(org.bukkit.event.inventory.InventoryCloseEvent.Reason.PLUGIN), 1L);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        QuestState state = questManager.getActiveQuest(player.getUniqueId());

        if (state == null) return;

        // Sprawdź czy to q2 quest
        if (!state.getQuestId().startsWith("q2_")) return;

        QuestData.DungeonQuest questData = QuestData.getQuestData(state.getQuestId());
        if (questData == null) return;

        // Sprawdź czy gracz jest w obszarze trucizny
        QuestData.LocationInfo poisonArea = questData.getPoisonArea();
        if (poisonArea != null && poisonArea.isInside(player.getLocation())) {
            handlePoisonArea(player, state);
        } else {
            // Gracz wyszedł z obszaru trucizny
            UUID playerId = player.getUniqueId();
            Integer taskId = poisonTimers.remove(playerId);
            if (taskId != null) {
                Bukkit.getScheduler().cancelTask(taskId);

                if (debuggingFlag == 1) {
                    player.sendMessage(ChatColor.GRAY + "DEBUG: Left poison area, timer cancelled");
                }
            }
        }
    }

    private void updateMushroomProgress(Player player, QuestState state) {
        int redCurrent = state.getRedMushroomsCollected();
        int redRequired = state.getRequiredRedMushrooms();
        int brownCurrent = state.getBrownMushroomsCollected();
        int brownRequired = state.getRequiredBrownMushrooms();

        // Jeśli zebrano wszystkie grzyby, przejdź do fazy z kociołkiem
        if (state.hasCollectedAllMushrooms()) {
            player.sendTitle(
                    ChatColor.GREEN + "All Mushrooms Collected!",
                    ChatColor.YELLOW + "Now brew the antidote at the cauldron",
                    10, 70, 20
            );

            player.sendMessage(ChatColor.GOLD + "§l✦ ════════════════════════ ✦");
            player.sendMessage(ChatColor.GREEN + "§l» §r§aYou've collected all required mushrooms!");
            player.sendMessage(ChatColor.YELLOW + "§l» §r§cRed Mushrooms: " + redCurrent + "/" + redRequired);
            player.sendMessage(ChatColor.YELLOW + "§l» §r§6Brown Mushrooms: " + brownCurrent + "/" + brownRequired);
            player.sendMessage(ChatColor.YELLOW + "§l» §r§eNow find a cauldron to brew an antidote.");
            player.sendMessage(ChatColor.RED + "§l» §r§cBeware of the poisonous areas until you brew the antidote!");
            player.sendMessage(ChatColor.GOLD + "§l✦ ════════════════════════ ✦");

            if (debuggingFlag == 1) {
                player.sendMessage(ChatColor.GRAY + "DEBUG: All mushrooms collected, player should now find cauldron");
            }
        } else {
            // Pokaż postęp
            player.sendMessage(ChatColor.YELLOW + "§l» §r§eMushroom progress:");
            player.sendMessage(ChatColor.YELLOW + "  • §r§cRed Mushrooms: " + redCurrent + "/" + redRequired);
            player.sendMessage(ChatColor.YELLOW + "  • §r§6Brown Mushrooms: " + brownCurrent + "/" + brownRequired);
        }
    }
    private void handlePoisonArea(Player player, QuestState state) {
        // Jeśli gracz ma już odporność, nic nie rób
        if (state.hasPoisonImmunity()) {
            return;
        }

        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        // Pokazuj ostrzeżenie co 5 sekund
        if (!lastPoisonWarningTime.containsKey(playerId) ||
                currentTime - lastPoisonWarningTime.get(playerId) > 5000) {

            player.sendMessage(ChatColor.RED + "§l» §r§cYou're in a poisonous area! Find mushrooms and brew an antidote quickly!");
            lastPoisonWarningTime.put(playerId, currentTime);

            // Dodaj efekt mdłości
            player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100, 1));

            if (debuggingFlag == 1) {
                player.sendMessage(ChatColor.GRAY + "DEBUG: Player in poison area without immunity");
            }
        }

        // Jeśli nie ma jeszcze timera śmierci, utwórz go
        if (!poisonTimers.containsKey(playerId)) {
            int taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                // Sprawdź czy gracz nadal jest online i nie ma odporności
                if (player.isOnline() && !state.hasPoisonImmunity()) {
                    // Zabij gracza
                    player.setHealth(0);
                    player.sendMessage(ChatColor.RED + "§l» §r§cYou've been killed by the poison!");

                    if (debuggingFlag == 1) {
                        player.sendMessage(ChatColor.GRAY + "DEBUG: Player killed by poison");
                    }
                }

                poisonTimers.remove(playerId);
            }, 60L); // 3 sekundy (60 ticków)

            poisonTimers.put(playerId, taskId);

            if (debuggingFlag == 1) {
                player.sendMessage(ChatColor.GRAY + "DEBUG: Poison death timer started");
            }
        }
    }
    public void handlePossibleItemDrop(Player player, String mobId) {
        QuestState state = questManager.getActiveQuest(player.getUniqueId());
        if (state == null) return;

        // Only for Q3, Q6, Q7, and Q8 quests
        if (!state.getQuestId().startsWith("q3_") && !state.getQuestId().startsWith("q6_") && 
            !state.getQuestId().startsWith("q7_") && !state.getQuestId().startsWith("q8_")) return;

        // Ensure we're in collection phase
        if (state.getCurrentObjective() != QuestState.QuestObjective.COLLECT_FROM_MOBS) return;

        QuestData.DungeonQuest questData = QuestData.getQuestData(state.getQuestId());
        if (questData == null) return;

        Map<String, QuestData.CollectObjective> collectObjectives = questData.getCollectObjectives(state.getCurrentStage());
        QuestData.CollectObjective objective = collectObjectives.get(mobId);

        if (objective == null) return;

        if (debuggingFlag == 1) {
            player.sendMessage(ChatColor.GRAY + "DEBUG: Checking drop for mob: " + mobId +
                    " in stage " + state.getCurrentStage());
        }

        // Q3 quest - Stage 1: Collect undead bones
        if (state.getQuestId().startsWith("q3_") && state.getCurrentStage() == 1) {
            // Don't do anything if already collected all
            if (state.hasCollectedAllItems()) return;

            int dropChance = objective.getDropChance(); // 75% for bones
            int roll = ThreadLocalRandom.current().nextInt(100);

            if (roll < dropChance) {
                state.incrementItemsCollected();
                int collected = state.getItemsCollected();
                int required = state.getRequiredItems();

                player.sendMessage(ChatColor.GREEN + "§l» §r§aYou found " + objective.getDisplayName() +
                        "! (" + collected + "/" + required + ")");

                String progressBar = createProgressBar(collected, required);
                player.sendMessage(ChatColor.YELLOW + "Progress: " + progressBar);

                // If collected all bones
                if (collected >= required) {
                    player.sendTitle(
                            ChatColor.GREEN + "All " + objective.getDisplayName() + " Collected!",
                            ChatColor.YELLOW + "Now find a grindstone to grind the bones",
                            10, 70, 20
                    );

                    player.sendMessage(ChatColor.GOLD + "§l✦ ════════════════════════ ✦");
                    player.sendMessage(ChatColor.GREEN + "§l» §r§aYou've collected all required " +
                            objective.getDisplayName() + "!");
                    player.sendMessage(ChatColor.YELLOW + "§l» §r§eNow find a grindstone to grind them.");
                    player.sendMessage(ChatColor.GOLD + "§l✦ ════════════════════════ ✦");

                    state.advanceToNextObjective(); // Move to INTERACT_WITH_BLOCKS
                }
            }
        }
        // Q3 quest - Stage 2: Collect emerald rune fragment
        else if (state.getQuestId().startsWith("q3_") && state.getCurrentStage() == 2) {
            // Only for slain_assassin mobs
            if (!mobId.contains("slain_assassin")) return;

            if (state.getRuneFragmentsCollected() >= 1) return; // Already have the fragment

            // Get kill count for this mob type
            int killCount = state.getKillCount(mobId);

            // Progressive drop chance
            int dropChance;
            if (killCount == 0) dropChance = 30;      // First mob: 30%
            else if (killCount == 1) dropChance = 60; // Second mob: 60%
            else dropChance = 100;                    // Third mob: 100%

            // Always increment kill counter
            int newKillCount = state.incrementKillCount(mobId);

            if (debuggingFlag == 1) {
                player.sendMessage(ChatColor.GRAY + "DEBUG: Kill count for " + mobId + ": " + newKillCount +
                        ", drop chance: " + dropChance + "%");
            }

            // Check for rune fragment drop
            int roll = ThreadLocalRandom.current().nextInt(100);
            if (roll < dropChance) {
                // Fragment found!
                state.incrementRuneFragmentsCollected();

                player.sendTitle(
                        ChatColor.GREEN + "Emerald Rune Fragment Found!",
                        ChatColor.YELLOW + "Now find The Bloody Arrow",
                        10, 70, 20
                );

                player.sendMessage(ChatColor.GOLD + "§l✦ ════════════════════════ ✦");
                player.sendMessage(ChatColor.GREEN + "§l» §r§aYou've found the Emerald Rune Fragment!");
                player.sendMessage(ChatColor.YELLOW + "§l» §r§eNow defeat The Bloody Arrow to get the second fragment.");
                player.sendMessage(ChatColor.GOLD + "§l✦ ════════════════════════ ✦");

                // Move to boss phase
                state.advanceToNextObjective();

                if (debuggingFlag == 1) {
                    player.sendMessage(ChatColor.GRAY + "DEBUG: Rune fragment found! Advancing to KILL_BOSS objective");
                }
            } else {
                if (debuggingFlag == 1) {
                    player.sendMessage(ChatColor.GRAY + "DEBUG: Rune fragment didn't drop (roll: " + roll +
                            " vs chance: " + dropChance + "%)");
                }
            }
        }
        // Q6 quest - Stage 2: Collect dagger parts
        else if (state.getQuestId().startsWith("q6_") && state.getCurrentStage() == 2) {
            String partType = "";

            if (mobId.contains("elite_skeleton_archer")) {
                partType = "dagger_part_1";
            } else if (mobId.contains("elite_skeleton_warrior")) {
                partType = "dagger_part_2";
            } else if (mobId.contains("death_archer")) {
                partType = "dagger_part_3";
            } else {
                return; // Not a relevant mob
            }

            // Skip if already have this part
            if (state.hasDaggerPart(partType)) {
                return;
            }

            // Get kill count for this mob type
            int killCount = state.incrementKillCount(mobId);

            // Determine drop chance based on mob type
            int dropChance = objective.getDropChance(); // Default 30%

            // For death_archer, use progressive drop rate
            if (partType.equals("dagger_part_3") && objective.isProgressive()) {
                if (killCount == 1) dropChance = 30;      // First kill: 30%
                else if (killCount == 2) dropChance = 60; // Second kill: 60%
                else dropChance = 100;                    // Third kill: 100%
            }
            // For other parts, guarantee after X kills
            else if (objective.getGuaranteedAfterKills() > 0 && killCount >= objective.getGuaranteedAfterKills()) {
                dropChance = 100; // Guaranteed drop
            }

            if (debuggingFlag == 1) {
                player.sendMessage(ChatColor.GRAY + "DEBUG: Kill count for " + mobId + ": " + killCount +
                        ", drop chance: " + dropChance + "%");
            }

            // Check for dagger part drop
            int roll = ThreadLocalRandom.current().nextInt(100);
            if (roll < dropChance) {
                // Dagger part found!
                state.collectDaggerPart(partType);

                player.sendMessage(ChatColor.GOLD + "§l✦ ════════════════════════ ✦");
                player.sendMessage(ChatColor.GREEN + "§l» §r§aYou've found the " + objective.getDisplayName() + "!");

                // Check if all parts are collected
// When all dagger parts are collected
// Check if all parts are collected
// When all dagger parts are collected
                if (state.hasCollectedAllDaggerParts()) {
                    player.sendTitle(
                            ChatColor.GREEN + "All Dagger Parts Found!",
                            ChatColor.YELLOW + "Now find and defeat Murot High Priest",
                            10, 70, 20
                    );

                    player.sendMessage(ChatColor.GOLD + "§l✦ ════════════════════════ ✦");
                    player.sendMessage(ChatColor.GREEN + "§l» §r§aYou've collected all parts of the Sacrificial Dagger!");

                    // Get boss objective details
                    QuestData.BossObjective bossObj = questData.getBossObjectiveDetails(state.getCurrentStage());
                    if (bossObj != null) {
                        player.sendMessage(ChatColor.LIGHT_PURPLE + "§l» §r§d" + bossObj.getObjectiveText());
                    }

                    player.sendMessage(ChatColor.GOLD + "§l✦ ════════════════════════ ✦");

                    // Advance to boss phase
                    state.advanceToNextObjective();

                    if (debuggingFlag == 1) {
                        player.sendMessage(ChatColor.GRAY + "DEBUG: All dagger parts collected, advancing to KILL_BOSS objective");
                    }

                    // Check if boss is already killed (can happen if parts are collected after killing boss)
                    if (mobId.contains("murot_high_priest")) {
                        if (debuggingFlag == 1) {
                            player.sendMessage(ChatColor.GRAY + "DEBUG: Boss already killed, forcing portal phase");
                        }
                        state.setBossKilled(true);
                        state.advanceToNextObjective(); // Move to FIND_PORTAL
                        questManager.handleStageComplete(player);
                    }

                    // Return early to avoid duplicate messages
                    return;
                } else {
                    player.sendMessage(ChatColor.YELLOW + "§l» §r§eContinue searching for the remaining dagger parts.");
                    player.sendMessage(ChatColor.GOLD + "§l✦ ════════════════════════ ✦");
                }


                if (debuggingFlag == 1) {
                    player.sendMessage(ChatColor.GRAY + "DEBUG: Dagger part found: " + partType);
                }
            } else {
                if (debuggingFlag == 1) {
                    player.sendMessage(ChatColor.GRAY + "DEBUG: Dagger part didn't drop (roll: " + roll +
                            " vs chance: " + dropChance + "%)");
                }
            }
        }
        // Q7 quest - Stage 1: Collect catapult balls
        else if (state.getQuestId().startsWith("q7_") && state.getCurrentStage() == 1) {
            // Only check for mechanoid mobs
            if (!mobId.contains("b1000_combat_mechanoid")) return;

            // Check if already collected max balls
            if (state.getCatapultBallsCollected() >= 2) return;

            int dropChance = objective.getDropChance(); // 50% for catapult balls
            int roll = ThreadLocalRandom.current().nextInt(100);

            if (roll < dropChance) {
                // Ball collected!
                state.incrementCatapultBalls();
                int collected = state.getCatapultBallsCollected();

                player.sendMessage(ChatColor.GREEN + "§l» §r§aYou found a " + objective.getDisplayName() +
                        "! (" + collected + "/2)");

                // If collected all balls, advance to lever phase
                if (collected >= 2) {
                    player.sendTitle(
                        ChatColor.GREEN + "Catapult Balls Collected!",
                        ChatColor.YELLOW + "Now find and activate two catapults",
                        10, 70, 20
                    );

                    player.sendMessage(ChatColor.GOLD + "§l✦ ════════════════════════ ✦");
                    player.sendMessage(ChatColor.GREEN + "§l» §r§aYou've collected all catapult balls!");
                    player.sendMessage(ChatColor.YELLOW + "§l» §r§eNow find and fire TWO different catapults.");
                    player.sendMessage(ChatColor.YELLOW + "§l» §r§e(Right-click on two different levers)");
                    player.sendMessage(ChatColor.GOLD + "§l✦ ════════════════════════ ✦");

                    if (debuggingFlag == 1) {
                        player.sendMessage(ChatColor.GRAY + "DEBUG: All catapult balls collected (" + collected + "/2)");
                        player.sendMessage(ChatColor.GRAY + "DEBUG: Current objective before advancing: " + state.getCurrentObjective());
                    }

                    // Move to lever phase
                    state.advanceToNextObjective();

                    if (debuggingFlag == 1) {
                        player.sendMessage(ChatColor.GRAY + "DEBUG: Objective after advancing: " + state.getCurrentObjective());
                    }
                }
            }
        }
        // Q8 quest - Stage 1: Collect electrical shards
        else if (state.getQuestId().startsWith("q8_") && state.getCurrentStage() == 1) {
            // Only check for electrified_ferocity mobs
            if (!mobId.contains("electrified_ferocity")) return;

            // Check if already collected max shards
            if (state.getItemsCollected() >= state.getRequiredItems()) return;

            int dropChance = objective.getDropChance(); // 75% for electrical shards
            int roll = ThreadLocalRandom.current().nextInt(100);

            if (roll < dropChance) {
                // Shard collected!
                state.incrementItemsCollected();
                int collected = state.getItemsCollected();
                int required = state.getRequiredItems();

                player.sendMessage(ChatColor.YELLOW + "§l» §r§eYou found an " + objective.getDisplayName() +
                        "! (" + collected + "/" + required + ")");

                // Add electrical effect
                player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_BLAZE_HURT, 0.5f, 2.0f);

                String progressBar = createProgressBar(collected, required);
                player.sendMessage(ChatColor.AQUA + "Progress: " + progressBar);

                // If collected all shards
                if (collected >= required) {
                    player.sendTitle(
                            ChatColor.YELLOW + "All Electrical Shards Collected!",
                            ChatColor.GREEN + "Now find chiseled deepslate to channel the energy",
                            10, 70, 20
                    );

                    player.sendMessage(ChatColor.GOLD + "§l✦ ════════════════════════ ✦");
                    player.sendMessage(ChatColor.GREEN + "§l» §r§aYou've collected all required " +
                            objective.getDisplayName() + "s!");
                    player.sendMessage(ChatColor.YELLOW + "§l» §r§eThe electrical energy crackles around you.");
                    player.sendMessage(ChatColor.YELLOW + "§l» §r§eFind any chiseled deepslate block to channel this power.");
                    player.sendMessage(ChatColor.GOLD + "§l✦ ════════════════════════ ✦");

                    state.advanceToNextObjective(); // Move to INTERACT_WITH_BLOCKS

                    if (debuggingFlag == 1) {
                        player.sendMessage(ChatColor.GRAY + "DEBUG: All electrical shards collected, advancing to INTERACT_WITH_BLOCKS");
                    }
                }
            }
        }
    }    private String createProgressBar(int current, int max) {
        StringBuilder bar = new StringBuilder();
        int totalBars = 20;
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
    // Dodaj tę metodę publiczną
    public void clearPlayerData(UUID playerId) {
        lastMushroomInteractions.remove(playerId);
        lastPoisonWarningTime.remove(playerId);

        // Anuluj też timer trucizny, jeśli istnieje
        Integer taskId = poisonTimers.remove(playerId);
        if (taskId != null) {
            plugin.getServer().getScheduler().cancelTask(taskId);
        }

        if (debuggingFlag == 1) {
            Player player = plugin.getServer().getPlayer(playerId);
            if (player != null) {
                player.sendMessage(ChatColor.GRAY + "DEBUG: Interaction data cleared for player");
            }
        }
    }
    public void cleanupInactivePlayerData() {
        // Usuń dane dla graczy bez aktywnego questa
        lastMushroomInteractions.keySet().removeIf(playerId ->
                !questManager.hasActiveQuest(playerId));

        // Usuń starsze wpisy (> 10 minut) dla aktywnych graczy
        long currentTime = System.currentTimeMillis();
        for (Map<String, Long> interactions : lastMushroomInteractions.values()) {
            interactions.entrySet().removeIf(entry ->
                    currentTime - entry.getValue() > 1800000); // 30 minut
        }
    }

}
