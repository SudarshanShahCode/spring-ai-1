package com.springai.controllers;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai/rag")
public class RagController {

    private final ChatClient chatClient;

    public RagController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/chat")
    public String chat(
            @RequestParam String message,
            @RequestParam String conversationId) {

        return chatClient
                .prompt()
                .user(message)
                .advisors(a -> a.param(
                        ChatMemory.CONVERSATION_ID, conversationId
                ))
                .call()
                .content();
    }
}
