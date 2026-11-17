package com.murat.featurephone.persistence.repository;


import com.murat.featurephone.persistence.entity.TelegramMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TelegramMessageRepository  extends JpaRepository<TelegramMessageEntity, Long> {


    Optional<TelegramMessageEntity> findByChatIdAndTelegramMessageId(Long chatId, Long telegramMessageId);

    List<TelegramMessageEntity> findTop20ByChatIdOrderByTelegramMessageIdDesc(Long chatId);


}
