package com.murat.featurephone.persistence.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "telegram_message",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_telegram_message_chat_message",
                        columnNames = {
                                "telegram_chat_id",
                                "telegram_message_id"

                        }

                )

        }

)

@Getter
@Setter
public class TelegramMessageEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;




    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "telegram_chat_id", nullable = false)
    private TelegramChatEntity chat;

    @Column(name = "telegram_message_id", nullable = false)
    private Long telegramMessageId;

    @Column(name = "sender_id")
    private Long senderId;

    @Column(name = "text", columnDefinition = "TEXT")
    private String text;

    @Column(name = "outgoing", nullable = false)
    private Boolean outgoing = false;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime   sentAt;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "content_type", nullable = false, length = 50)
    private String contentType = "TEXT";
}
