package com.murat.featurephone.telegram;

public record TelegramMessageDto (

        long id,
        long chatId,
        String sender,
        String text,
        boolean outgoing
){
}
