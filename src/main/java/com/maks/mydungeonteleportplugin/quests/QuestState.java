package com.maks.mydungeonteleportplugin.quests;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks the state of a player's active quest
 */
public class QuestState {
    public enum QuestObjective {
        FIND_LOCATION,
        INTERACT_WITH_BLOCKS,
        KILL_MOBS,
        KILL_BOSS,
        FIND_PORTAL,
        COLLECT_FROM_MOBS
    }

    private final UUID playerId;
    private final String questId;
    private int currentStage;
    private QuestObjective currentObjective;
    private final Map<String, Integer> mobKillCounts;
    private boolean locationFound;
    private boolean bossKilled;
    private boolean portalFound;
    private boolean waitingForTeleport;
    private long teleportStartTime;
    private int timeoutTaskId;
    private int redMushroomsCollected = 0;
    private int brownMushroomsCollected = 0;
    private boolean hasBrewedPotion = false;
    private boolean hasPoisonImmunity = false;
    private final Map<String, Boolean> collectedMushrooms = new HashMap<>(); // Śledzenie konkretnych grzybów po ID
    private int requiredRedMushrooms = 0;
    private int requiredBrownMushrooms = 0;
    private int itemsCollected = 0;
    private int requiredItems = 0;
    private boolean hasInteractedWithGrindstone = false;
    private boolean miniBossInvulnerable = true;
    private int runeFragmentsCollected = 0;
    private final Map<String, Boolean> daggerPartsCollected = new HashMap<>();
    private final Map<String, Integer> guaranteedKillsCount = new HashMap<>();

    // Q7 quest state
    private int catapultBallsCollected = 0;
    private final java.util.Set<String> usedLevers = new java.util.HashSet<>();
    private boolean altar1Activated = false;
    private boolean altar2Activated = false;
    private boolean inQ7SpecialTransition = false;

    // Q9 quest state
    private final java.util.Set<Integer> selectedStatues = new java.util.HashSet<>(); // Which 4 statues are active
    private final java.util.Set<String> collectedStatues = new java.util.HashSet<>(); // Statue IDs collected
    private final java.util.Set<String> activatedAltars = new java.util.HashSet<>(); // Altar locations activated

    // Q10 quest state
    private int fragmentsCollected = 0;
    private int fragmentsDeposited = 0;
    private boolean hasStatuette = false;
    private final java.util.Set<String> usedShulkerBoxes = new java.util.HashSet<>();
    private final java.util.Set<String> usedLodestones = new java.util.HashSet<>();
    private boolean hasFragmentInInventory = false; // Track if player has an undeposited fragment

    // Debug flag
    private int debuggingFlag = 0; // Set to 0 when everything is working

    public QuestState(UUID playerId, String questId) {
        this.playerId = playerId;
        this.questId = questId;
        this.currentStage = 1;
        this.currentObjective = QuestObjective.FIND_LOCATION;
        this.mobKillCounts = new HashMap<>();
        this.locationFound = false;
        this.bossKilled = false;
        this.portalFound = false;
        this.waitingForTeleport = false;
        this.teleportStartTime = 0;
        this.timeoutTaskId = -1;
    }

    // Getters
    public UUID getPlayerId() { return playerId; }
    public String getQuestId() { return questId; }
    public int getCurrentStage() { return currentStage; }
    public QuestObjective getCurrentObjective() { return currentObjective; }
    public Map<String, Integer> getMobKillCounts() { return mobKillCounts; }
    public boolean isLocationFound() { return locationFound; }
    public boolean isBossKilled() { return bossKilled; }
    public boolean isPortalFound() { return portalFound; }
    public boolean isWaitingForTeleport() { return waitingForTeleport; }
    public long getTeleportStartTime() { return teleportStartTime; }
    public int getTimeoutTaskId() { return timeoutTaskId; }
    public boolean hasDaggerPart(String partType) {
        return daggerPartsCollected.getOrDefault(partType, false);
    }

    public void collectDaggerPart(String partType) {
        daggerPartsCollected.put(partType, true);
    }

    public boolean hasCollectedAllDaggerParts() {
        return daggerPartsCollected.size() >= 3;
    }

    public void clearDaggerParts() {
        daggerPartsCollected.clear();
    }
    // Setters
    public void setTimeoutTaskId(int timeoutTaskId) { this.timeoutTaskId = timeoutTaskId; }

