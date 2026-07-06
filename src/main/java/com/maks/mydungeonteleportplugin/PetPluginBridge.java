package com.maks.mydungeonteleportplugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;

/**
 * Reflection-based bridge to the PetPlugin dungeon API. Falls back gracefully when absent.
 */
public class PetPluginBridge {

    private static final String API_CLASS = "pl.yourserver.api.PetDungeonApi";

    private final MyDungeonTeleportPlugin plugin;
    private Object dungeonApi;
    private Method getChanceMethod;
    private Method shouldGrantMethod;
    private Method damageBonusMethod;
    private Method executeMethod;

    public PetPluginBridge(MyDungeonTeleportPlugin plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        try {
            Class<?> apiClass = Class.forName(API_CLASS);
            RegisteredServiceProvider<?> registration = Bukkit.getServicesManager().getRegistration(apiClass);
            if (registration == null) {
                plugin.getLogger().info("PetPlugin API not found via ServicesManager.");
                return;
            }

            dungeonApi = registration.getProvider();
            getChanceMethod = apiClass.getMethod("getEndermanFreeTeleportChance", Player.class);
            shouldGrantMethod = apiClass.getMethod("shouldGrantFreeTeleport", Player.class);
            damageBonusMethod = apiClass.getMethod("getDungeonQuestDamageBonus", Player.class, String.class);
            executeMethod = apiClass.getMethod("hasDungeonExecuteEffect", Player.class, String.class);

            plugin.getLogger().info("Hooked into PetPlugin dungeon API.");
        } catch (ClassNotFoundException e) {
            plugin.getLogger().info("PetPlugin API class not present. Skipping integration.");
        } catch (NoSuchMethodException e) {
            plugin.getLogger().log(Level.WARNING, "PetPlugin API signature mismatch. Integration disabled.", e);
            dungeonApi = null;
        }
    }

    public boolean isAvailable() {
        return dungeonApi != null && getChanceMethod != null;
    }

    public double getFreeTeleportChance(Player player) {
        if (!isAvailable() || player == null) {
            return 0.0;
        }
        try {
            Object result = getChanceMethod.invoke(dungeonApi, player);
            return result instanceof Number ? ((Number) result).doubleValue() : 0.0;
        } catch (IllegalAccessException | InvocationTargetException e) {
            handleInvocationError("getEndermanFreeTeleportChance", e);
            return 0.0;
        }
    }

    public boolean shouldGrantFreeTeleport(Player player) {
        if (!isAvailable() || shouldGrantMethod == null || player == null) {
            return false;
        }
        try {
            Object result = shouldGrantMethod.invoke(dungeonApi, player);
            return result instanceof Boolean && (Boolean) result;
        } catch (IllegalAccessException | InvocationTargetException e) {
            handleInvocationError("shouldGrantFreeTeleport", e);
            return false;
        }
    }

    public double getDungeonDamageBonus(Player player, String questId) {
        if (!isAvailable() || damageBonusMethod == null || player == null) {
            return 0.0;
        }
        try {
            Object result = damageBonusMethod.invoke(dungeonApi, player, questId);
            return result instanceof Number ? ((Number) result).doubleValue() : 0.0;
        } catch (IllegalAccessException | InvocationTargetException e) {
            handleInvocationError("getDungeonQuestDamageBonus", e);
            return 0.0;
        }
    }

    public boolean hasDungeonExecuteEffect(Player player, String questId) {
        if (!isAvailable() || executeMethod == null || player == null) {
            return false;
        }
        try {
            Object result = executeMethod.invoke(dungeonApi, player, questId);
            return result instanceof Boolean && (Boolean) result;
        } catch (IllegalAccessException | InvocationTargetException e) {
            handleInvocationError("hasDungeonExecuteEffect", e);
            return false;
        }
    }

    private void handleInvocationError(String method, Exception exception) {
        if (plugin.getConfig().getBoolean("debug", false)) {
            plugin.getLogger().log(Level.WARNING, "PetPlugin API call failed for " + method, exception);
        }
    }
}
