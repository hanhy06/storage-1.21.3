package com.hanhy06.storage.data;

import com.hanhy06.storage.Storage;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.PersistentState;

import java.util.ArrayList;
import java.util.List;

public class StorageDataManager extends PersistentState {
    public NbtCompound toNbt(StorageData storageData){
        NbtCompound nbt = new NbtCompound();

        nbt.putString("owner", storageData.getOwner());

        NbtList items = new NbtList();
        for(int i=0;i<storageData.getInventory().size();i++){
            ItemStack itemStack=storageData.getInventory().getStack(i);
            NbtCompound itemNbt = (NbtCompound)itemStack.CODEC.encodeStart(NbtOps.INSTANCE, itemStack).getOrThrow();
            itemNbt.putInt("Slot",i);
            items.add(i,itemNbt);
        }

        nbt.put("Inventory",items);

        NbtList players = new NbtList();

        for(String name:storageData.getAcceptPlayers()){
            players.add(NbtString.of(name));
        }

        nbt.put("players",players);

        return nbt;
    }

    public static StorageDataManager fromNbt(NbtCompound nbt){
        StorageDataManager manager = new StorageDataManager();
        NbtList nbtList = nbt.getList("StorageData", NbtElement.COMPOUND_TYPE);

        for (int i = 0; i < nbtList.size(); i++) {
            NbtCompound storageNbt = nbtList.getCompound(i);

            String owner = storageNbt.getString("owner");

            NbtList items = storageNbt.getList("Inventory", NbtElement.COMPOUND_TYPE);
            Inventory inventory = new SimpleInventory(54);
            for (int j = 0; j < items.size(); j++) {
                NbtCompound itemNbt = items.getCompound(j);
                ItemStack itemStack = ItemStack.CODEC.decode(NbtOps.INSTANCE, itemNbt).getOrThrow().getFirst();
                int slot = itemNbt.getInt("Slot");
                inventory.setStack(slot, itemStack);
            }

            List<String> players = new ArrayList<>();
            NbtList playersNbt = storageNbt.getList("players", NbtElement.STRING_TYPE);
            for (int j = 0; j < playersNbt.size(); j++) {
                players.add(playersNbt.getString(j));
            }

            StorageData storageData = new StorageData(owner, players, inventory);
            Storage.storageDataList.add(storageData);
        }

        return manager;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        NbtList nbtList = new NbtList();

        for(int i=0;i< Storage.storageDataList.size();i++){
            nbtList.add(toNbt(Storage.storageDataList.get(i)));
        }

        nbt.put("StorageData",nbtList);
        return nbt;
    }
}