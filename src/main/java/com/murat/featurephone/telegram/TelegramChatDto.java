package com.murat.featurephone.telegram;

public record TelegramChatDto(
        long id,
        String title,
        String lastMessage
) {

    public String displayTitle() {

        if (title == null) {
            return "";
        }

        if (title.length() <= 20) {
            return title;
        }

        return title.substring(0, 17) + "...";
    }
}