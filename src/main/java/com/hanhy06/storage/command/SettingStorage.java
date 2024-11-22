package com.hanhy06.storage.command;

import com.hanhy06.storage.Storage;
import com.hanhy06.storage.data.StorageData;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;

public class SettingStorage {
    // /storage 명령어 등록
    public static void registerSettingStorageCommand() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    CommandManager.literal("storage")
                            .then(CommandManager.literal("add_accept")
                                    .then(CommandManager.argument("targets", EntityArgumentType.players()) // 대상 지정
                                            .executes(context ->
                                                    addAcceptPlayer(context, EntityArgumentType.getPlayers(context, "targets"))
                                            )
                                    )
                            )

                            .then(CommandManager.literal("remove_accept")
                                    .then(CommandManager.argument("targets", EntityArgumentType.players()) // 대상 지정
                                            .executes(context ->
                                                    removeAcceptPlayer(context, EntityArgumentType.getPlayers(context, "targets"))
                                            )
                                    )
                            )
            );
        });
    }

    // 플레이어에게 창고 접근 권한을 부여합니다
    private static int addAcceptPlayer(CommandContext<ServerCommandSource> context, Collection<ServerPlayerEntity> targets) {
        PlayerEntity owner = context.getSource().getPlayer();

        // 플레이어가 소유한 창고 데이터를 검색
        for (StorageData data : Storage.storageDataList) {
            if (data.getOwner().equals(owner.getName().getString())) {
                for(PlayerEntity target:targets){
                    String targetName = target.getName().getString();

                    if(!data.acceptPlayers.contains(targetName)){
                        data.addAcceptPlayer(target.getName().getString());
                        owner.sendMessage(Text.literal(String.format("플레이어 %s 님이 접근 권한이 부여되었습니다.", targetName)), false);
                    }
                    else{
                        owner.sendMessage(Text.literal(String.format("플레이어 %s 님은 이미 접근 권한이 부여되었습니다.", targetName)), false);
                    }
                }
                return 1;
            }
        }

        // 창고를 찾을 수 없는 경우 오류 메시지 전송
        owner.sendMessage(Text.literal(String.format("요청하신 플레이어(들) 을 찾을수 없습니다.")),false);
        return 0;
    }

    // 플레이어의 창고 접근 권한을 해제합니다
    private static int removeAcceptPlayer(CommandContext<ServerCommandSource> context, Collection<ServerPlayerEntity> targets) {
        PlayerEntity owner = context.getSource().getPlayer();

        // 플레이어가 소유한 창고 데이터를 검색
        for (StorageData data : Storage.storageDataList) {
            if (data.getOwner().equals(owner.getName().getString())) {
                for (PlayerEntity target : targets) {
                    String targetName = target.getName().getString();

                    if(data.acceptPlayers.contains(targetName)){
                        data.removeAcceptPlayer(target.getName().getString());
                        owner.sendMessage(Text.literal(String.format("플레이어 %s 님의 접근 권한이 해제되었습니다.", targetName)), false);
                    }
                    else{
                        owner.sendMessage(Text.literal(String.format("플레이어 %s 님의은 접근 권한이 없습니다.", targetName)), false);
                    }
                }
                return 1;
            }
        }

        // 창고를 찾을 수 없는 경우 오류 메시지 전송
        owner.sendMessage(Text.literal("요청하신 플레이어(들)을 찾을 수 없습니다."), false);
        return 0;
    }
}