    public void advanceToNextStage() {
        currentStage++;
        resetObjectives();

        // Dla q3, etap 2 powinien zaczynać się od zbierania fragmentów runy
        if (questId.startsWith("q3_") && currentStage == 2) {
            currentObjective = QuestObjective.COLLECT_FROM_MOBS;
        }
        // Dla q3, etap 3 powinien zaczynać się od razu od walki z bossem
        else if (questId.startsWith("q3_") && currentStage == 3) {
            currentObjective = QuestObjective.KILL_BOSS;
            setLocationFound(true); // Pomijamy etap znajdowania lokacji
        }
        // For Q10, stage 2 should start with COLLECT_FROM_MOBS objective
        else if (questId.startsWith("q10_") && currentStage == 2) {
            currentObjective = QuestObjective.COLLECT_FROM_MOBS;
            setLocationFound(true);
        }
        // For Q10, stage 3 should start with KILL_BOSS objective
        else if (questId.startsWith("q10_") && currentStage == 3) {
            currentObjective = QuestObjective.KILL_BOSS;
            setLocationFound(true);
        }
        else {
            currentObjective = QuestObjective.FIND_LOCATION;
        }
    }

    public void advanceToNextObjective() {
        switch (currentObjective) {
            case FIND_LOCATION:
                if (questId.startsWith("q2_")) {
                    currentObjective = QuestObjective.INTERACT_WITH_BLOCKS;
                } else if (questId.startsWith("q3_")) {
                    currentObjective = QuestObjective.COLLECT_FROM_MOBS;
                } else {
                    currentObjective = QuestObjective.KILL_MOBS;
                }
                break;
            case COLLECT_FROM_MOBS:
                if (questId.startsWith("q3_") && getCurrentStage() == 1) {
                    currentObjective = QuestObjective.INTERACT_WITH_BLOCKS;
                } else if (questId.startsWith("q3_") && getCurrentStage() == 2) {
                    currentObjective = QuestObjective.KILL_BOSS;
                } else if (questId.startsWith("q7_") && getCurrentStage() == 1) {
                    // Add this case for Q7
                    currentObjective = QuestObjective.INTERACT_WITH_BLOCKS;

                    if (debuggingFlag == 1) {
                        // Get player for debug messages
                        org.bukkit.entity.Player player = org.bukkit.Bukkit.getPlayer(playerId);
                        if (player != null) {
                            player.sendMessage(org.bukkit.ChatColor.GRAY + "DEBUG: Advancing from COLLECT_FROM_MOBS to INTERACT_WITH_BLOCKS for Q7");
                        }
                    }
                } else if (questId.startsWith("q8_") && getCurrentStage() == 1) {
                    // Add this case for Q8
                    currentObjective = QuestObjective.INTERACT_WITH_BLOCKS;

                    if (debuggingFlag == 1) {
                        org.bukkit.entity.Player player = org.bukkit.Bukkit.getPlayer(playerId);
                        if (player != null) {
                            player.sendMessage(org.bukkit.ChatColor.GRAY + "DEBUG: Advancing from COLLECT_FROM_MOBS to INTERACT_WITH_BLOCKS for Q8");
                        }
                    }
                } else if (questId.startsWith("q10_") && getCurrentStage() == 2) {
                    // For Q10 stage 2, advance from collect to boss
                    currentObjective = QuestObjective.KILL_BOSS;
                    setMiniBossInvulnerable(false); // Make Akheilos vulnerable after collecting statuette

                    if (debuggingFlag == 1) {
                        org.bukkit.entity.Player player = org.bukkit.Bukkit.getPlayer(playerId);
                        if (player != null) {
                            player.sendMessage(org.bukkit.ChatColor.GRAY + "DEBUG: Advancing from COLLECT_FROM_MOBS to KILL_BOSS for Q10");
                        }
                    }
                }
                break;
            case INTERACT_WITH_BLOCKS:
                if (questId.startsWith("q3_")) {
                    currentObjective = QuestObjective.KILL_BOSS;
                    setMiniBossInvulnerable(false);
                } else if (questId.startsWith("q10_") && getCurrentStage() == 1) {
                    // For Q10 stage 1, make Melas vulnerable after depositing all fragments
                    currentObjective = QuestObjective.KILL_BOSS;
                    setMiniBossInvulnerable(false);
                } else {
                    currentObjective = QuestObjective.KILL_BOSS;
                }
                break;
            case KILL_MOBS:
                currentObjective = QuestObjective.KILL_BOSS;
                break;
            case KILL_BOSS:
                currentObjective = QuestObjective.FIND_PORTAL;
                break;
            case FIND_PORTAL:
                // Next stage should be handled separately
                break;
        }
    }

