package com.murat.featurephone.telegram;


import lombok.RequiredArgsConstructor;
import org.drinkless.tdlib.TdApi;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Controller
@RequiredArgsConstructor
public class TelegramController {

    private final TelegramService telegramService;
    private final TelegramTdClient telegramTdClient;


    @GetMapping("/telegram")
    public String telegram(Model model) {

        List<TelegramChatDto> chats = telegramService.getChats();
        model.addAttribute("chats", chats);
        return "telegram";

    }


    @GetMapping("/telegram/chats/{id}")
    public String chat(@PathVariable long id, Model model){

        TelegramChatDto chat = telegramService.getChatById(id);
       List<TelegramMessageDto> messages = telegramService.getMessagesByChatId(id);

        model.addAttribute("chat", chat);
        model.addAttribute("messages", messages);

        return "telegram-chat";

    }


    @PostMapping("/telegram/chats/{id}/messages")
    public  String sendMessage(@PathVariable long id,
                               @RequestParam String text){

        telegramService.sendMessage(id, text);
        return "redirect:/telegram/chats/" + id;
    }


    @GetMapping("telegram/auth")
    public String authPage(){
        return "telegram-auth";
    }

    @PostMapping("telegram/auth/phone")
        public String submitPhone(@RequestParam String phone) {
            telegramTdClient.setPhoneNumber(phone);

            return "redirect:/telegram/auth";
        }

        @PostMapping("telegram/auth/code")
        public String submitCode(@RequestParam String code) {

        telegramTdClient.checkCode(code);
        return "redirect:/telegram/auth";
        }

        @GetMapping("telegram/me")
        @ResponseBody
        public String me(){
            TdApi.User user = telegramService.getMe();

            return "Telegram user: "
                    +user.firstName
                    +" "+user.lastName
                    +", id: "
                    + user.id;
        }




    }



