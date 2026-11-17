package com.murat.featurephone.telegram;


import com.murat.featurephone.persistence.entity.TelegramAccountEntity;
import com.murat.featurephone.service.TelegramAccountService;
import com.murat.featurephone.service.TelegramChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.drinkless.tdlib.TdApi;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramChatSyncService {



    private final TelegramTdClient telegramTdClient;
    private final TelegramAccountService telegramAccountService;
    private final TelegramChatService telegramChatService;
    private final AtomicBoolean syncInProgress = new AtomicBoolean(false);


    public void syncChats(Long gatewayUserId) throws Exception {

        if(!syncInProgress.compareAndSet(false, true)) {
            log.info("Telegram chat synchronization already running. Skipping.");
            return;
        }

        try {

        TdApi.User me = telegramTdClient.getMe()
                .get(10, TimeUnit.SECONDS);

        TelegramAccountEntity account =
                telegramAccountService.getOrCreateTelegramAccount(
                gatewayUserId, me.id
        );

        Set<Long> mainChatIds = syncChatList(
                account.getId(),
                new TdApi.ChatListMain(),
                false
        );
            Set<Long> archivedChatIds = syncChatList(
                    account.getId(),
                    new TdApi.ChatListArchive(),
                    true
            );

            Set<Long> actualChatIds = new HashSet<>(mainChatIds);
            actualChatIds.addAll(archivedChatIds);

            int deactivatedChatCount = telegramChatService.deactivateMissingChats(
                    account.getId(), actualChatIds
            );

            log.info(" Telegram chat synchronization completed : deactivated={}", deactivatedChatCount);

            log.info("Telegram synchronization completed: main={}, archive={}, total={}",
                    mainChatIds.size(), archivedChatIds.size(), actualChatIds);


    } finally {
            syncInProgress.set(false);
        }
    }

    private void loadAllChats(TdApi.ChatList chatList) throws Exception {

        int maxBatches = 1000;
        for (int batch = 0; batch < maxBatches; batch++) {
            boolean hasMore = telegramTdClient
                    .loadChats(chatList, 100)
                    .get(30, TimeUnit.SECONDS);
            if (!hasMore) {
                return;
            }
        }

        throw new IllegalStateException(
                "Telegram chat loading exceeded maximum batches"
        );
    }

    private Set<Long> syncChatList(
            Long accountId,
            TdApi.ChatList chatList,
            boolean archived
    )throws Exception{


        loadAllChats(chatList);
        long[] chatIds = telegramTdClient
                .getChatIds(chatList,100_000)
                .get(30, TimeUnit.SECONDS);

        if (chatIds.length >= 100_000) {
            throw new IllegalStateException(
                    "Telegram chat list reached the configured limit. " +
                            "Reconciliation aborted."

            );

        }

        Set<Long> actualChatIds = new HashSet<>();

        for(long chatId : chatIds){
            TdApi.Chat chat = telegramTdClient
                    .getChat(chatId)
                    .get(10, TimeUnit.SECONDS);

            telegramChatService.upsert(
                    accountId,
                    chat.id,
                    chat.title,
                    chat.type.getClass().getSimpleName(),
                    chat.unreadCount,
                    archived

            );
            actualChatIds.add(chatId);
        }

        log.info("Telegram chat list synchronized: archived={}, count={}",
                archived, chatIds.length);



        return actualChatIds;
    }

}
