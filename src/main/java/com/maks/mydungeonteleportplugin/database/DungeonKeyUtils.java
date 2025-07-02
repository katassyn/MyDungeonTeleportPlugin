package com.maks.mydungeonteleportplugin.database;

/**
 * Utility class for handling dungeon keys
 */
public class DungeonKeyUtils {

    /**
     * Convert a selectedMap value to a dungeon key
     * @param selectedMap The selectedMap value (e.g., "q1_m1_inf")
     * @return The dungeon key (e.g., "q1_inf")
     */
    public static String getDungeonKeyFromSelectedMap(String selectedMap) {
        if (selectedMap == null) return null;
        
        // Pattern: q1_m1_inf -> q1_inf
        // Pattern: q10_m1_blood -> q10_blood
        String[] parts = selectedMap.split("_");
        if (parts.length >= 3) {
            return parts[0] + "_" + parts[2]; // e.g., q1_inf
        }
        
        return selectedMap; // Fallback
    }
    
    /**
     * Get a formatted dungeon name from a dungeon key
     * @param dungeonKey The dungeon key (e.g., "q1_inf")
     * @return A formatted dungeon name (e.g., "Q1 Infernal")
     */
    public static String getFormattedDungeonName(String dungeonKey) {
        if (dungeonKey == null) return "Unknown";
        
        String[] parts = dungeonKey.split("_");
        if (parts.length != 2) return dungeonKey;
        
        String questNumber = parts[0].toUpperCase();
        String difficulty = parts[1].toLowerCase();
        
        String difficultyName;
        switch (difficulty) {
            case "inf":
                difficultyName = "Infernal";
                break;
            case "hell":
                difficultyName = "Hell";
                break;
            case "blood":
                difficultyName = "Bloodshed";
                break;
            default:
                difficultyName = difficulty;
        }
        
        return questNumber + " " + difficultyName;
    }
    
    /**
     * Get the GUI title for a dungeon's drop preview
     * @param dungeonKey The dungeon key (e.g., "q1_inf")
     * @return The GUI title (e.g., "Possible Drops - Q1 Infernal")
     */
    public static String getDropPreviewTitle(String dungeonKey) {
        return "Possible Drops - " + getFormattedDungeonName(dungeonKey);
    }
}