package com.murat.featurephone.service;

import com.murat.featurephone.persistence.entity.TelegramAccountEntity;
import com.murat.featurephone.persistence.entity.TelegramChatEntity;
import com.murat.featurephone.persistence.repository.TelegramAccountRepository;
import com.murat.featurephone.persistence.repository.TelegramChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class TelegramChatService {

    private final TelegramChatRepository chatRepository;
    private final TelegramAccountRepository accountRepository;



    @Transactional
    public TelegramChatEntity upsert(
            Long accountId,
            Long telegramChatId,
            String title,
            String type,
            Integer unreadCount,
            Boolean archived

    ) {
        TelegramChatEntity chat = chatRepository
                .findByTelegramAccountIdAndTelegramChatId(
                        accountId, telegramChatId
                )
                .orElseGet(() -> {
                    TelegramAccountEntity account = accountRepository
                            .findById(accountId)
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Telegram account not found: " + accountId
                                    )
                            );

                    TelegramChatEntity newChat = new TelegramChatEntity();
                    newChat.setTelegramAccount(account);
                    newChat.setTelegramChatId(telegramChatId);
                    return newChat;
                });

        chat.setTitle(title);
        chat.setType(type);
        chat.setUnreadCount(unreadCount);
        chat.setArchived(archived);
        chat.setActive(true);

        return chatRepository.save(chat);

    }


    @Transactional
    public int deactivateMissingChats(Long accountId, Set<Long> actualChatIds) {

        if(actualChatIds.isEmpty()){
            throw new IllegalStateException(
                    "Cannot reconcile Telegram chats without an empty chat list"
            );
        }

        return chatRepository.deactivateMissingChats(
                accountId, actualChatIds
        );
    }

}


