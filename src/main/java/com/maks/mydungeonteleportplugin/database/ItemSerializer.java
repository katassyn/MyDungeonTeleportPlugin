package com.maks.mydungeonteleportplugin.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for serializing and deserializing inventories and items
 */
public class ItemSerializer {

    private static final Gson gson = new GsonBuilder().create();

    /**
     * Serialize an inventory to a Base64 string
     * @param inventory The inventory to serialize
     * @return A Base64 encoded string representing the inventory
     */
    public static String inventoryToBase64(Inventory inventory) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            // Write the inventory size
            dataOutput.writeInt(inventory.getSize());

            // Write each item in the inventory
            for (int i = 0; i < inventory.getSize(); i++) {
                dataOutput.writeObject(inventory.getItem(i));
            }

            // Close streams
            dataOutput.close();

            // Return Base64 encoded string
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to serialize inventory", e);
        }
    }

    /**
     * Deserialize a Base64 string to an inventory
     * @param base64 The Base64 encoded string
     * @param title The title for the inventory
     * @param size The size of the inventory
     * @return The deserialized inventory
     */
    public static Inventory inventoryFromBase64(String base64, String title, int size) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(base64));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

            // Read the inventory size
            int invSize = dataInput.readInt();

            // Create a new inventory
            Inventory inventory = Bukkit.createInventory(null, size, title);

            // Read each item in the inventory
            for (int i = 0; i < invSize; i++) {
                ItemStack item = (ItemStack) dataInput.readObject();
                if (i < size) {
                    inventory.setItem(i, item);
                }
            }

            // Close streams
            dataInput.close();

            return inventory;
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("Failed to deserialize inventory", e);
        }
    }

    /**
     * Serialize an inventory to a JSON string with slot mapping
     * This is an alternative approach that stores items with their slot positions
     * @param inventory The inventory to serialize
     * @return A JSON string representing the inventory
     */
    public static String inventoryToJson(Inventory inventory) {
        Map<Integer, String> serializedItems = new HashMap<>();

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null) {
                try {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

                    dataOutput.writeObject(item);
                    dataOutput.close();

                    String serialized = Base64.getEncoder().encodeToString(outputStream.toByteArray());
                    serializedItems.put(i, serialized);
                } catch (IOException e) {
                    throw new IllegalStateException("Failed to serialize item at slot " + i, e);
                }
            }
        }

        return gson.toJson(serializedItems);
    }

    /**
     * Deserialize a JSON string to an inventory
     * @param json The JSON string
     * @param title The title for the inventory
     * @param size The size of the inventory
     * @return The deserialized inventory
     */
    public static Inventory inventoryFromJson(String json, String title, int size) {
        Type type = new TypeToken<Map<Integer, String>>(){}.getType();
        Map<Integer, String> serializedItems = gson.fromJson(json, type);

        Inventory inventory = Bukkit.createInventory(null, size, title);

        for (Map.Entry<Integer, String> entry : serializedItems.entrySet()) {
            int slot = entry.getKey();
            String serialized = entry.getValue();

            try {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(serialized));
                BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

                ItemStack item = (ItemStack) dataInput.readObject();
                dataInput.close();

                if (slot < size) {
                    inventory.setItem(slot, item);
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new IllegalStateException("Failed to deserialize item for slot " + slot, e);
            }
        }

        return inventory;
    }

    /**
     * Serialize a list of ItemStacks to Base64
     * @param items List of ItemStacks to serialize
     * @return Base64 encoded string
     */
    public static String itemListToBase64(List<ItemStack> items) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            // Write the size of the list
            dataOutput.writeInt(items.size());

            // Write each item
            for (ItemStack item : items) {
                dataOutput.writeObject(item);
            }

            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item list.", e);
        }
    }

    /**
     * Deserialize a list of ItemStacks from Base64
     * @param data Base64 encoded string
     * @return List of ItemStacks
     */
    public static List<ItemStack> itemListFromBase64(String data) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

            // Read the size of the list
            int size = dataInput.readInt();
            List<ItemStack> items = new ArrayList<>();

            // Read each item
            for (int i = 0; i < size; i++) {
                ItemStack item = (ItemStack) dataInput.readObject();
                if (item != null) {
                    items.add(item);
                }
            }

            dataInput.close();
            return items;
        } catch (Exception e) {
            throw new IllegalStateException("Unable to decode item list.", e);
        }
    }
}
