package com.murat.featurephone.persistence.repository;

import com.murat.featurephone.persistence.entity.TelegramChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TelegramChatRepository extends JpaRepository<TelegramChatEntity, Long> {


    Optional<TelegramChatEntity> findByTelegramAccountIdAndTelegramChatId(Long telegramAccountId, Long telegramChatId);

    List<TelegramChatEntity> findAllByTelegramAccountId(Long telegramAccountId);

    @Modifying
    @Query("""
    UPDATE TelegramChatEntity c
    SET c.active = false
    WHERE c.telegramAccount.id = :accountId
      AND c.active = true
      AND c.telegramChatId NOT IN :actualChatIds
    """)
    int deactivateMissingChats(
            @Param("accountId") Long accountId,
            @Param("actualChatIds") Collection<Long> actualChatIds
    );
}
