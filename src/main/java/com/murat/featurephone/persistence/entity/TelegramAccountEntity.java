package com.murat.featurephone.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "telegram_account",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_telegram_account_user_telegram",
                        columnNames = {"user_id", "telegram_user_id"}
                )
        }
)
@Getter
@Setter
public class TelegramAccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private  GatewayUserEntity user;

    @Column(name = "telegram_user_id", nullable = false)
    private Long telegramUserId;

    @Column(name = "messages_sent_from_gateway", nullable = false)
    private Long messagesSentFromGateway = 0L;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false)
    private LocalDateTime updatedAt;


}
