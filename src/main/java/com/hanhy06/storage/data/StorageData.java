package com.hanhy06.storage.data;

import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StorageData {
    public String owner;
    public List<String> acceptPlayers;
    public Inventory inventory;

    public StorageData(String owner, List<String> acceptPlayers, Inventory inventory) {
        this.owner = owner;
        this.acceptPlayers = new ArrayList<>(acceptPlayers); // 방어적 복사
        this.inventory = inventory != null ? inventory : new SimpleInventory(45); // Null 방지
    }

    public String getOwner() {
        return owner;
    }

    public List<String> getAcceptPlayers() {
        return Collections.unmodifiableList(acceptPlayers); // 방어적 반환
    }

    public void addAcceptPlayer(String player) {
        if (!acceptPlayers.contains(player)) {
            acceptPlayers.add(player);
        }
    }

    public void removeAcceptPlayer(String player) {
        acceptPlayers.remove(player);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public String toString() {
        return "StorageData{" +
                "owner='" + owner + '\'' +
                ", acceptPlayers=" + acceptPlayers +
                ", inventory=" + inventory +
                '}';
    }
}

