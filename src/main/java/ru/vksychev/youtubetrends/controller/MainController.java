package ru.vksychev.youtubetrends.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/")
public class MainController {

    @Value("${spring.profiles.active")
    private String profile;

    @GetMapping
    public String index(Model model) {

        Map<Object, Object> data = new HashMap<>();

        data.put("profile", "123");

        model.addAttribute("frontendData", data);
        model.addAttribute("isDevMode", "dev".equals(profile));
        return "index";
    }
}