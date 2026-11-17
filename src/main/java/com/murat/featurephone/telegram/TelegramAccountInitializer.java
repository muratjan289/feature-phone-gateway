package com.murat.featurephone.telegram;


import com.murat.featurephone.service.TelegramAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TelegramAccountInitializer {


    private final TelegramAccountService telegramAccountService;
    private final TelegramTdClient telegramTdClient;
    private final TelegramChatSyncService telegramChatSyncService;

    @Async("telegramUpdateExecutor")
    @EventListener
    public void onTelegramAuthorized(TelegramAuthorizedEvent event){

        try{
            var user = telegramTdClient.getMe()
                    .get(10, TimeUnit.SECONDS);

            telegramAccountService.getOrCreateTelegramAccount(
                    1L, user.id
            );
            System.out.println("Telegram account linked successfully");

            telegramChatSyncService.syncChats(1L );
        }

        catch (Exception e){
            System.err.println("Telegram account initialization failed " + e.getMessage());
        }

    }
}
