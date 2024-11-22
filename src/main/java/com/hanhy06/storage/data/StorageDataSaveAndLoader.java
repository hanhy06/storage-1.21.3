package com.hanhy06.storage.data;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.hanhy06.storage.Storage;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class StorageDataSaveAndLoader {

    // Gson 객체 생성: Inventory를 처리할 어댑터 등록
    private static final Gson gsonInventory = new GsonBuilder()
            .registerTypeAdapter(Inventory.class, new InventoryAdapter())
            .setPrettyPrinting()
            .create();

    private static final Gson gson = new Gson();

    public static void saveStorageData() {
        String fileName = Storage.worldName + ".json";
        String folderName = "Storages";

        Path folderPath = Path.of(folderName);
        Path filePath = folderPath.resolve(fileName);

        try {
            if (Files.notExists(folderPath)) {
                Files.createDirectories(folderPath);
            }

            // Storage.storageDataList를 JSON으로 직렬화하여 파일에 저장
            String jsonData = gsonInventory.toJson(Storage.storageDataList);
            Files.writeString(filePath, jsonData,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to save storage data", e);
        }
    }

    public static void loadStorageData() {
        String fileName = Storage.worldName + ".json";
        String folderName = "Storages";

        Path filePath = Path.of(folderName).resolve(fileName);

        if (Files.exists(filePath)) {
            try {
                String jsonData = Files.readString(filePath);
                List<StorageData> dataList = gsonInventory.fromJson(jsonData, new TypeToken<List<StorageData>>(){}.getType());
                Storage.storageDataList = dataList;
            } catch (IOException e) {
                throw new RuntimeException("Failed to load storage data", e);
            }
        }
    }

    // Inventory 어댑터 정의
    public static class InventoryAdapter implements JsonSerializer<Inventory>, JsonDeserializer<Inventory> {
        @Override
        public JsonElement serialize(Inventory src, Type typeOfSrc, JsonSerializationContext context) {
            JsonArray jsonArray = new JsonArray();
            for (int i = 0; i < src.size(); i++) {
                jsonArray.add(context.serialize(src.getStack(i))); // ItemStack 직렬화
            }
            return jsonArray;
        }

        @Override
        public Inventory deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            Inventory inventory = new SimpleInventory(45); // 기본 크기
            JsonArray jsonArray = json.getAsJsonArray();
            for (int i = 0; i < jsonArray.size(); i++) {
                inventory.setStack(i, context.deserialize(jsonArray.get(i), ItemStack.class)); // ItemStack 역직렬화
            }
            return inventory;
        }
    }

    public static class ItemStackAdapter implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack>{
        @Override
        public JsonElement serialize(ItemStack itemStack, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject = new JsonObject();

            Identifier itemId = Registries.ITEM.getId(itemStack.getItem());
            if (itemId != null) {
                jsonObject.addProperty("item", itemId.toString());
            }

            return null;
        }

        @Override
        public ItemStack deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return null;
        }
    }
}