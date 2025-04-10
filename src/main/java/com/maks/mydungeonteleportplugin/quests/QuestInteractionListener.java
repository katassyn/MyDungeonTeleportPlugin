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
    private int debuggingFlag = 1;

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

        // Tylko dla Q3 questów
        if (!state.getQuestId().startsWith("q3_")) return;

        // Upewnij się, że jesteśmy w fazie zbierania przedmiotów
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

        // ETAP 1: Zbieranie kości nieumarlych
        if (state.getCurrentStage() == 1) {
            // Nie robi nic jeśli już zbrano wszystkie
            if (state.hasCollectedAllItems()) return;

            int dropChance = objective.getDropChance(); // 75% dla kości
            int roll = ThreadLocalRandom.current().nextInt(100);

            if (roll < dropChance) {
                state.incrementItemsCollected();
                int collected = state.getItemsCollected();
                int required = state.getRequiredItems();

                player.sendMessage(ChatColor.GREEN + "§l» §r§aYou found " + objective.getDisplayName() +
                        "! (" + collected + "/" + required + ")");

                String progressBar = createProgressBar(collected, required);
                player.sendMessage(ChatColor.YELLOW + "Progress: " + progressBar);

                // Jeśli zebraliśmy wszystkie kości
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

                    state.advanceToNextObjective(); // Przejście do INTERACT_WITH_BLOCKS
                }
            }
        }
        // ETAP 2: Zbieranie fragmentu runy
        else if (state.getCurrentStage() == 2) {
            // Tylko dla moby slain_assassin
            if (!mobId.contains("slain_assassin")) return;

            if (state.getRuneFragmentsCollected() >= 1) return; // Już mamy fragment

            // Pobierz liczbę zabitych mobów tego typu
            int killCount = state.getKillCount(mobId);

            // Progresywna szansa na drop
            int dropChance;
            if (killCount == 0) dropChance = 30;      // Pierwszy mob: 30%
            else if (killCount == 1) dropChance = 60; // Drugi mob: 60%
            else dropChance = 100;                    // Trzeci mob: 100%

            // Zawsze zwiększaj licznik zabić
            int newKillCount = state.incrementKillCount(mobId);

            if (debuggingFlag == 1) {
                player.sendMessage(ChatColor.GRAY + "DEBUG: Kill count for " + mobId + ": " + newKillCount +
                        ", drop chance: " + dropChance + "%");
            }

            // Sprawdź, czy wypada fragment runy
            int roll = ThreadLocalRandom.current().nextInt(100);
            if (roll < dropChance) {
                // Fragment znaleziony!
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

                // Przejście do etapu walki z mini-bossem
                state.advanceToNextObjective(); // Przejście do KILL_BOSS

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
    }
    private String createProgressBar(int current, int max) {
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