    public void resetObjectives() {
        locationFound = false;
        bossKilled = false;
        portalFound = false;
        waitingForTeleport = false;
        mobKillCounts.clear();
        redMushroomsCollected = 0;
        brownMushroomsCollected = 0;
        requiredRedMushrooms = 0;
        requiredBrownMushrooms = 0;
        collectedMushrooms.clear();
        hasBrewedPotion = false;
        hasPoisonImmunity = false;
        itemsCollected = 0;
        requiredItems = 0;
        hasInteractedWithGrindstone = false;
        miniBossInvulnerable = true;
        runeFragmentsCollected = 0;
        daggerPartsCollected.clear();
        guaranteedKillsCount.clear();

        // Reset Q7 quest state
        catapultBallsCollected = 0;
        usedLevers.clear();
        altar1Activated = false;
        altar2Activated = false;

        // Reset Q9 quest state
        selectedStatues.clear();
        collectedStatues.clear();
        activatedAltars.clear();

        // Reset Q10 quest state
        fragmentsCollected = 0;
        fragmentsDeposited = 0;
        hasStatuette = false;
        usedShulkerBoxes.clear();
        usedLodestones.clear();
        hasFragmentInInventory = false;
    }

    // Kill tracking
    public int incrementKillCount(String mobId) {
        int current = mobKillCounts.getOrDefault(mobId, 0);
        mobKillCounts.put(mobId, current + 1);
        return current + 1;
    }

    public int getKillCount(String mobId) {
        return mobKillCounts.getOrDefault(mobId, 0);
    }

    public boolean areKillsComplete(Map<String, Integer> requiredKills) {
        if (requiredKills.isEmpty()) {
            return true;
        }

        for (Map.Entry<String, Integer> entry : requiredKills.entrySet()) {
            String mobId = entry.getKey();
            int required = entry.getValue();
            int killed = getKillCount(mobId);

            if (killed < required) {
                return false;
            }
        }

        return true;
    }

    // Objective completion flags
    public void setLocationFound(boolean found) {
        this.locationFound = found;
    }

    public void setBossKilled(boolean killed) {
        this.bossKilled = killed;
    }

    public void setPortalFound(boolean found) {
        this.portalFound = found;
    }

    public void setWaitingForTeleport(boolean waiting) {
        this.waitingForTeleport = waiting;
        if (waiting) {
            this.teleportStartTime = System.currentTimeMillis();
        }
    }
    public void collectMushroom(boolean isRed) {
        if (isRed) {
            redMushroomsCollected++;
        } else {
            brownMushroomsCollected++;
        }
    }

    public int getRedMushroomsCollected() {
        return redMushroomsCollected;
    }

    public int getBrownMushroomsCollected() {
        return brownMushroomsCollected;
    }

    public int getTotalMushroomsCollected() {
        return redMushroomsCollected + brownMushroomsCollected;
    }

    public boolean hasCollectedAllMushrooms() {
        return getTotalMushroomsCollected() >= 5; // Wymagamy 5 grzybów
    }

    public boolean hasBrewedPotion() {
        return hasBrewedPotion;
    }

    public void brewPotion() {
        this.hasBrewedPotion = true;
        this.hasPoisonImmunity = true;
    }

    public boolean hasPoisonImmunity() {
        return hasPoisonImmunity;
    }
    // Dodaj te metody
    public void setMushroomRequirements(int red, int brown) {
        this.requiredRedMushrooms = red;
        this.requiredBrownMushrooms = brown;
    }

    public boolean isMushroomCollected(String mushroomId) {
        return collectedMushrooms.getOrDefault(mushroomId, false);
    }

    public void collectMushroom(String mushroomId, boolean isRed) {
        collectedMushrooms.put(mushroomId, true);
        if (isRed) {
            redMushroomsCollected++;
        } else {
            brownMushroomsCollected++;
        }
    }

    public int getRequiredRedMushrooms() {
        return requiredRedMushrooms;
    }

    public int getRequiredBrownMushrooms() {
        return requiredBrownMushrooms;
    }
    public boolean isInteractionComplete() {
        // W pierwszej fazie zbieramy grzyby, w drugiej warzymy miksturę
        if (!hasCollectedAllMushrooms()) {
            return false;
        }
        return hasBrewedPotion();
    }
    // Getters and setters
    public int getItemsCollected() {
        return itemsCollected;
    }

    public void incrementItemsCollected() {
        this.itemsCollected++;
    }

    public void setRequiredItems(int requiredItems) {
        this.requiredItems = requiredItems;
    }

    public int getRequiredItems() {
        return requiredItems;
    }

    public boolean hasCollectedAllItems() {
        return itemsCollected >= requiredItems;
    }

