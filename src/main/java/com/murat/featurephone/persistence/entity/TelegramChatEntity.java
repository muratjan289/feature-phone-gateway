package com.murat.featurephone.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "telegram_chat",
        uniqueConstraints = {
        @UniqueConstraint(

                name = "uq_telegram_chat_account_chat",
        columnNames = {"telegram_account_id", "telegram_chat_id"}
        )
                }
)
@Getter
@Setter
public class TelegramChatEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "telegram_account_id", nullable = false)
        private TelegramAccountEntity telegramAccount;

        @Column(name = "telegram_chat_id", nullable = false)
        private Long telegramChatId;

        @Column(name = "title", nullable = false, length = 255)
        private String title;

        @Column(name = "type", nullable = false, length = 50)
        private String type;

        @Column(name = "unread_count", nullable = false)
        private Integer unreadCount;

        @Column(name = "archived", nullable = false)
        private Boolean archived = false;

        @Column(name = "is_active", nullable = false)
        private boolean active = true;

        @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
        private LocalDateTime createdAt;

        @Column(name = "updated_at", nullable = false, insertable = false)
        private LocalDateTime updatedAt;
}
