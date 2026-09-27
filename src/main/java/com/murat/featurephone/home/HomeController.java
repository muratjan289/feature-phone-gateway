package com.murat.featurephone.home;

import com.murat.featurephone.phone.PhoneTextRenderer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final PhoneTextRenderer phoneTextRenderer;

    @GetMapping("/")
    public String home(Model model) {

        model.addAttribute(
                "phoneText",
                phoneTextRenderer.render(
                        "Привет Мир 😂 ❤️ 👍 😊 😢 🔥 🚀 🥰"
                )
        );

        return "home";
    }
}