    public boolean hasInteractedWithGrindstone() {
        return hasInteractedWithGrindstone;
    }

    public void setInteractedWithGrindstone(boolean interacted) {
        this.hasInteractedWithGrindstone = interacted;
    }

    public boolean isMiniBossInvulnerable() {
        return miniBossInvulnerable;
    }

    public void setMiniBossInvulnerable(boolean invulnerable) {
        this.miniBossInvulnerable = invulnerable;
    }

    public int getRuneFragmentsCollected() {
        return runeFragmentsCollected;
    }

    public void incrementRuneFragmentsCollected() {
        this.runeFragmentsCollected++;
    }

    // Q7 quest methods
    public int getCatapultBallsCollected() {
        return catapultBallsCollected;
    }

    public void incrementCatapultBalls() {
        catapultBallsCollected++;
    }

    public boolean hasUsedLever(String leverId) {
        return usedLevers.contains(leverId);
    }

    public void useLever(String leverId) {
        usedLevers.add(leverId);
    }

    public int getLeverCount() {
        return usedLevers.size();
    }

    public boolean isAltar1Activated() {
        return altar1Activated;
    }

    public boolean isAltar2Activated() {
        return altar2Activated;
    }

    public void setAltar1Activated(boolean activated) {
        this.altar1Activated = activated;
    }

    public void setAltar2Activated(boolean activated) {
        this.altar2Activated = activated;
    }

    public void setInQ7SpecialTransition(boolean value) {
        inQ7SpecialTransition = value;
    }

    public boolean isInQ7SpecialTransition() {
        return inQ7SpecialTransition;
    }

    // Q9 quest methods
    public boolean isStatueSelected(int index) {
        return selectedStatues.contains(index);
    }

    public void setSelectedStatues(java.util.Set<Integer> statues) {
        selectedStatues.clear();
        selectedStatues.addAll(statues);
    }

    public boolean hasCollectedStatue(String statueId) {
        return collectedStatues.contains(statueId);
    }

    public void collectStatue(String statueId) {
        collectedStatues.add(statueId);
    }

    public int getStatueFragmentsCollected() {
        return collectedStatues.size();
    }

    public boolean hasCollectedAllStatues() {
        return getStatueFragmentsCollected() >= 4;
    }

    public boolean hasActivatedAltar(String altarId) {
        return activatedAltars.contains(altarId);
    }

    public void activateAltar(String altarId) {
        activatedAltars.add(altarId);
    }

    public int getAltarsActivated() {
        return activatedAltars.size();
    }

    public boolean hasActivatedAllAltars() {
        return getAltarsActivated() >= 5;
    }

    public java.util.Set<String> getActivatedAltars() {
        return activatedAltars;
    }

    // Q10 quest methods
    public int getFragmentsCollected() {
        return fragmentsCollected;
    }

    public int getFragmentsDeposited() {
        return fragmentsDeposited;
    }

    public boolean hasFragmentInInventory() {
        return hasFragmentInInventory;
    }

    public void collectFragment(String shulkerBoxId) {
        if (!hasFragmentInInventory) {
            usedShulkerBoxes.add(shulkerBoxId);
            fragmentsCollected++;
            hasFragmentInInventory = true;
        }
    }

    public void depositFragment(String lodestoneId) {
        if (hasFragmentInInventory) {
            usedLodestones.add(lodestoneId);
            fragmentsDeposited++;
            hasFragmentInInventory = false;
        }
    }

    public boolean hasUsedShulkerBox(String shulkerBoxId) {
        return usedShulkerBoxes.contains(shulkerBoxId);
    }

    public boolean hasUsedLodestone(String lodestoneId) {
        return usedLodestones.contains(lodestoneId);
    }

    public boolean hasAllFragmentsDeposited() {
        return fragmentsDeposited >= 3;
    }

    public boolean hasStatuette() {
        return hasStatuette;
    }

    public void setHasStatuette(boolean hasStatuette) {
        this.hasStatuette = hasStatuette;
    }

    public void setCurrentObjective(QuestObjective objective) {
        this.currentObjective = objective;
    }
    // Check if current objective is complete
    public boolean isCurrentObjectiveComplete(QuestData.DungeonQuest questData) {
        switch (currentObjective) {
            case FIND_LOCATION:
                return locationFound;
            case KILL_MOBS:
                return areKillsComplete(questData.getKillObjectives(currentStage));
            case KILL_BOSS:
                return bossKilled;
            case FIND_PORTAL:
                return portalFound;
            default:
                return false;
        }
    }
}
