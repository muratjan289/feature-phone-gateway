package com.murat.featurephone.telegram;

import com.murat.featurephone.phone.PhoneTextRenderer;
import lombok.RequiredArgsConstructor;
import org.drinkless.tdlib.TdApi;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TelegramService {

    private static final int MAX_MESSAGE_LENGTH = 500;
    private static final int LIMIT_MESSAGES_IN_CHAT = 20;

    private final TelegramTdClient telegramTdClient;
    private final PhoneTextRenderer phoneTextRenderer;


    public List<TelegramChatDto> getChats() {

        try {
            long [] chatsId = telegramTdClient.getChatIds()
                    .get(5,TimeUnit.SECONDS);

            List<TelegramChatDto> chats = new ArrayList<>();

            for(long chatId : chatsId) {
                TdApi.Chat chat  = telegramTdClient.getChat(chatId)
                        .get(5, TimeUnit.SECONDS);

                TelegramChatDto chatDto = new TelegramChatDto(
                        chat.id,
                        phoneTextRenderer.render(chat.title),
                        ""
                );
                chats.add(chatDto);
            }
            return chats;

        }
        catch (Exception e) {
            throw new RuntimeException(
                    "Failed to load Telegram chats",e);
        }


    }


   public TelegramChatDto getChatById(long id) {

        try {
            TdApi.Chat chat = telegramTdClient.getChat(id)
                .get(5,TimeUnit.SECONDS);

            return new TelegramChatDto(
                    chat.id,
                    phoneTextRenderer.render(chat.title),
                    ""
            );

        }catch (Exception e){
            throw new RuntimeException(
                    "Failed to load Telegram chat" + id, e
            );
        }

    }

    public List <TelegramMessageDto> getMessagesByChatId(long chatId) {

        try {
            TdApi.Messages history = telegramTdClient
                    .getChatHistory(chatId, LIMIT_MESSAGES_IN_CHAT)
                    .get(5, TimeUnit.SECONDS);


            List<TelegramMessageDto> result = new ArrayList<>();

            for(TdApi.Message message : history.messages) {
                if(message.content instanceof TdApi.MessageText messageText){
                    String text = messageText.text.text;

                    TelegramMessageDto telegramMessageDto = new TelegramMessageDto(
                            message.id,
                            message.chatId,
                            message.isOutgoing
                                 ?"Me"
                                    :"Telegram",
                            phoneTextRenderer.render(text),
                            message.isOutgoing
                    );

                    result.add(telegramMessageDto);
                }
            }
            result = result.reversed();

            return result;
        }
        catch (Exception e){
            throw new RuntimeException(
                    "Failed to load Telegram chats" + chatId, e
            );
        }

    }

    public void sendMessage(long chatId, String text) {

        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException(
                    "Message must not be null or empty"
            );
        }
        String normalizedText = text.trim();

        if (normalizedText.length() > MAX_MESSAGE_LENGTH) {
            throw new IllegalArgumentException("Message is too long");
        }
        try {
            telegramTdClient.sendMessage(chatId, normalizedText)
                    .get(5, TimeUnit.SECONDS);
        }catch (Exception e){
            throw new RuntimeException(
                    "Failed to send message",
                    e
            );
        }
    }

    public TdApi.User getMe(){

        try {
            return telegramTdClient
                    .getMe()
                    .get(5, TimeUnit.SECONDS);
        }
        catch (Exception e) {
            throw new RuntimeException( "Failed to get Telegram user", e);
        }
    }



}
