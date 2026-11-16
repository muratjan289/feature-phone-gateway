package com.murat.featurephone.service;


import com.murat.featurephone.persistence.entity.GatewayUserEntity;
import com.murat.featurephone.persistence.entity.TelegramAccountEntity;
import com.murat.featurephone.persistence.repository.GatewayUserRepository;
import com.murat.featurephone.persistence.repository.TelegramAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TelegramAccountService {

    private final TelegramAccountRepository telegramAccountRepository;
    private final GatewayUserRepository gatewayUserRepository;


    @Transactional
    public TelegramAccountEntity getOrCreateTelegramAccount(Long gatewayUserId, Long telegramUserId) {

        return telegramAccountRepository.
                findByUserIdAndTelegramUserId(gatewayUserId, telegramUserId)
                .orElseGet(() -> {
                        GatewayUserEntity user = gatewayUserRepository
                        .findById(gatewayUserId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Gateway user not found"
                                ));

                        TelegramAccountEntity telegramAccount = new TelegramAccountEntity();
                        telegramAccount.setUser(user);
                        telegramAccount.setTelegramUserId(telegramUserId);

                        return telegramAccountRepository.save(telegramAccount);
    });
    }




}
