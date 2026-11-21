package com.murat.featurephone.telegram;

import lombok.extern.slf4j.Slf4j;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class TelegramTdClient {

    private final Client client;
    private final TelegramProperties properties;
    private final ApplicationEventPublisher eventPublisher;
    private volatile boolean authorized = false;


    public TelegramTdClient(TelegramProperties properties, ApplicationEventPublisher eventPublisher) throws Client.ExecutionException {
        this.properties = properties;
        this.eventPublisher = eventPublisher;

        Client.execute(new TdApi.SetLogVerbosityLevel(1));
        this.client = Client.create(
                this::handleUpdate,
                null,
                null

        );

    }




    private void handleUpdate(TdApi.Object object) {
        if (object instanceof TdApi.UpdateAuthorizationState update) {

            System.out.println(
                    "Authorization state: " + update.authorizationState
            );
            TdApi.AuthorizationState authorizationState = update.authorizationState;

            if(authorizationState instanceof TdApi.AuthorizationStateWaitTdlibParameters){
                sendTdLibParameters();
            }

            if (authorizationState
                    instanceof TdApi.AuthorizationStateWaitPhoneNumber) {
                System.out.println("TDLib is waiting for phone number");
            }

            if (authorizationState instanceof TdApi.AuthorizationStateWaitCode) {
                System.out.println("TDLib is waiting for authentication code");
            }

            if (authorizationState instanceof TdApi.AuthorizationStateWaitPassword) {
                System.out.println("TDLib is waiting for 2FA password");
            }

            if (authorizationState instanceof TdApi.AuthorizationStateReady) {
                authorized = true;
                System.out.println("Telegram authorization READY");
                eventPublisher.publishEvent(
                        new TelegramAuthorizedEvent()
                );
            }
        }
    }

    private void sendTdLibParameters() {

        TdApi.SetTdlibParameters request =
                new TdApi.SetTdlibParameters();
        request.databaseDirectory = "tdlib-data";
        request.useMessageDatabase = true;
        request.useSecretChats = false;
        request.apiId = properties.apiId();
        request.apiHash = properties.apiHash();
        request.systemLanguageCode = "en";
        request.deviceModel = "Feature Phone Gateway";
        request.applicationVersion = "1.0";
        client.send(request, result -> {


        });

    }

    public void setPhoneNumber(String phoneNumber) {

        TdApi.SetAuthenticationPhoneNumber request =
                new TdApi.SetAuthenticationPhoneNumber();
        request.phoneNumber = phoneNumber.trim();
        client.send(request, result -> {
        });
    }

    public void checkCode(String code) {
        TdApi.CheckAuthenticationCode request =
                new TdApi.CheckAuthenticationCode();
        request.code = code.trim();
        client.send(request, result -> {

        }
        );
    }

    public CompletableFuture<TdApi.User> getMe(){

        CompletableFuture<TdApi.User> future = new CompletableFuture<>();

        client.send(new TdApi.GetMe(), result ->{
            if(result instanceof TdApi.User user){
                future.complete(user);
                return;
            }

            if (result instanceof TdApi.Error error) {
                future.completeExceptionally(
                        new RuntimeException(
                        "TDLib error" + error.code + ": " + error.message
                        )
                );
                return;
            }
            future.completeExceptionally(
                    new IllegalStateException(
                            "Unexpected TDLib response: "
                            + result.getClass().getSimpleName()
                    )
            );
        });
        return future;
    }

    public CompletableFuture<long[]> getChatIds(
            TdApi.ChatList chatList,
            int limit
    ){
        CompletableFuture<long[]> future = new CompletableFuture<>();

        TdApi.GetChats request = new TdApi.GetChats();

        request.chatList = chatList;
        request.limit = limit;
        log.info(
                "TDLib GetChats request: list={}, limit={}",
                request.chatList.getClass().getSimpleName(),
                request.limit
        );

        client.send(request,result ->{
            if(result instanceof TdApi.Chats chats ){
                future.complete(chats.chatIds);
                return;
            }
            if(result instanceof TdApi.Error error){
                future.completeExceptionally(
                        new RuntimeException(
                                "TDLib error" + error.code + ": " + error.message
                        )
                );
                return;
            }
            future.completeExceptionally(
                    new IllegalStateException(
                            "Unexpected TDLib response: "
                            + result.getClass().getSimpleName()
                    )
            );
        });

        return future;
    }

    public CompletableFuture<long[]> getChatIds() {
        return getChatIds(new TdApi.ChatListMain(), 10);
    }

    public CompletableFuture<Boolean> loadChats(
            TdApi.ChatList chatList,
            int limit
    ) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        TdApi.LoadChats request = new TdApi.LoadChats();
        request.chatList = chatList;
        request.limit = limit;
        client.send(request, result -> {

            if (result instanceof TdApi.Ok) {
                future.complete(true);
                return;
            }

            if (result instanceof TdApi.Error error) {

                if (error.code == 404) {
                    future.complete(false);
                    return;
                }

                future.completeExceptionally(
                        new RuntimeException(
                                "TDLib LoadChats error "
                                        + error.code + ": "
                                        + error.message
                        )
                );
                return;
            }
            future.completeExceptionally(
                    new IllegalStateException(
                            "Unexpected TDLib response: "
                                    + result.getClass().getSimpleName()
                    )
            );
        });

        return future;
    }





    public CompletableFuture<TdApi.Chat> getChat(long chatId){
        CompletableFuture<TdApi.Chat> future = new CompletableFuture<>();

        client.send(new TdApi.GetChat(chatId), result ->{
            if(result instanceof TdApi.Chat chat){
                future.complete(chat);
                return;
            }

            if (result instanceof TdApi.Error error) {
                future.completeExceptionally(
                        new RuntimeException(
                                "TDLib error" + error.code + ": " + error.message
                        )
                );
                return;
            }
            future.completeExceptionally(
                    new IllegalStateException(
                            "Unexpected TDLib response: "+
                                    result.getClass().getSimpleName()
                    )
            );
        });
        return future;
    }

    public CompletableFuture<TdApi.Messages> getChatHistory(
            long chatId,
            int limit
    ){
        CompletableFuture<TdApi.Messages> future = new CompletableFuture<>();

        TdApi.GetChatHistory request = new TdApi.GetChatHistory();

        request.chatId = chatId;
        request.fromMessageId = 0;
        request.limit = limit;
        request.onlyLocal = false;

        client.send(request, result-> {
            if(result instanceof TdApi.Messages messages){
                future.complete(messages);
                return;
            }

            if (result instanceof TdApi.Error error) {
                future.completeExceptionally(
                        new RuntimeException(
                                "TDLib error" + error.code + ": " + error.message
                        )
                );
                return;
            }
            future.completeExceptionally(
                    new IllegalStateException(
                            "Unexpected TDLib response: "
                            + result.getClass().getSimpleName()
                    )
            );
        });
        return future;
    }

    public CompletableFuture<TdApi.Message> sendMessage(
            long chatId,
            String text
    ){
        CompletableFuture<TdApi.Message> future = new CompletableFuture<>();

        TdApi.InputMessageText inputMessageText = new TdApi.InputMessageText();

        inputMessageText.text = new TdApi.FormattedText(
                text, new TdApi.TextEntity[0]
        );

        TdApi.SendMessage request = new TdApi.SendMessage();

        request.chatId = chatId;

        request.inputMessageContent = inputMessageText;

        client.send(request, result -> {
            if(result instanceof TdApi.Message message){
                future.complete(message);

                return;
            }

            if (result instanceof TdApi.Error error) {
                future.completeExceptionally(
                        new RuntimeException(
                                "TDLib error" + error.code + ": " + error.message
                        )
                );
                return;
            }

            future.completeExceptionally(
                    new IllegalStateException(
                            "Unexpected TDLib response: "
                            + result.getClass().getSimpleName()
                    )
            );
        });

        return future;
    }

    public boolean isAuthorized() {
        return authorized;
    }



    }





