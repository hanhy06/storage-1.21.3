package com.hanhy06.storage;

import com.hanhy06.storage.command.OpenStorage;
import com.hanhy06.storage.command.SettingStorage;
import com.hanhy06.storage.data.StorageData;
import com.hanhy06.storage.data.StorageDataManager;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.util.WorldSavePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Storage implements ModInitializer {
	public static final String MOD_ID = "storage";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static List<StorageData> storageDataList = new ArrayList<>();

	@Override
	public void onInitialize() {
		ServerWorldEvents.LOAD.register(((minecraftServer, serverWorld) -> {
			StorageDataManager manager = new StorageDataManager();
		}));

		// /open 및 /storage 명령어 등록
		OpenStorage.registerOpenStorageCommand();
		SettingStorage.registerSettingStorageCommand();

		// 서버 전용 창고 데이터 추가
		storageDataList.add(new StorageData("server", new ArrayList<>(), new SimpleInventory(45)));

		// 플레이어가 서버에 접속할 때 이벤트 등록
		ServerPlayConnectionEvents.JOIN.register((serverPlayNetworkHandler, packetSender, minecraftServer) -> {
			String playerName = serverPlayNetworkHandler.player.getName().getString();

			// 플레이어의 창고 데이터가 없는 경우 새로 추가
			if (storageDataList.stream().noneMatch(data -> data.getOwner().equals(playerName))) {
				storageDataList.add(new StorageData(playerName, new ArrayList<>(), new SimpleInventory(54)));
			}

			// 플레이어 접속 로그 출력
			LOGGER.info(String.format("플레이어 %s 님이 접속하였습니다.", playerName));
		});

		// 모드 초기화 로그 출력
		LOGGER.info("Hello Fabric world!");
	}
}
