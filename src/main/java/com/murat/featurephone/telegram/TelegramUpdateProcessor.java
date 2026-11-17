package com.murat.featurephone.telegram;

import com.murat.featurephone.service.TelegramMessageService;
import lombok.RequiredArgsConstructor;
import org.drinkless.tdlib.TdApi;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class TelegramUpdateProcessor {


    private final TelegramMessageService telegramMessageService;


    @Async("telegramUpdateExecutor")
    public void processNewMessage(
            Long internalChatId,
            TdApi.Message message
    ){

        LocalDateTime sentAt = LocalDateTime.ofEpochSecond(
                message.date,
                0,
                ZoneOffset.UTC
        );

        String text = null;
        String contentType = "OTHER";

        if (message.content instanceof TdApi.MessageText messageText) {
            text = messageText.text.text;
            contentType = "TEXT";
        } else if (message.content instanceof TdApi.MessagePhoto){
            contentType = "PHOTO";
        } else if (message.content instanceof TdApi.MessageVoiceNote){
            contentType = "VOICE";
        } else if (message.content instanceof TdApi.MessageVideo){
            contentType = "VIDEO";
        }else if (message.content instanceof TdApi.MessageDocument){
            contentType = "DOCUMENT";
        }

        Long senderId = null;

        if (message.senderId instanceof TdApi.MessageSenderUser sender){
            senderId = sender.userId;
        }

        telegramMessageService.saveOrUpdate(
                internalChatId,
                message.id,
                senderId,
                text,
                message.isOutgoing,
                sentAt,
                contentType

        );

    }
}
