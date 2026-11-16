package com.murat.featurephone.service;


import com.murat.featurephone.persistence.entity.TelegramChatEntity;
import com.murat.featurephone.persistence.entity.TelegramMessageEntity;
import com.murat.featurephone.persistence.repository.TelegramChatRepository;
import com.murat.featurephone.persistence.repository.TelegramMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TelegramMessageService {

    private final TelegramChatRepository telegramChatRepository;
    private final TelegramMessageRepository telegramMessageRepository;

    @Transactional
    public TelegramMessageEntity saveOrUpdate(
            Long chatId,
            Long telegramMessageId,
            Long senderId,
            String text,
            Boolean outgoing,
            LocalDateTime sentAt,
            String contentType

    ){

        TelegramMessageEntity message = telegramMessageRepository
                .findByChatIdAndTelegramMessageId(
                        chatId,
                        telegramMessageId
                )
                .orElseGet(() -> {
                    TelegramChatEntity chat = telegramChatRepository
                            .findById(chatId)
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Telegram chat not found" + chatId
                                    )
                            );

                    TelegramMessageEntity newMessage = new TelegramMessageEntity();

                    newMessage.setChat(chat);
                    newMessage.setTelegramMessageId(telegramMessageId);
                    return newMessage;
                });

        message.setSenderId(senderId);
        message.setText(text);
        message.setOutgoing(outgoing);
        message.setSentAt(sentAt);
        message.setContentType(contentType);

        return telegramMessageRepository.save(message);

    }
}
