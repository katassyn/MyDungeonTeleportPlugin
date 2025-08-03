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
        initializeQ4();
        initializeQ5();
        initializeQ6();
        initializeQ7();
        initializeQ8();
        initializeQ9();
        initializeQ10();
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
        DungeonQuest q1Hell = new DungeonQuest("q1_hell", "Q1 Hell", 65, 1.0, "Qhell", 25);
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
        DungeonQuest q1Blood = new DungeonQuest("q1_blood", "Q1 Bloodshed", 80, 1.5, "Qblood", 50);
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
        DungeonQuest q2Hell = new DungeonQuest("q2_hell", "Q2 Hell", 65, 1.0, "Qhell", 25);

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
        DungeonQuest q2Blood = new DungeonQuest("q2_blood", "Q2 Bloodshed", 80, 1.5, "Qblood", 50);
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
        DungeonQuest q3Hell = new DungeonQuest("q3_hell", "Q3 Hell", 65, 1.0, "Qhell", 25);
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
        DungeonQuest q3Blood = new DungeonQuest("q3_blood", "Q3 Bloodshed", 80, 1.5, "Qblood", 50);
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
    private static void initializeQ4() {
        // Q4 Infernal
        DungeonQuest q4Inf = new DungeonQuest("q4_inf", "Q4 Infernal", 50, 0.5, "Qinf", 10);

        // Stage 1 - Start directly with kill objectives
        q4Inf.addKillObjective(1, "sanguine_clan_raging_snooper_inf", 5, "Sanguine Clan Snooper");
        q4Inf.addKillObjective(1, "sanguine_clan_raging_hunter_inf", 5, "Sanguine Clan Hunter");
        q4Inf.setBossObjective(1, "parallel_world_ancient_stag_inf", "Mini-Boss: Ancient Stag");
        q4Inf.addPortalObjective(1, "world", -3143, -61, -732, -3142, -59, -722);
        q4Inf.setStageEndCommand(1, "warp q4_m2_inf");

        // Stage 2
        q4Inf.addKillObjective(2, "eternal_root_creature_inf", 5, "Eternal Root Creature");
        q4Inf.addKillObjective(2, "eternal_hunter_inf", 5, "Eternal Hunter");
        q4Inf.setBossObjective(2, "ulgar_the_master_butcher_phase3_inf", "Mini-Boss: Ulgar the Master Butcher");
        q4Inf.addPortalObjective(2, "world", -3426, -61, -819, -3427, -59, -820);
        q4Inf.setStageEndCommand(2, "warp q4_m3_inf");

        // Stage 3
        q4Inf.setBossObjective(3, "bearach_champion_of_wilds_inf", "Final Boss: Bearach, Champion of Wilds");

        questData.put("q4_inf", q4Inf);

        // Q4 Hell
        DungeonQuest q4Hell = new DungeonQuest("q4_hell", "Q4 Hell", 65, 1.0, "Qhell", 25);

        // Stage 1 - Start directly with kill objectives
        q4Hell.addKillObjective(1, "sanguine_clan_raging_snooper_hell", 5, "Sanguine Clan Snooper");
        q4Hell.addKillObjective(1, "sanguine_clan_raging_hunter_hell", 5, "Sanguine Clan Hunter");
        q4Hell.setBossObjective(1, "parallel_world_ancient_stag_hell", "Mini-Boss: Ancient Stag");
        q4Hell.addPortalObjective(1, "world", -2721, -61, -730, -2720, -59, -729);
        q4Hell.setStageEndCommand(1, "warp q4_m2_hell");

        // Stage 2
        q4Hell.addKillObjective(2, "eternal_root_creature_hell", 5, "Eternal Root Creature");
        q4Hell.addKillObjective(2, "eternal_hunter_hell", 5, "Eternal Hunter");
        q4Hell.setBossObjective(2, "ulgar_the_master_butcher_phase3_hell", "Mini-Boss: Ulgar the Master Butcher");
        q4Hell.addPortalObjective(2, "world", -3004, -61, -826, -3005, -59, -827);
        q4Hell.setStageEndCommand(2, "warp q4_m3_hell");

        // Stage 3
        q4Hell.setBossObjective(3, "bearach_champion_of_wilds_hell", "Final Boss: Bearach, Champion of Wilds");

        questData.put("q4_hell", q4Hell);

        // Q4 Blood
        DungeonQuest q4Blood = new DungeonQuest("q4_blood", "Q4 Bloodshed", 80, 1.5, "Qblood", 50);

        // Stage 1 - Start directly with kill objectives
        q4Blood.addKillObjective(1, "sanguine_clan_raging_snooper_blood", 5, "Sanguine Clan Snooper");
        q4Blood.addKillObjective(1, "sanguine_clan_raging_hunter_blood", 5, "Sanguine Clan Hunter");
        q4Blood.setBossObjective(1, "parallel_world_ancient_stag_blood", "Mini-Boss: Ancient Stag");
        q4Blood.addPortalObjective(1, "world", -2324, -61, -731, -2323, -59, -730);
        q4Blood.setStageEndCommand(1, "warp q4_m2_blood");

        // Stage 2
        q4Blood.addKillObjective(2, "eternal_root_creature_blood", 5, "Eternal Root Creature");
        q4Blood.addKillObjective(2, "eternal_hunter_blood", 5, "Eternal Hunter");
        q4Blood.setBossObjective(2, "ulgar_the_master_butcher_phase3_blood", "Mini-Boss: Ulgar the Master Butcher");
        q4Blood.addPortalObjective(2, "world", -2607, -61, -827, -2608, -59, -828);
        q4Blood.setStageEndCommand(2, "warp q4_m3_blood");

        // Stage 3
        q4Blood.setBossObjective(3, "bearach_champion_of_wilds_blood", "Final Boss: Bearach, Champion of Wilds");

        questData.put("q4_blood", q4Blood);
    }
    private static void initializeQ5() {
        // Q5 Infernal
        DungeonQuest q5Inf = new DungeonQuest("q5_inf", "Q5 Infernal", 50, 0.5, "Qinf", 10);
        q5Inf.setLocationMessage("Find the cursed area and investigate");

        // Stage 1
        q5Inf.addLocationObjective(1, "world", -3490, -61, -100, -3480, -58, -90); // Approximate starting area
        q5Inf.addKillObjective(1, "furious_andermagic_inf", 1, "Furious Andermagic");
        q5Inf.setBossObjective(1, "parallel_world_old_jabbax_shaman_inf", "Mini-Boss: Old Jabbax Shaman");
        q5Inf.addPortalObjective(1, "world", -3491, -61, -73, -3492, -59, -72);
        q5Inf.setStageEndCommand(1, "warp q5_m2_inf");

        // Stage 2
        q5Inf.setBossObjective(2, "wandering_experiment_inf", "Mini-Boss: Wandering Experiment");
        q5Inf.addPortalObjective(2, "world", -3511, -61, 208, -3512, -59, 209);
        q5Inf.setStageEndCommand(2, "warp q5_m3_inf");

        // Stage 3
        q5Inf.setBossObjective(3, "khalys_leader_of_cultists_inf", "Final Boss: Khalys, Leader of Cultists");

        questData.put("q5_inf", q5Inf);

        // Q5 Hell
        DungeonQuest q5Hell = new DungeonQuest("q5_hell", "Q5 Hell", 65, 1.0, "Qhell", 25);
        q5Hell.setLocationMessage("Find the cursed area and investigate");

        // Stage 1
        q5Hell.addLocationObjective(1, "world", -3485, -61, 850, -3475, -58, 860); // Approximate starting area
        q5Hell.addKillObjective(1, "furious_andermagic_hell", 1, "Furious Andermagic");
        q5Hell.setBossObjective(1, "parallel_world_old_jabbax_shaman_hell", "Mini-Boss: Old Jabbax Shaman");
        q5Hell.addPortalObjective(1, "world", -3487, -61, 874, -3488, -59, 875);
        q5Hell.setStageEndCommand(1, "warp q5_m2_hell");

        // Stage 2
        q5Hell.setBossObjective(2, "wandering_experiment_hell", "Mini-Boss: Wandering Experiment");
        q5Hell.addPortalObjective(2, "world", -3507, -61, 1155, -3508, -59, 1156);
        q5Hell.setStageEndCommand(2, "warp q5_m3_hell");

        // Stage 3
        q5Hell.setBossObjective(3, "khalys_leader_of_cultists_hell", "Final Boss: Khalys, Leader of Cultists");

        questData.put("q5_hell", q5Hell);

        // Q5 Blood
        DungeonQuest q5Blood = new DungeonQuest("q5_blood", "Q5 Bloodshed", 80, 1.5, "Qblood", 50);
        q5Blood.setLocationMessage("Find the cursed area and investigate");

        // Stage 1
        q5Blood.addLocationObjective(1, "world", -3460, -61, 1780, -3450, -58, 1790); // Approximate starting area
        q5Blood.addKillObjective(1, "furious_andermagic_blood", 1, "Furious Andermagic");
        q5Blood.setBossObjective(1, "parallel_world_old_jabbax_shaman_blood", "Mini-Boss: Old Jabbax Shaman");
        q5Blood.addPortalObjective(1, "world", -3462, -61, 1791, -3463, -59, 1792);
        q5Blood.setStageEndCommand(1, "warp q5_m2_blood");

        // Stage 2
        q5Blood.setBossObjective(2, "wandering_experiment_blood", "Mini-Boss: Wandering Experiment");
        q5Blood.addPortalObjective(2, "world", -3482, -61, 2072, -3483, -59, 2073);
        q5Blood.setStageEndCommand(2, "warp q5_m3_blood");

        // Stage 3
        q5Blood.setBossObjective(3, "khalys_leader_of_cultists_blood", "Final Boss: Khalys, Leader of Cultists");

        questData.put("q5_blood", q5Blood);
    }
    private static void initializeQ6() {
        // Q6 Infernal
        DungeonQuest q6Inf = new DungeonQuest("q6_inf", "Q6 Infernal", 50, 0.5, "Qinf", 10);
        q6Inf.setLocationMessage("Find the fallen warriors' stronghold");

        // Stage 1 - Kill mobs
        q6Inf.addKillObjective(1, "fallen_warrior_inf", 10, "Fallen Warriors");
        q6Inf.addKillObjective(1, "fallen_archer_inf", 10, "Fallen Archers");
        q6Inf.addKillObjective(1, "death_knight_inf", 3, "Death Knights");
        q6Inf.setBossObjective(1, "mortis_death_knight_inf", "Mini-Boss: Mortis Death Knight");
        q6Inf.addPortalObjective(1, "world", -4490, -61, -857, -4491, -59, -858);
        q6Inf.setStageEndCommand(1, "warp q6_m2_inf");

        // Stage 2 - Collect dagger parts
        q6Inf.setStageMessage(2, "Find the pieces of the Sacrificial Dagger");
        q6Inf.addCollectObjective(2, "elite_skeleton_archer_inf", 1, 30, "First Part of Sacrificial Dagger", false, 20);
        q6Inf.addCollectObjective(2, "elite_skeleton_warrior_inf", 1, 30, "Second Part of Sacrificial Dagger", false, 20);
        q6Inf.addCollectObjective(2, "death_archer_inf", 1, 30, "Third Part of Sacrificial Dagger", true);
        q6Inf.setBossObjective(2, "murot_high_priest_inf", "Mini-Boss: Murot High Priest");
        q6Inf.addPortalObjective(2, "world", -4598, -61, -1169, -4599, -59, -1170);
        q6Inf.setStageEndCommand(2, "warp q6_m3_inf");

        // Stage 3 - Kill final boss
        q6Inf.setBossObjective(3, "mortis_phase3_inf", "Final Boss: Mortis, Unchained God of Death");

        questData.put("q6_inf", q6Inf);

        // Q6 Hell
        DungeonQuest q6Hell = new DungeonQuest("q6_hell", "Q6 Hell", 65, 1.0, "Qhell", 25);
        q6Hell.setLocationMessage("Find the fallen warriors' stronghold");

        // Stage 1 - Kill mobs
        q6Hell.addKillObjective(1, "fallen_warrior_hell", 10, "Fallen Warriors");
        q6Hell.addKillObjective(1, "fallen_archer_hell", 10, "Fallen Archers");
        q6Hell.addKillObjective(1, "death_knight_hell", 3, "Death Knights");
        q6Hell.setBossObjective(1, "mortis_death_knight_hell", "Mini-Boss: Mortis Death Knight");
        q6Hell.addPortalObjective(1, "world", -4783, -61, -1866, -4784, -59, -1867);
        q6Hell.setStageEndCommand(1, "warp q6_m2_hell");

        // Stage 2 - Collect dagger parts
        q6Hell.setStageMessage(2, "Find the pieces of the Sacrificial Dagger");
        q6Hell.addCollectObjective(2, "elite_skeleton_archer_hell", 1, 30, "First Part of Sacrificial Dagger", false, 20);
        q6Hell.addCollectObjective(2, "elite_skeleton_warrior_hell", 1, 30, "Second Part of Sacrificial Dagger", false, 20);
        q6Hell.addCollectObjective(2, "death_archer_hell", 1, 30, "Third Part of Sacrificial Dagger", true);
        q6Hell.setBossObjective(2, "murot_high_priest_hell", "Mini-Boss: Murot High Priest");
        q6Hell.addPortalObjective(2, "world", -4891, -61, -2178, -4892, -59, -2179);
        q6Hell.setStageEndCommand(2, "warp q6_m3_hell");

        // Stage 3 - Kill final boss
        q6Hell.setBossObjective(3, "mortis_phase3_hell", "Final Boss: Mortis, Unchained God of Death");

        questData.put("q6_hell", q6Hell);

        // Q6 Blood
        DungeonQuest q6Blood = new DungeonQuest("q6_blood", "Q6 Bloodshed", 80, 1.5, "Qblood", 50);
        q6Blood.setLocationMessage("Find the fallen warriors' stronghold");

        // Stage 1 - Kill mobs
        q6Blood.addKillObjective(1, "fallen_warrior_blood", 10, "Fallen Warriors");
        q6Blood.addKillObjective(1, "fallen_archer_blood", 10, "Fallen Archers");
        q6Blood.addKillObjective(1, "death_knight_blood", 3, "Death Knights");
        q6Blood.setBossObjective(1, "mortis_death_knight_blood", "Mini-Boss: Mortis Death Knight");
        q6Blood.addPortalObjective(1, "world", -5076, -61, -2875, -5077, -59, -2876);
        q6Blood.setStageEndCommand(1, "warp q6_m2_blood");

        // Stage 2 - Collect dagger parts
        q6Blood.setStageMessage(2, "Find the pieces of the Sacrificial Dagger");
        q6Blood.addCollectObjective(2, "elite_skeleton_archer_blood", 1, 30, "First Part of Sacrificial Dagger", false, 20);
        q6Blood.addCollectObjective(2, "elite_skeleton_warrior_blood", 1, 30, "Second Part of Sacrificial Dagger", false, 20);
        q6Blood.addCollectObjective(2, "death_archer_blood", 1, 30, "Third Part of Sacrificial Dagger", true);
        q6Blood.setBossObjective(2, "murot_high_priest_blood", "Mini-Boss: Murot High Priest");
        q6Blood.addPortalObjective(2, "world", -5184, -61, -3187, -5185, -59, -3188);
        q6Blood.setStageEndCommand(2, "warp q6_m3_blood");

        // Stage 3 - Kill final boss
        q6Blood.setBossObjective(3, "mortis_phase3_blood", "Final Boss: Mortis, Unchained God of Death");

        questData.put("q6_blood", q6Blood);
    }

    private static void initializeQ7() {
        // Q7 Infernal
        DungeonQuest q7Inf = new DungeonQuest("q7_inf", "Q7 Infernal", 50, 0.5, "Qinf", 10);
        q7Inf.setLocationMessage("Find the ancient fortress and collect catapult ammunition");

        // Stage 1 - Collect catapult balls
        q7Inf.addCollectObjective(1, "b1000_combat_mechanoid_inf", 2, 50, "Catapult Ball", false);
        q7Inf.setStageMessage(1, "Collect 2 Catapult Balls from Combat Mechanoids to prepare the attack");

        // Stage 1 - Interact with levers (catapults)
        q7Inf.addInteractObjective(1, Material.LEVER, "Fire the catapults (use TWO different levers)");

        // Stage 1 - Kill mini-boss
        q7Inf.setBossObjective(1, "iron_creeper_gate_guard_inf", "Mini-Boss: Iron Creeper Gate Guard");
        q7Inf.addPortalObjective(1, "world", -3765, -54, -2262, -3766, -52, -2263);
        q7Inf.setStageEndCommand(1, "warp q7_m2_inf");

        // Stage 2 - Altar 1 area and mob killing
        q7Inf.addLocationObjective(2, "world", -3679, -61, -920, -3679, -61, -920, 10); // 10 block radius
        q7Inf.setStageMessage(2, "Find the first altar to summon enemies");

        // Stage 2 - Altar 2 area
        q7Inf.addLocationObjective(2, "world", -3759, -61, -881, -3759, -61, -881, 10); // 10 block radius

        // Stage 2 - Mini-boss and portal
        q7Inf.setBossObjective(2, "commander_embersword_inf", "Mini-Boss: Commander Embersword");
        q7Inf.addPortalObjective(2, "world", -3751, -61, -864, -3752, -59, -863);
        q7Inf.setStageEndCommand(2, "warp q7_m3_inf");

        // Stage 3 - Kill final boss
        q7Inf.setBossObjective(3, "herald_of_anderworld_inf", "Final Boss: Herald of Anderworld");

        questData.put("q7_inf", q7Inf);

        // Q7 Hell
        DungeonQuest q7Hell = new DungeonQuest("q7_hell", "Q7 Hell", 65, 1.0, "Qhell", 25);
        q7Hell.setLocationMessage("Find the ancient fortress and collect catapult ammunition");

        // Stage 1 - Collect catapult balls
        q7Hell.addCollectObjective(1, "b1000_combat_mechanoid_hell", 2, 50, "Catapult Ball", false);
        q7Hell.setStageMessage(1, "Collect 2 Catapult Balls from Combat Mechanoids to prepare the attack");

        // Stage 1 - Interact with levers (catapults)
        q7Hell.addInteractObjective(1, Material.LEVER, "Fire the catapults (use TWO different levers)");

        // Stage 1 - Kill mini-boss
        q7Hell.setBossObjective(1, "iron_creeper_gate_guard_hell", "Mini-Boss: Iron Creeper Gate Guard");
        q7Hell.addPortalObjective(1, "world", -3743, -54, -1494, -3744, -52, -1495);
        q7Hell.setStageEndCommand(1, "warp q7_m2_hell");

        // Stage 2 - Altar 1 area and mob killing
        q7Hell.addLocationObjective(2, "world", -3702, -61, -1693, -3702, -61, -1693, 10); // 10 block radius
        q7Hell.setStageMessage(2, "Find the first altar to summon enemies");

        // Stage 2 - Altar 2 area
        q7Hell.addLocationObjective(2, "world", -3782, -61, -1652, -3782, -61, -1652, 10); // 10 block radius

        // Stage 2 - Mini-boss and portal
        q7Hell.setBossObjective(2, "commander_embersword_hell", "Mini-Boss: Commander Embersword");
        q7Hell.addPortalObjective(2, "world", -3774, -61, -1635, -3775, -59, -1634);
        q7Hell.setStageEndCommand(2, "warp q7_m3_hell");

        // Stage 3 - Kill final boss
        q7Hell.setBossObjective(3, "herald_of_anderworld_hell", "Final Boss: Herald of Anderworld");

        questData.put("q7_hell", q7Hell);

        // Q7 Blood
        DungeonQuest q7Blood = new DungeonQuest("q7_blood", "Q7 Bloodshed", 80, 1.5, "Qblood", 50);
        q7Blood.setLocationMessage("Find the ancient fortress and collect catapult ammunition");

        // Stage 1 - Collect catapult balls
        q7Blood.addCollectObjective(1, "b1000_combat_mechanoid_blood", 2, 50, "Catapult Ball", false);
        q7Blood.setStageMessage(1, "Collect 2 Catapult Balls from Combat Mechanoids to prepare the attack");

        // Stage 1 - Interact with levers (catapults)
        q7Blood.addInteractObjective(1, Material.LEVER, "Fire the catapults (use TWO different levers)");

        // Stage 1 - Kill mini-boss
        q7Blood.setBossObjective(1, "iron_creeper_gate_guard_blood", "Mini-Boss: Iron Creeper Gate Guard");
        q7Blood.addPortalObjective(1, "world", -3720, -54, -723, -3721, -52, -724);
        q7Blood.setStageEndCommand(1, "warp q7_m2_blood");

        // Stage 2 - Altar 1 area and mob killing
        q7Blood.addLocationObjective(2, "world", -3724, -61, -2462, -3724, -61, -2462, 10); // 10 block radius
        q7Blood.setStageMessage(2, "Find the first altar to summon enemies");

        // Stage 2 - Altar 2 area
        q7Blood.addLocationObjective(2, "world", -3804, -61, -2418, -3804, -61, -2418, 10); // 10 block radius

        // Stage 2 - Mini-boss and portal
        q7Blood.setBossObjective(2, "commander_embersword_blood", "Mini-Boss: Commander Embersword");
        q7Blood.addPortalObjective(2, "world", -3796, -61, -2403, -3797, -59, -2402);
        q7Blood.setStageEndCommand(2, "warp q7_m3_blood");

        // Stage 3 - Kill final boss
        q7Blood.setBossObjective(3, "herald_of_anderworld_blood", "Final Boss: Herald of Anderworld");

        questData.put("q7_blood", q7Blood);
    }

    private static void initializeQ8() {
        // Q8 Infernal
        DungeonQuest q8Inf = new DungeonQuest("q8_inf", "Q8 Infernal", 50, 0.5, "Qinf", 10);
        q8Inf.setLocationMessage("Find the electrified area and collect electrical shards");

        // Stage 1 - Collect electrical shards
        q8Inf.addCollectObjective(1, "electrified_ferocity_inf", 5, 75, "Electrical Shard");
        q8Inf.setStageMessage(1, "Defeat electrified creatures to collect 5 Electrical Shards!");

        // Stage 1 - Interact with chiseled deepslate
        q8Inf.addInteractObjective(1, Material.CHISELED_DEEPSLATE, "Channel the electrical energy into chiseled deepslate");

        // Stage 1 - Kill mini-boss
        q8Inf.setBossObjective(1, "shocking_forocity_inf", "Mini-Boss: Shocking Forocity");
        q8Inf.addPortalObjective(1, "world", -3758, -61, -315, -3757, -59, -316);
        q8Inf.setStageEndCommand(1, "warp q8_m2_inf");

        // Stage 2 - Kill mini-boss
        q8Inf.setBossObjective(2, "pale_enforcer_inf", "Mini-Boss: Pale Enforcer");
        q8Inf.addPortalObjective(2, "world", -3730, -61, -45, -3729, -59, -46);
        q8Inf.setStageEndCommand(2, "warp q8_m3_inf");

        // Stage 3 - Kill final boss
        q8Inf.setBossObjective(3, "sigrismarr_priest_of_fjalnir_inf", "Final Boss: Sigrismarr, Priest of Fjalnir");

        questData.put("q8_inf", q8Inf);

        // Q8 Hell
        DungeonQuest q8Hell = new DungeonQuest("q8_hell", "Q8 Hell", 65, 1.0, "Qhell", 25);
        q8Hell.setLocationMessage("Find the electrified area and collect electrical shards");

        // Stage 1 - Collect electrical shards
        q8Hell.addCollectObjective(1, "electrified_ferocity_hell", 5, 75, "Electrical Shard");
        q8Hell.setStageMessage(1, "Defeat electrified creatures to collect 5 Electrical Shards!");

        // Stage 1 - Interact with chiseled deepslate
        q8Hell.addInteractObjective(1, Material.CHISELED_DEEPSLATE, "Channel the electrical energy into chiseled deepslate");

        // Stage 1 - Kill mini-boss
        q8Hell.setBossObjective(1, "shocking_forocity_hell", "Mini-Boss: Shocking Forocity");
        q8Hell.addPortalObjective(1, "world", -3756, -61, 1455, -3755, -59, 1454);
        q8Hell.setStageEndCommand(1, "warp q8_m2_hell");

        // Stage 2 - Kill mini-boss
        q8Hell.setBossObjective(2, "pale_enforcer_hell", "Mini-Boss: Pale Enforcer");
        q8Hell.addPortalObjective(2, "world", -3728, -61, 1725, -3727, -59, 1724);
        q8Hell.setStageEndCommand(2, "warp q8_m3_hell");

        // Stage 3 - Kill final boss
        q8Hell.setBossObjective(3, "sigrismarr_priest_of_fjalnir_hell", "Final Boss: Sigrismarr, Priest of Fjalnir");

        questData.put("q8_hell", q8Hell);

        // Q8 Bloodshed
        DungeonQuest q8Blood = new DungeonQuest("q8_blood", "Q8 Bloodshed", 80, 1.5, "Qblood", 50);
        q8Blood.setLocationMessage("Find the electrified area and collect electrical shards");

        // Stage 1 - Collect electrical shards
        q8Blood.addCollectObjective(1, "electrified_ferocity_blood", 5, 75, "Electrical Shard");
        q8Blood.setStageMessage(1, "Defeat electrified creatures to collect 5 Electrical Shards!");

        // Stage 1 - Interact with chiseled deepslate
        q8Blood.addInteractObjective(1, Material.CHISELED_DEEPSLATE, "Channel the electrical energy into chiseled deepslate");

        // Stage 1 - Kill mini-boss
        q8Blood.setBossObjective(1, "shocking_forocity_blood", "Mini-Boss: Shocking Forocity");
        q8Blood.addPortalObjective(1, "world", -3757, -61, 570, -3756, -59, 569);
        q8Blood.setStageEndCommand(1, "warp q8_m2_blood");

        // Stage 2 - Kill mini-boss
        q8Blood.setBossObjective(2, "pale_enforcer_blood", "Mini-Boss: Pale Enforcer");
        q8Blood.addPortalObjective(2, "world", -3729, -61, 840, -3728, -59, 839);
        q8Blood.setStageEndCommand(2, "warp q8_m3_blood");

        // Stage 3 - Kill final boss
        q8Blood.setBossObjective(3, "sigrismarr_priest_of_fjalnir_blood", "Final Boss: Sigrismarr, Priest of Fjalnir");

        questData.put("q8_blood", q8Blood);
    }

    private static void initializeQ9() {
        // Q9 Infernal
        DungeonQuest q9Inf = new DungeonQuest("q9_inf", "Q9 Infernal", 50, 0.5, "Qinf", 10);
        q9Inf.setLocationMessage("Find and collect statue fragments");

        // Stage 1 - Collect statue fragments (4 random from 8 possible locations)
        q9Inf.addStatueLocations(1, new int[][] {
                {-3889, -60, -560}, {-3833, -60, -583}, {-3888, -60, -635}, {-3848, -60, -700},
                {-3895, -60, -721}, {-3965, -60, -684}, {-3939, -60, -651}, {-3963, -60, -602}
        });
        q9Inf.setRequiredStatues(1, 4); // Collect 4 fragments
        q9Inf.setStageMessage(1, "Find and interact with 4 ancient statues to collect their fragments");

        // Stage 1 - Kill mini-boss
        q9Inf.setBossObjective(1, "asterion_inf", "Mini-Boss: Asterion");
        q9Inf.addPortalObjective(1, "world", -4015, -61, -645, -4016, -59, -646);
        q9Inf.setStageEndCommand(1, "warp q9_m2_inf");

        // Stage 2 - Activate 5 metronomes
        q9Inf.setStageMessage(2, "Find and activate 5 ancient metronomes");
        q9Inf.setRequiredAltars(2, 5);

        // Stage 2 - Kill mini-boss
        q9Inf.setBossObjective(2, "ebicarus_inf", "Mini-Boss: Ebicarus");
        q9Inf.addPortalObjective(2, "world", -3948, -61, -1007, -3947, -59, -1006);
        q9Inf.setStageEndCommand(2, "warp q9_m3_inf");

        // Stage 3 - Kill final boss
        q9Inf.setBossObjective(3, "medusa_inf", "Final Boss: Medusa");

        questData.put("q9_inf", q9Inf);

        // Q9 Hell
        DungeonQuest q9Hell = new DungeonQuest("q9_hell", "Q9 Hell", 65, 1.0, "Qhell", 25);
        q9Hell.setLocationMessage("Find and collect statue fragments");

        // Stage 1 - Collect statue fragments
        q9Hell.addStatueLocations(1, new int[][] {
                {-4032, -60, -1325}, {-3986, -60, -1348}, {-4031, -60, -1400}, {-3991, -60, -1465},
                {-4038, -60, -1486}, {-4082, -60, -1416}, {-4108, -60, -1449}, {-4106, -60, -1367},
                {-3976, -60, -1348}
        });
        q9Hell.setRequiredStatues(1, 4);
        q9Hell.setStageMessage(1, "Find and interact with 4 ancient statues to collect their fragments");

        // Stage 1 - Kill mini-boss
        q9Hell.setBossObjective(1, "asterion_hell", "Mini-Boss: Asterion");
        q9Hell.addPortalObjective(1, "world", -4158, -61, -1410, -4159, -59, -1411);
        q9Hell.setStageEndCommand(1, "warp q9_m2_hell");

        // Stage 2 - Activate 5 metronomes
        q9Hell.setStageMessage(2, "Find and activate 5 ancient metronomes");
        q9Hell.setRequiredAltars(2, 5);

        // Stage 2 - Kill mini-boss
        q9Hell.setBossObjective(2, "ebicarus_hell", "Mini-Boss: Ebicarus");
        q9Hell.addPortalObjective(2, "world", -4046, -61, -1830, -4045, -59, -1829);
        q9Hell.setStageEndCommand(2, "warp q9_m3_hell");

        // Stage 3 - Kill final boss
        q9Hell.setBossObjective(3, "medusa_hell", "Final Boss: Medusa");

        questData.put("q9_hell", q9Hell);

        // Q9 Blood
        DungeonQuest q9Blood = new DungeonQuest("q9_blood", "Q9 Bloodshed", 80, 1.5, "Qblood", 50);
        q9Blood.setLocationMessage("Find and collect statue fragments");

        // Stage 1 - Collect statue fragments
        q9Blood.addStatueLocations(1, new int[][] {
                {-4080, -60, -2170}, {-4024, -60, -2193}, {-4079, -60, -2245}, {-4039, -60, -2310},
                {-4086, -60, -2331}, {-4130, -60, -2261}, {-4156, -60, -2294}, {-4154, -60, -2212}
        });
        q9Blood.setRequiredStatues(1, 4);
        q9Blood.setStageMessage(1, "Find and interact with 4 ancient statues to collect their fragments");

        // Stage 1 - Kill mini-boss
        q9Blood.setBossObjective(1, "asterion_blood", "Mini-Boss: Asterion");
        q9Blood.addPortalObjective(1, "world", -4206, -61, -2255, -4207, -59, -2256);
        q9Blood.setStageEndCommand(1, "warp q9_m2_blood");

        // Stage 2 - Activate 5 metronomes
        q9Blood.setStageMessage(2, "Find and activate 5 ancient metronomes");
        q9Blood.setRequiredAltars(2, 5);

        // Stage 2 - Kill mini-boss
        q9Blood.setBossObjective(2, "ebicarus_blood", "Mini-Boss: Ebicarus");
        q9Blood.addPortalObjective(2, "world", -4094, -61, -2675, -4093, -59, -2674);
        q9Blood.setStageEndCommand(2, "warp q9_m3_blood");

        // Stage 3 - Kill final boss
        q9Blood.setBossObjective(3, "medusa_blood", "Final Boss: Medusa");

        questData.put("q9_blood", q9Blood);
    }

    private static void initializeQ10() {
        // Q10 Infernal
        DungeonQuest q10Inf = new DungeonQuest("q10_inf", "Q10 Infernal", 50, 0.5, "Qinf", 10);
        q10Inf.setLocationMessage("Find and collect ancient fragments");

        // Stage 1 - Fragment collection and deposit
        q10Inf.setStageMessage(1, "Collect 3 ancient fragments from lime shulker boxes and deposit them into lodestones");
        q10Inf.setRequiredFragments(1, 3); // Need to collect and deposit 3 fragments

        // Stage 1 - Kill mini-boss
        q10Inf.setBossObjective(1, "melas_the_swift_footed_inf", "Mini-Boss: Melas the Swift-Footed");
        q10Inf.addPortalObjective(1, "world", -4065, -61, -186, -4064, -59, -185);
        q10Inf.setStageEndCommand(1, "warp q10_m2_inf");

        // Stage 2 - Collect golden walrus statuette
        q10Inf.setStageMessage(2, "Find a Golden Walrus Statuette to awaken the guardian");
        q10Inf.addCollectObjective(2, "armed_khaross_inf", 1, 10, "Golden Walrus Statuette", true, 30);

        // Stage 2 - Kill mini-boss
        q10Inf.setBossObjective(2, "akheilos_inf", "Mini-Boss: Akheilos");
        q10Inf.addPortalObjective(2, "world", -4131, -61, 269, -4130, -59, 270);
        q10Inf.setStageEndCommand(2, "warp q10_m3_inf");

        // Stage 3 - Kill final boss
        q10Inf.setBossObjective(3, "parallel_world_gorga_inf", "Final Boss: Parallel World Gorga");

        questData.put("q10_inf", q10Inf);

        // Q10 Hell
        DungeonQuest q10Hell = new DungeonQuest("q10_hell", "Q10 Hell", 65, 1.0, "Qhell", 25);
        q10Hell.setLocationMessage("Find and collect ancient fragments");

        // Stage 1 - Fragment collection and deposit
        q10Hell.setStageMessage(1, "Collect 3 ancient fragments from lime shulker boxes and deposit them into lodestones");
        q10Hell.setRequiredFragments(1, 3);

        // Stage 1 - Kill mini-boss
        q10Hell.setBossObjective(1, "melas_the_swift_footed_hell", "Mini-Boss: Melas the Swift-Footed");
        q10Hell.addPortalObjective(1, "world", -4114, -61, 781, -4113, -59, 782);
        q10Hell.setStageEndCommand(1, "warp q10_m2_hell");

        // Stage 2 - Collect golden walrus statuette
        q10Hell.setStageMessage(2, "Find a Golden Walrus Statuette to awaken the guardian");
        q10Hell.addCollectObjective(2, "armed_khaross_hell", 1, 10, "Golden Walrus Statuette", true, 30);

        // Stage 2 - Kill mini-boss
        q10Hell.setBossObjective(2, "akheilos_hell", "Mini-Boss: Akheilos");
        q10Hell.addPortalObjective(2, "world", -4180, -61, 1236, -4179, -59, 1237);
        q10Hell.setStageEndCommand(2, "warp q10_m3_hell");

        // Stage 3 - Kill final boss
        q10Hell.setBossObjective(3, "parallel_world_gorga_hell", "Final Boss: Parallel World Gorga");

        questData.put("q10_hell", q10Hell);

        // Q10 Blood
        DungeonQuest q10Blood = new DungeonQuest("q10_blood", "Q10 Bloodshed", 80, 1.5, "Qblood", 50);
        q10Blood.setLocationMessage("Find and collect ancient fragments");

        // Stage 1 - Fragment collection and deposit
        q10Blood.setStageMessage(1, "Collect 3 ancient fragments from lime shulker boxes and deposit them into lodestones");
        q10Blood.setRequiredFragments(1, 3);

        // Stage 1 - Kill mini-boss
        q10Blood.setBossObjective(1, "melas_the_swift_footed_blood", "Mini-Boss: Melas the Swift-Footed");
        q10Blood.addPortalObjective(1, "world", -4163, -61, 1753, -4162, -59, 1754);
        q10Blood.setStageEndCommand(1, "warp q10_m2_blood");

        // Stage 2 - Collect golden walrus statuette
        q10Blood.setStageMessage(2, "Find a Golden Walrus Statuette to awaken the guardian");
        q10Blood.addCollectObjective(2, "armed_khaross_blood", 1, 10, "Golden Walrus Statuette", true, 30);

        // Stage 2 - Kill mini-boss
        q10Blood.setBossObjective(2, "akheilos_blood", "Mini-Boss: Akheilos");
        q10Blood.addPortalObjective(2, "world", -4229, -61, 2208, -4228, -59, 2209);
        q10Blood.setStageEndCommand(2, "warp q10_m3_blood");

        // Stage 3 - Kill final boss
        q10Blood.setBossObjective(3, "parallel_world_gorga_blood", "Final Boss: Parallel World Gorga");

        questData.put("q10_blood", q10Blood);
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
        private final Map<Integer, int[][]> statueLocations = new HashMap<>();
        private final Map<Integer, Integer> requiredStatues = new HashMap<>();
        private final Map<Integer, Integer> requiredAltars = new HashMap<>();
        private final Map<Integer, Integer> requiredFragments = new HashMap<>();

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

        // Q9 specific methods
        public void addStatueLocations(int stage, int[][] locations) {
            statueLocations.put(stage, locations);
        }

        public int[][] getStatueLocations(int stage) {
            return statueLocations.get(stage);
        }

        public void setRequiredStatues(int stage, int count) {
            requiredStatues.put(stage, count);
        }

        public int getRequiredStatues(int stage) {
            return requiredStatues.getOrDefault(stage, 0);
        }

        public void setRequiredAltars(int stage, int count) {
            requiredAltars.put(stage, count);
        }

        public int getRequiredAltars(int stage) {
            return requiredAltars.getOrDefault(stage, 0);
        }

        public boolean hasStatueObjective(int stage) {
            return statueLocations.containsKey(stage);
        }

        public boolean hasAltarObjective(int stage) {
            return requiredAltars.containsKey(stage);
        }

        // Q10 specific methods
        public void setRequiredFragments(int stage, int count) {
            requiredFragments.put(stage, count);
        }

        public int getRequiredFragments(int stage) {
            return requiredFragments.getOrDefault(stage, 0);
        }

        public boolean hasFragmentObjective(int stage) {
            return requiredFragments.containsKey(stage);
        }

        public void addCollectObjective(int stage, String mobId, int count, int dropChance, String displayName) {
            collectObjectives.get(stage).put(mobId, new CollectObjective(mobId, count, dropChance, displayName, false));
        }

        public void addCollectObjective(int stage, String mobId, int count, int dropChance, String displayName, boolean progressive) {
            collectObjectives.get(stage).put(mobId, new CollectObjective(mobId, count, dropChance, displayName, progressive));
        }

        public void addCollectObjective(int stage, String mobId, int count, int dropChance, String displayName, boolean progressive, int guaranteedAfterKills) {
            collectObjectives.get(stage).put(mobId, new CollectObjective(mobId, count, dropChance, displayName, progressive, guaranteedAfterKills));
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

        public void addLocationObjective(int stage, String world, int x1, int y1, int z1, int x2, int y2, int z2, int radius) {
            initialLocationObjectives.put(stage, new LocationInfo(world, x1, y1, z1, x2, y2, z2, radius));
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
            } else if (itemReward.equals("Qhell")) {
                return "Hell Item Chest";
            } else if (itemReward.equals("Qblood")) {
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
        private int radius = 0;

        public LocationInfo(String world, int x1, int y1, int z1, int x2, int y2, int z2) {
            this.world = world;
            this.x1 = x1;
            this.y1 = y1;
            this.z1 = z1;
            this.x2 = x2;
            this.y2 = y2;
            this.z2 = z2;
        }

        // Constructor for radius-based locations
        public LocationInfo(String world, int x1, int y1, int z1, int x2, int y2, int z2, int radius) {
            this.world = world;
            this.x1 = x1;
            this.y1 = y1;
            this.z1 = z1;
            this.x2 = x2;
            this.y2 = y2;
            this.z2 = z2;
            this.radius = radius;
        }

        public String getWorld() { return world; }
        public int getX1() { return x1; }
        public int getY1() { return y1; }
        public int getZ1() { return z1; }
        public int getX2() { return x2; }
        public int getY2() { return y2; }
        public int getZ2() { return z2; }
        public int getRadius() { return radius; }

        public boolean isInside(Location loc) {
            if (!loc.getWorld().getName().equals(world)) {
                return false;
            }

            // If radius is specified, check if within radius
            if (radius > 0) {
                int centerX = (x1 + x2) / 2;
                int centerY = (y1 + y2) / 2;
                int centerZ = (z1 + z2) / 2;

                double distanceSquared = Math.pow(loc.getBlockX() - centerX, 2) +
                        Math.pow(loc.getBlockY() - centerY, 2) +
                        Math.pow(loc.getBlockZ() - centerZ, 2);

                return distanceSquared <= Math.pow(radius, 2);
            }

            // Otherwise check if within bounds
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
        private final int guaranteedAfterKills; // After how many kills it's guaranteed
        public CollectObjective(String mobId, int count, int dropChance, String displayName, boolean progressive) {
            this(mobId, count, dropChance, displayName, progressive, 0);
        }
        public CollectObjective(String mobId, int count, int dropChance, String displayName, boolean progressive, int guaranteedAfterKills) {
            this.mobId = mobId;
            this.count = count;
            this.dropChance = dropChance;
            this.displayName = displayName;
            this.progressive = progressive;
            this.guaranteedAfterKills = guaranteedAfterKills;
        }

        public String getMobId() { return mobId; }
        public int getCount() { return count; }
        public int getDropChance() { return dropChance; }
        public String getDisplayName() { return displayName; }
        public boolean isProgressive() { return progressive; }
        public int getGuaranteedAfterKills() { return guaranteedAfterKills; }

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
