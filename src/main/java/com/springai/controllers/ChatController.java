package com.springai.controllers;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class ChatController {

    private final ChatClient chatClient;

    @Value("classpath:/prompts/prompt1.st")
    private Resource promptResource;

    public ChatController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

//    Your Controller handler
//        → ChatClient.prompt().user("...").call()
//            → Spring AI builds a Prompt object
//                → OpenAiChatModel sends HTTP POST to api.openai.com/v1/chat/completions
//                    → Response is parsed into ChatResponse
//                        → .content() extracts the text
//
    @GetMapping("/ask")
    public String ask() {
        return chatClient
                .prompt()
                .user("Hey, what is Spring AI?")
                .call()
                .content();
    }

    // streaming response
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream() {
        return chatClient
                .prompt()
                .user("Hey, what is Spring AI?")
                .stream()
                .content();
    }


    // prompt templates
    // inject some variables, and change prompt dynamically

    @GetMapping("/ask2")
    public String ask2(@RequestParam String domain, @RequestParam String question) {
        String promptTemplate = """
                You are a world-class expert in {domain}.
                Answer the following question clearly and concisely.
                
                Question: {question}
                """;

        return chatClient
                .prompt()
                .user(u -> u.text(promptTemplate)
                            .param("domain", domain)
                            .param("question", question))
                .call()
                .content();
    }

    // PromptTemplate class
    @GetMapping("/ask3")
    public String ask3(@RequestParam String domain, @RequestParam String question) {
        PromptTemplate promptTemplate = new PromptTemplate(promptResource);

        Prompt prompt = promptTemplate.create(Map.of("domain", domain, "question", question));

        return chatClient
                .prompt(prompt)
                .call()
                .content();
    }
}
