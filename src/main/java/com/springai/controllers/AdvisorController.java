package com.springai.controllers;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class AdvisorController {

    private final ChatClient chatClient;

    public AdvisorController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/advise")
    public String ask(@RequestParam String message) {
        return chatClient
                .prompt()
                .user(message)
                .call()
                .content();
    }
}
