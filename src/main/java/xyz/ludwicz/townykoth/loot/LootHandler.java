package xyz.ludwicz.townykoth.loot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;
import xyz.ludwicz.townykoth.KOTH;
import xyz.ludwicz.townykoth.TownyKOTH;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LootHandler {

    private static Gson GSON = new GsonBuilder()
            .registerTypeAdapter(ItemStack.class, new ItemStackSerializer())
            .registerTypeAdapter(Inventory.class, new InventorySerializer())
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    @Getter
    private Set<Loot> loots = new HashSet<>();

    public LootHandler() {
        load();
    }

    public void load() {
        try {
            File directory = new File(TownyKOTH.getInstance().getDataFolder(), "loots");

            if (!directory.exists())
                directory.mkdirs();

            for (File file : directory.listFiles()) {
                if (!file.getName().endsWith(".json"))
                    continue;

                Loot loot = GSON.fromJson(FileUtils.readFileToString(file, "UTF-8"), Loot.class);
                loots.add(loot);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            File directory = new File(TownyKOTH.getInstance().getDataFolder(), "loots");

            if (!directory.exists())
                directory.mkdirs();

            for (File file : directory.listFiles())
                file.delete();

            for (Loot loot : loots) {
                FileUtils.write(new File(directory, loot.getName() + ".json"), GSON.toJson(loot), "UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void newLoot(String name) {
        Loot loot = new Loot(name);
        loots.add(loot);
        save();
    }

    public void deleteLoot(Loot loot) {
        loots.remove(loot);
        save();
    }

    public Loot getLoot(String name) {
        for (Loot loot : loots) {
            if (loot.getName().equalsIgnoreCase(name))
                return loot;
        }

        return null;
    }

    public List<String> getLootNames() {
        ArrayList<String> lootNames = new ArrayList<>();

        for(Loot loot : loots) {
            lootNames.add(loot.getName());
        }

        return lootNames;
    }

    public static class ItemStackSerializer implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {
        @Override
        public JsonElement serialize(ItemStack inventory, Type type, JsonSerializationContext jsonSerializationContext) {
            try {

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
                dataOutput.writeObject(inventory);

                dataOutput.close();
                return new JsonPrimitive(Base64Coder.encodeLines(outputStream.toByteArray()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        public ItemStack deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            try {
                String data = jsonElement.getAsJsonPrimitive().getAsString();

                ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
                BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
                try {
                    return (ItemStack) dataInput.readObject();
                } finally {
                    dataInput.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static class InventorySerializer implements JsonSerializer<Inventory>, JsonDeserializer<Inventory> {

        @Override
        public JsonElement serialize(Inventory inventory, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonArray array = new JsonArray();

            for (ItemStack item : inventory.getContents()) {
                if (item == null)
                    continue;

                JsonElement jsonItem = GSON.toJsonTree(item);
                array.add(jsonItem);
            }

            return array;
        }

        @Override
        public Inventory deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            Inventory inventory = Bukkit.createInventory(null, 27, "Edit Loot");

            JsonArray array = jsonElement.getAsJsonArray();

            array.forEach(jsonItem -> {
                ItemStack item = GSON.fromJson(jsonItem, ItemStack.class);
                inventory.addItem(item);
            });

            return null;
        }
    }

}
