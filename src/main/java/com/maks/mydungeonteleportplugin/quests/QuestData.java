package com.maks.mydungeonteleportplugin.quests;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public class QuestData {
    private static final Map<String, DungeonQuest> questData = new HashMap<>();

    static {
        initializeQ1();
        initializeQ2();
        initializeQ3();
    }

    private static void initializeQ1() {
        // Q1 Infernal
        DungeonQuest q1Inf = new DungeonQuest("q1_inf", "Q1 Infernal", 50, 0.5, "Qinf", 10);
        q1Inf.setLocationMessage("Find the Forgotten Circle and investigate");

        // Stage 1
        q1Inf.addLocationObjective(1, "world", -1219, -61, 19, -1219, -58, 40);
        q1Inf.addKillObjective(1, "flamecult_servant_inf", 25, "Flamecult Servants");
        q1Inf.addKillObjective(1, "flamecult_archer_inf", 25, "Flamecult Archers");
        q1Inf.setBossObjective(1, "raazghul_the_corruptor_inf", "Mini-Boss: Raazghul the Corruptor");
        q1Inf.addPortalObjective(1, "world", -1095, -61, 318, -1096, -59, 318);
        q1Inf.setStageEndCommand(1, "warp q1_m2_inf");

        // Stage 2
        q1Inf.setBossObjective(2, "perral_world_dragonknight_inf", "Mini-Boss: Perral World Dragonknight");
        q1Inf.addPortalObjective(2, "world", -1858, -61, -378, -1857, -59, -378);
        q1Inf.setStageEndCommand(2, "warp q1_m3_inf");

        // Stage 3
        q1Inf.setBossObjective(3, "grimmag_inf", "Final Boss: Grimmag");

        questData.put("q1_inf", q1Inf);

        // Q1 Hell
        DungeonQuest q1Hell = new DungeonQuest("q1_hell", "Q1 Hell", 65, 1.0, "QHell", 25);
        q1Hell.setLocationMessage("Find the Forgotten Circle and investigate");

        // Stage 1
        q1Hell.addLocationObjective(1, "world", -1221, -60, 686, -1221, -58, 707);
        q1Hell.addKillObjective(1, "flamecult_servant_hell", 25, "Flamecult Servants");
        q1Hell.addKillObjective(1, "flamecult_archer_hell", 25, "Flamecult Archers");
        q1Hell.setBossObjective(1, "raazghul_the_corruptor_hell", "Mini-Boss: Raazghul the Corruptor");
        q1Hell.addPortalObjective(1, "world", -1096, -61, 985, -1097, -59, 985);
        q1Hell.setStageEndCommand(1, "warp q1_m2_hell");

        // Stage 2
        q1Hell.setBossObjective(2, "perral_world_dragonknight_hell", "Mini-Boss: Perral World Dragonknight");
        q1Hell.addPortalObjective(2, "world", -1872, -61, 317, -1871, -59, 317);
        q1Hell.setStageEndCommand(2, "warp q1_m3_hell");

        // Stage 3
        q1Hell.setBossObjective(3, "grimmag_hell", "Final Boss: Grimmag");

        questData.put("q1_hell", q1Hell);

        // Q1 Blood
        DungeonQuest q1Blood = new DungeonQuest("q1_blood", "Q1 Bloodshed", 80, 1.5, "QBlood", 50);
        q1Blood.setLocationMessage("Find the Forgotten Circle and investigate");

        // Stage 1
        q1Blood.addLocationObjective(1, "world", -1221, -60, 1347, -1221, -58, 1369);
        q1Blood.addKillObjective(1, "flamecult_servant_blood", 25, "Flamecult Servants");
        q1Blood.addKillObjective(1, "flamecult_archer_blood", 25, "Flamecult Archers");
        q1Blood.setBossObjective(1, "raazghul_the_corruptor_blood", "Mini-Boss: Raazghul the Corruptor");
        q1Blood.addPortalObjective(1, "world", -1096, -61, 1647, -1097, -59, 1647);
        q1Blood.setStageEndCommand(1, "warp q1_m2_blood");

        // Stage 2
        q1Blood.setBossObjective(2, "perral_world_dragonknight_blood", "Mini-Boss: Perral World Dragonknight");
        q1Blood.addPortalObjective(2, "world", -1881, -61, 997, -1880, -59, 997);
        q1Blood.setStageEndCommand(2, "warp q1_m3_blood");

        // Stage 3
        q1Blood.setBossObjective(3, "grimmag_blood", "Final Boss: Grimmag");

        questData.put("q1_blood", q1Blood);
    }
    private static void initializeQ2() {
        // Q2 Infernal
        DungeonQuest q2Inf = new DungeonQuest("q2_inf", "Q2 Infernal", 50, 0.5, "Qinf", 10);

        // Poison area
        q2Inf.setPoisonArea("world", -1378, -60, -975, -1306, -37, -1064);

        // Boss and portal for Stage 1
        q2Inf.setBossObjective(1, "xerib_the_hunchback_inf", "Mini-Boss: Xerib the Hunchback");
        q2Inf.addPortalObjective(1, "world", -1317, -61, -1065, -1316, -59, -1066);
        q2Inf.setStageEndCommand(1, "warp q2_m2_inf");

        // Stage 2
        q2Inf.setBossObjective(2, "archus_the_mad_inf", "Mini-Boss: Archus the Mad");
        q2Inf.addPortalObjective(2, "world", -1145, -61, -852, -1146, -59, -851);
        q2Inf.setStageEndCommand(2, "warp q2_m3_inf");

        // Stage 3
        q2Inf.setBossObjective(3, "arachna_scourge_of_duria_inf", "Final Boss: Arachna, Scourge of Duria");

        questData.put("q2_inf", q2Inf);

        // Q2 Hell
        DungeonQuest q2Hell = new DungeonQuest("q2_hell", "Q2 Hell", 65, 1.0, "QHell", 25);

        // Poison area
        q2Hell.setPoisonArea("world", -1487, -60, -1588, -1416, -46, -1678);

        // Boss and portal for Stage 1
        q2Hell.setBossObjective(1, "xerib_the_hunchback_hell", "Mini-Boss: Xerib the Hunchback");
        q2Hell.addPortalObjective(1, "world", -1426, -61, -1678, -1425, -59, -1679);
        q2Hell.setStageEndCommand(1, "warp q2_m2_hell");

        // Stage 2
        q2Hell.setBossObjective(2, "archus_the_mad_hell", "Mini-Boss: Archus the Mad");
        q2Hell.addPortalObjective(2, "world", -1153, -61, -1508, -1154, -59, -1507);
        q2Hell.setStageEndCommand(2, "warp q2_m3_hell");

        // Stage 3
        q2Hell.setBossObjective(3, "arachna_scourge_of_duria_hell", "Final Boss: Arachna, Scourge of Duria");

        questData.put("q2_hell", q2Hell);

        // Q2 Blood
        DungeonQuest q2Blood = new DungeonQuest("q2_blood", "Q2 Bloodshed", 80, 1.5, "QBlood", 50);
        // Poison area
        q2Blood.setPoisonArea("world", -1487, -60, -2143, -1416, -46, -2233);

        // Boss and portal for Stage 1
        q2Blood.setBossObjective(1, "xerib_the_hunchback_blood", "Mini-Boss: Xerib the Hunchback");
        q2Blood.addPortalObjective(1, "world", -1426, -61, -2233, -1425, -59, -2234);
        q2Blood.setStageEndCommand(1, "warp q2_m2_blood");

        // Stage 2
        q2Blood.setBossObjective(2, "archus_the_mad_blood", "Mini-Boss: Archus the Mad");
        q2Blood.addPortalObjective(2, "world", -1155, -61, -2131, -1156, -59, -2130);
        q2Blood.setStageEndCommand(2, "warp q2_m3_blood");

        // Stage 3
        q2Blood.setBossObjective(3, "arachna_scourge_of_duria_blood", "Final Boss: Arachna, Scourge of Duria");

        questData.put("q2_blood", q2Blood);
    }
    private static void initializeQ3() {
        // Q3 Infernal
        DungeonQuest q3Inf = new DungeonQuest("q3_inf", "Q3 Infernal", 50, 0.5, "Qinf", 10);
        q3Inf.setLocationMessage("Find the evil miller's area and collect undead bones");

        // Stage 1 - Collect bones from undead mobs
        q3Inf.addLocationObjective(1, "world", -2477, -61, -304, -2476, -58, -299);
        q3Inf.addCollectObjective(1, "cursed_farmer_inf", 20, 75, "Undead Bones");
        q3Inf.addCollectObjective(1, "cursed_archer_inf", 20, 75, "Undead Bones");
        q3Inf.setStageMessage(1, "Evil miller has cast a protective spell on King Heredur's tomb. Collect 20 undead bones and grind them in the mill to dispel it!");

        // Stage 1 - Grind bones in mill
        q3Inf.addInteractObjective(1, Material.GRINDSTONE, "Grind the bones in the mill");

        // Stage 1 - Kill mini-boss
        q3Inf.setBossObjective(1, "parallel_world_evil_miller_inf", "Mini-Boss: Evil Miller");
        q3Inf.addPortalObjective(1, "world", -2451, -58, -275, -2452, -56, -274);
        q3Inf.setStageEndCommand(1, "warp q3_m2_inf");

        // Stage 2 - Collect emerald rune fragments
        q3Inf.addCollectObjective(2, "slain_assassin_inf", 1, 30, "Emerald Rune Fragment", true);
        q3Inf.setStageMessage(2, "Find the Emerald Rune fragments. Defeating assassins will grant you fragments.");

        // Stage 2 - Kill mini-boss
        q3Inf.setBossObjective(2, "the_bloody_arrow_inf", "Mini-Boss: The Bloody Arrow");
        q3Inf.setStageMessage(2, "The Bloody Arrow holds the second fragment of the Emerald Rune!");
        q3Inf.addPortalObjective(2, "world", -2411, -51, 45, -2410, -49, 46);
        q3Inf.setStageEndCommand(2, "warp q3_m3_inf");

        // Stage 3 - Kill final boss
        q3Inf.setBossObjective(3, "undead_parallel_world_king_heredur_inf", "Final Boss: Undead King Heredur");

        questData.put("q3_inf", q3Inf);

        // Q3 Hell
        DungeonQuest q3Hell = new DungeonQuest("q3_hell", "Q3 Hell", 65, 1.0, "QHell", 25);
        q3Hell.setLocationMessage("Find the evil miller's area and collect undead bones");

        // Stage 1 - Collect bones from undead mobs
        q3Hell.addLocationObjective(1, "world", -2856, -61, -304, -2855, -58, -299);
        q3Hell.addCollectObjective(1, "cursed_farmer_hell", 20, 75, "Undead Bones");
        q3Hell.addCollectObjective(1, "cursed_archer_hell", 20, 75, "Undead Bones");
        q3Hell.setStageMessage(1, "Evil miller has cast a protective spell on King Heredur's tomb. Collect 20 undead bones and grind them in the mill to dispel it!");

        // Stage 1 - Grind bones in mill
        q3Hell.addInteractObjective(1, Material.GRINDSTONE, "Grind the bones in the mill");

        // Stage 1 - Kill mini-boss
        q3Hell.setBossObjective(1, "parallel_world_evil_miller_hell", "Mini-Boss: Evil Miller");
        q3Hell.addPortalObjective(1, "world", -2831, -58, -274, -2832, -56, -273);
        q3Hell.setStageEndCommand(1, "warp q3_m2_hell");

        // Stage 2 - Collect emerald rune fragments
        q3Hell.addCollectObjective(2, "slain_assassin_hell", 1, 30, "Emerald Rune Fragment", true);
        q3Hell.setStageMessage(2, "Find the Emerald Rune fragments. Defeating assassins will grant you fragments.");

        // Stage 2 - Kill mini-boss
        q3Hell.setBossObjective(2, "the_bloody_arrow_hell", "Mini-Boss: The Bloody Arrow");
        q3Hell.setStageMessage(2, "The Bloody Arrow holds the second fragment of the Emerald Rune!");
        q3Hell.addPortalObjective(2, "world", -2782, -51, 45, -2781, -49, 46);
        q3Hell.setStageEndCommand(2, "warp q3_m3_hell");

        // Stage 3 - Kill final boss
        q3Hell.setBossObjective(3, "undead_parallel_world_king_heredur_hell", "Final Boss: Undead King Heredur");

        questData.put("q3_hell", q3Hell);

        // Q3 Blood
        DungeonQuest q3Blood = new DungeonQuest("q3_blood", "Q3 Bloodshed", 80, 1.5, "QBlood", 50);
        q3Blood.setLocationMessage("Find the evil miller's area and collect undead bones");

        // Stage 1 - Collect bones from undead mobs
        q3Blood.addLocationObjective(1, "world", -3245, -61, -299, -3244, -58, -294);
        q3Blood.addCollectObjective(1, "cursed_farmer_blood", 20, 75, "Undead Bones");
        q3Blood.addCollectObjective(1, "cursed_archer_blood", 20, 75, "Undead Bones");
        q3Blood.setStageMessage(1, "Evil miller has cast a protective spell on King Heredur's tomb. Collect 20 undead bones and grind them in the mill to dispel it!");

        // Stage 1 - Grind bones in mill
        q3Blood.addInteractObjective(1, Material.GRINDSTONE, "Grind the bones in the mill");

        // Stage 1 - Kill mini-boss
        q3Blood.setBossObjective(1, "parallel_world_evil_miller_blood", "Mini-Boss: Evil Miller");
        q3Blood.addPortalObjective(1, "world", -3222, -58, -268, -3223, -56, -267);
        q3Blood.setStageEndCommand(1, "warp q3_m2_blood");

        // Stage 2 - Collect emerald rune fragments
        q3Blood.addCollectObjective(2, "slain_assassin_blood", 1, 30, "Emerald Rune Fragment", true);
        q3Blood.setStageMessage(2, "Find the Emerald Rune fragments. Defeating assassins will grant you fragments.");

        // Stage 2 - Kill mini-boss
        q3Blood.setBossObjective(2, "the_bloody_arrow_blood", "Mini-Boss: The Bloody Arrow");
        q3Blood.setStageMessage(2, "The Bloody Arrow holds the second fragment of the Emerald Rune!");
        q3Blood.addPortalObjective(2, "world", -3173, -51, 51, -3172, -49, 52);
        q3Blood.setStageEndCommand(2, "warp q3_m3_blood");

        // Stage 3 - Kill final boss
        q3Blood.setBossObjective(3, "undead_parallel_world_king_heredur_blood", "Final Boss: Undead King Heredur");

        questData.put("q3_blood", q3Blood);
    }
    public static DungeonQuest getQuestData(String questId) {
        return questData.get(questId);
    }

    public static class DungeonQuest {
        private final String id;
        private final String name;
        private final int requiredLevel;
        private final double expReward;
        private final String itemReward;
        private final int requiredIPS;
        private String locationMessage = "Find the quest location";
        private LocationInfo poisonArea;
        private final Map<Integer, Map<String, KillObjective>> killObjectives = new HashMap<>();
        private final Map<Integer, BossObjective> bossObjectives = new HashMap<>();
        private final Map<Integer, LocationInfo> initialLocationObjectives = new HashMap<>();
        private final Map<Integer, LocationInfo> portalObjectives = new HashMap<>();
        private final Map<Integer, String> stageEndCommands = new HashMap<>();
        private final Map<Integer, Map<String, CollectObjective>> collectObjectives = new HashMap<>();
        private final Map<Integer, InteractObjective> interactObjectives = new HashMap<>();
        private final Map<Integer, String> stageMessages = new HashMap<>();

        public DungeonQuest(String id, String name, int requiredLevel, double expReward, String itemReward, int requiredIPS) {
            this.id = id;
            this.name = name;
            this.requiredLevel = requiredLevel;
            this.expReward = expReward;
            this.itemReward = itemReward;
            this.requiredIPS = requiredIPS;

            // Initialize empty kill objectives maps for each stage
            for (int i = 1; i <= 3; i++) {
                killObjectives.put(i, new HashMap<>());
                collectObjectives.put(i, new HashMap<>());
            }
        }
        public void addCollectObjective(int stage, String mobId, int count, int dropChance, String displayName) {
            collectObjectives.get(stage).put(mobId, new CollectObjective(mobId, count, dropChance, displayName, false));
        }

        public void addCollectObjective(int stage, String mobId, int count, int dropChance, String displayName, boolean progressive) {
            collectObjectives.get(stage).put(mobId, new CollectObjective(mobId, count, dropChance, displayName, progressive));
        }

        public void addInteractObjective(int stage, Material blockType, String displayName) {
            interactObjectives.put(stage, new InteractObjective(blockType, displayName));
        }

        public void setStageMessage(int stage, String message) {
            stageMessages.put(stage, message);
        }

        public String getStageMessage(int stage) {
            return stageMessages.getOrDefault(stage, "Complete the quest objectives");
        }

        public Map<String, CollectObjective> getCollectObjectives(int stage) {
            return collectObjectives.get(stage);
        }

        public InteractObjective getInteractObjective(int stage) {
            return interactObjectives.get(stage);
        }

        public boolean hasCollectObjectives(int stage) {
            return !collectObjectives.getOrDefault(stage, new HashMap<>()).isEmpty();
        }

        public boolean hasInteractObjective(int stage) {
            return interactObjectives.containsKey(stage);
        }
        public void setLocationMessage(String message) {
            this.locationMessage = message;
        }

        public String getLocationMessage() {
            return locationMessage;
        }

        public void addKillObjective(int stage, String mobId, int count, String displayName) {
            killObjectives.get(stage).put(mobId, new KillObjective(mobId, count, displayName));
        }

        public void setBossObjective(int stage, String bossId, String displayName) {
            bossObjectives.put(stage, new BossObjective(bossId, displayName));
        }

        public void addLocationObjective(int stage, String world, int x1, int y1, int z1, int x2, int y2, int z2) {
            initialLocationObjectives.put(stage, new LocationInfo(world, x1, y1, z1, x2, y2, z2));
        }

        public void addPortalObjective(int stage, String world, int x1, int y1, int z1, int x2, int y2, int z2) {
            portalObjectives.put(stage, new LocationInfo(world, x1, y1, z1, x2, y2, z2));
        }

        public void setStageEndCommand(int stage, String command) {
            stageEndCommands.put(stage, command);
        }
        public void setPoisonArea(String world, int x1, int y1, int z1, int x2, int y2, int z2) {
            this.poisonArea = new LocationInfo(world, x1, y1, z1, x2, y2, z2);
        }

        public LocationInfo getPoisonArea() {
            return poisonArea;
        }

        // Getters
        public String getId() { return id; }
        public String getName() { return name; }
        public int getRequiredLevel() { return requiredLevel; }
        public double getExpReward() { return expReward; }
        public String getItemReward() { return itemReward; }
        public int getRequiredIPS() { return requiredIPS; }

        public Map<String, Integer> getKillObjectives(int stage) {
            Map<String, Integer> result = new HashMap<>();
            for (KillObjective obj : killObjectives.get(stage).values()) {
                result.put(obj.getMobId(), obj.getCount());
            }
            return result;
        }

        public Map<String, KillObjective> getKillObjectiveDetails(int stage) {
            return killObjectives.get(stage);
        }

        public String getBossObjective(int stage) {
            BossObjective boss = bossObjectives.get(stage);
            return boss != null ? boss.getBossId() : null;
        }

        public BossObjective getBossObjectiveDetails(int stage) {
            return bossObjectives.get(stage);
        }

        public LocationInfo getLocationObjective(int stage) {
            return initialLocationObjectives.get(stage);
        }

        public LocationInfo getPortalObjective(int stage) {
            return portalObjectives.get(stage);
        }

        public String getStageEndCommand(int stage) {
            return stageEndCommands.get(stage);
        }

        public boolean hasStage(int stage) {
            return bossObjectives.containsKey(stage) || !killObjectives.getOrDefault(stage, new HashMap<>()).isEmpty();
        }

        public String getFormattedRewardName() {
            if (itemReward == null || itemReward.isEmpty()) {
                return "No Item";
            }

            if (itemReward.equals("Qinf")) {
                return "Infernal Item Chest";
            } else if (itemReward.equals("QHell")) {
                return "Hell Item Chest";
            } else if (itemReward.equals("QBlood")) {
                return "Blood Item Chest";
            } else {
                return itemReward;
            }
        }
    }

    public static class KillObjective {
        private final String mobId;
        private final int count;
        private final String displayName;

        public KillObjective(String mobId, int count, String displayName) {
            this.mobId = mobId;
            this.count = count;
            this.displayName = displayName;
        }

        public String getMobId() { return mobId; }
        public int getCount() { return count; }
        public String getDisplayName() { return displayName; }

        public String getObjectiveText() {
            return "Defeat " + count + " " + displayName;
        }
    }

    public static class BossObjective {
        private final String bossId;
        private final String displayName;

        public BossObjective(String bossId, String displayName) {
            this.bossId = bossId;
            this.displayName = displayName;
        }

        public String getBossId() { return bossId; }
        public String getDisplayName() { return displayName; }

        public String getObjectiveText() {
            return "Defeat " + displayName;
        }
    }

    public static class LocationInfo {
        private final String world;
        private final int x1, y1, z1, x2, y2, z2;

        public LocationInfo(String world, int x1, int y1, int z1, int x2, int y2, int z2) {
            this.world = world;
            this.x1 = x1;
            this.y1 = y1;
            this.z1 = z1;
            this.x2 = x2;
            this.y2 = y2;
            this.z2 = z2;
        }

        public String getWorld() { return world; }
        public int getX1() { return x1; }
        public int getY1() { return y1; }
        public int getZ1() { return z1; }
        public int getX2() { return x2; }
        public int getY2() { return y2; }
        public int getZ2() { return z2; }

        public boolean isInside(Location loc) {
            if (!loc.getWorld().getName().equals(world)) {
                return false;
            }

            int minX = Math.min(x1, x2);
            int maxX = Math.max(x1, x2);
            int minY = Math.min(y1, y2);
            int maxY = Math.max(y1, y2);
            int minZ = Math.min(z1, z2);
            int maxZ = Math.max(z1, z2);

            // Add a small buffer (2 blocks) for better detection
            return loc.getBlockX() >= minX - 2 && loc.getBlockX() <= maxX + 2 &&
                    loc.getBlockY() >= minY - 2 && loc.getBlockY() <= maxY + 2 &&
                    loc.getBlockZ() >= minZ - 2 && loc.getBlockZ() <= maxZ + 2;
        }
    }
    public static class CollectObjective {
        private final String mobId;
        private final int count;
        private final int dropChance; // percentage (0-100)
        private final String displayName;
        private final boolean progressive;

        public CollectObjective(String mobId, int count, int dropChance, String displayName, boolean progressive) {
            this.mobId = mobId;
            this.count = count;
            this.dropChance = dropChance;
            this.displayName = displayName;
            this.progressive = progressive;
        }

        public String getMobId() { return mobId; }
        public int getCount() { return count; }
        public int getDropChance() { return dropChance; }
        public String getDisplayName() { return displayName; }
        public boolean isProgressive() { return progressive; }

        public String getObjectiveText() {
            return "Collect " + count + " " + displayName + " (Drop chance: " + dropChance + "%)";
        }
    }

    public static class InteractObjective {
        private final Material blockType;
        private final String displayName;

        public InteractObjective(Material blockType, String displayName) {
            this.blockType = blockType;
            this.displayName = displayName;
        }

        public Material getBlockType() { return blockType; }
        public String getDisplayName() { return displayName; }

        public String getObjectiveText() {
            return displayName;
        }
    }
}