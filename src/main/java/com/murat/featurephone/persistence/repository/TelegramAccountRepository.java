package com.murat.featurephone.persistence.repository;

import com.murat.featurephone.persistence.entity.TelegramAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TelegramAccountRepository extends JpaRepository<TelegramAccountEntity, Long> {

    List<TelegramAccountEntity> findAllUserById(Long userId);

    Optional<TelegramAccountEntity> findByUserIdAndTelegramUserId(
            Long userId, Long telegramAccountId);






}
