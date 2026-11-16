package com.murat.featurephone.persistence.repository;

import com.murat.featurephone.persistence.entity.TelegramChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TelegramChatRepository extends JpaRepository<TelegramChatEntity, Long> {


    Optional<TelegramChatEntity> findByTelegramAccountIdAndTelegramChatId(Long telegramAccountId, Long telegramChatId);

    List<TelegramChatEntity> findAllByTelegramAccountId(Long telegramAccountId);
}
