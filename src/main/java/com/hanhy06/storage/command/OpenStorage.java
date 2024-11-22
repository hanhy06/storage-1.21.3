package com.hanhy06.storage.command;

import com.hanhy06.storage.Storage;
import com.hanhy06.storage.data.StorageData;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class OpenStorage {
    // /open 명령어 등록
    public static void registerOpenStorageCommand() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    CommandManager.literal("open")
                            .executes(OpenStorage::openStorageScreen) // 인자가 없는 경우 실행

                            .then(CommandManager.argument("target", EntityArgumentType.player())
                                    .executes(context -> openStorageScreen(context, EntityArgumentType.getPlayer(context, "target")))) // 대상 플레이어와 함께 실행

                            .then(CommandManager.literal("server")
                                    .executes(OpenStorage::openServerStorageScreen) // 서버 창고 실행
                            )
            );
        });
    }

    // 명령어를 실행한 플레이어의 창고 화면을 엽니다
    private static int openStorageScreen(CommandContext<ServerCommandSource> context) {
        PlayerEntity runPlayer = context.getSource().getPlayer();

        // 플레이어가 소유한 창고 데이터를 검색
        for (StorageData data : Storage.storageDataList) {
            if (runPlayer.getName().getString().equals(data.getOwner())) {
                openStorageForPlayer(runPlayer, data);
                return 1;
            }
        }

        // 창고를 찾을 수 없는 경우 오류 메시지 전송
        context.getSource().sendError(Text.of("해당 창고를 찾을 수 없습니다."));
        return 0;
    }

    // 명령어를 실행한 플레이어에게 서버 창고 화면을 엽니다
    private static int openServerStorageScreen(CommandContext<ServerCommandSource> context) {
        PlayerEntity runPlayer = context.getSource().getPlayer();

        // 첫 번째 창고 데이터를 서버 창고로 열기
        openStorageForPlayer(runPlayer, Storage.storageDataList.getFirst());

        return 1;
    }

    // 대상 플레이어의 창고 화면을 엽니다 (실행자가 권한이 있는 경우)
    private static int openStorageScreen(CommandContext<ServerCommandSource> context, PlayerEntity target) {
        PlayerEntity runPlayer = context.getSource().getPlayer();

        // 대상 플레이어가 소유한 창고 데이터를 검색
        for (StorageData data : Storage.storageDataList) {
            if (target.getName().getString().equals(data.getOwner()) && data.getAcceptPlayers().contains(runPlayer.getName().getString())) {
                openStorageForPlayer(runPlayer, data);
                return 1;
            }
        }

        // 창고를 찾을 수 없는 경우 오류 메시지 전송
        context.getSource().sendError(Text.of("대상 창고를 찾을 수 없습니다."));
        return 0;
    }

    // 플레이어에게 창고를 열어주는 유틸리티 메서드
    private static void openStorageForPlayer(PlayerEntity player, StorageData data) {
        player.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, inventory, playerEntity) ->
                new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X5, syncId, inventory, data.getInventory(),5),
                Text.of(data.getOwner() + "의 창고")
        ));
    }
}
