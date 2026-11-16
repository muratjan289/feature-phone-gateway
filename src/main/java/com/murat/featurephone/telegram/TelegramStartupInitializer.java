package com.murat.featurephone.telegram;


import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TelegramStartupInitializer {

   private final TelegramTdClient telegramTdClient;
   private final ApplicationEventPublisher applicationEventPublisher;

   @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady(){

       if(telegramTdClient.isAuthorized()){
           applicationEventPublisher.publishEvent(
                   new TelegramAuthorizedEvent()
           );
       }
   }

}
