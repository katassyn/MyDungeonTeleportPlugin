# MyDungeonTeleportPlugin

A comprehensive Minecraft plugin for managing dungeon-based quests with teleportation, difficulty levels, and integration with other plugins. Players can access dungeons through NPC interactions, fulfill specific requirements, and embark on challenging quests. Currently, the plugin fully supports dungeons `Q1` and `Q2`, with plans for future expansion.

---

## Features

### **Dungeon Access via NPCs**
- Each dungeon (`Q1` to `Q10`) is accessed through NPCs configured with permissions:
  - Permissions control which players can interact with specific NPCs for each dungeon.
- Players can choose from three difficulty levels:
  1. **Infernal**: Easiest difficulty.
  2. **Hell**: Medium difficulty.
  3. **Bloodshed**: Highest difficulty.
- Requirements for entering a dungeon:
  - **Player Level**: Must meet the minimum level for the selected difficulty.
  - **Special Item (IPS)**: A key item required to unlock the portal.

### **Portal Activation**
- When requirements are met, the plugin:
  1. Consumes the required **IPS** item(s).
  2. Activates the portal for the player.
  3. Teleports the player via EssentialsX warp.
  4. Assigns a quest to the player (via BeautyQuests).
  5. Resets spawners in the dungeon (via MythicMobs).

### **Quest Integration**
- Players receive a quest upon entering a dungeon.
- Spawners reset dynamically within the dungeon.
- Players have **30 minutes** to complete the quest:
  - If the timer expires, the plugin automatically kills the player.
  - Players also die upon quest completion or failure.

### **Command `/whodoq`**
- Available to all players.
- Displays a list of active dungeon participants, showing:
  - The dungeon and difficulty they are in.
  - Remaining time before the dungeon is freed.
- Dungeons are freed when:
  - The player completes the quest.
  - The player dies (from mobs, timer expiration, or quest completion).

---

## Configuration (Planned for Future Updates)

### **dungeon_config.yml**
In future updates, all dungeon settings will be configurable, including:
- **Portal Coordinates**: Locations for dungeon portals.
- **Key Items (IPS)**: Items required for each dungeon and difficulty.
- **Player Level Requirements**: Levels needed for entry.
- **Quest Names**: Associated quests for each dungeon and difficulty.
- **Warps**: Teleport destinations upon portal entry.

---

## Integration with Other Plugins

### **Required Plugins**
- **EssentialsX**: Handles warps for dungeon teleportation.
- **MythicMobs**: Manages dungeon mob spawners and resets.
- **BeautyQuests**: Assigns quests to players upon dungeon entry.

### **How It Works**
1. Players interact with an NPC assigned to a dungeon.
2. The plugin checks:
   - Player level.
   - Possession of the required key item (IPS).
3. If criteria are met:
   - The portal activates for the player.
   - The player receives a quest and is teleported into the dungeon.
   - Spawners reset for the dungeon.
4. The player must complete the quest within 30 minutes or they will die.
5. Upon quest completion, the player dies, freeing the dungeon for others.

---

## Installation

1. Place the plugin's JAR file into your server's `plugins` folder.
2. Ensure the following plugins are installed:
   - **EssentialsX**
   - **MythicMobs**
   - **BeautyQuests**
3. Start or reload the server.
4. Configure the dependencies (if required) for your environment.
5. In the future, configure `dungeon_config.yml` (currently hardcoded).

---

## Current Status

- **Fully Functional Dungeons**: `Q1` and `Q2`
  - All features work, including quests, spawner resets, and player management.
- **Future Dungeons (`Q3` to `Q10`)**:
  - Lack quest and mob setups due to incomplete maps and configurations.

---

## Planned Features

- **Dynamic Configuration**:
  - Move hardcoded settings (e.g., portal coordinates, keys, levels) to `dungeon_config.yml`.
    
---

## Commands

### **Player Commands**
- `/whodoq`:
  - Displays the list of players currently in dungeons and their respective timers.

---

## Requirements

- **EssentialsX**: For player teleportation (warps).
- **MythicMobs**: For dungeon mob spawners and resets.
- **BeautyQuests**: For assigning quests upon entry.

---

## Example Dungeon Flow

1. **Access the NPC**:
   - Player interacts with an NPC assigned to a dungeon (e.g., Q1).
2. **Select Difficulty**:
   - Player chooses a difficulty level (Infernal, Hell, Bloodshed).
3. **Portal Activation**:
   - Plugin verifies player level and IPS items.
   - If successful:
     - Activates the portal.
     - Assigns a quest.
     - Teleports the player.
4. **Quest Timer**:
   - Player has 30 minutes to complete the quest.
   - If the timer runs out, the player is killed.
5. **Quest Completion**:
   - Player finishes the quest and dies, releasing the dungeon.

---

## Support

If you encounter any issues or have feature requests, feel free to open an issue in the repository or contact the developer.

Enjoy using **MyDungeonTeleportPlugin**! 😊
