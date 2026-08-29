package com.springai.controllers;

import com.springai.services.UserService;
import com.springai.tools.WeatherTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class ToolController {

    private final ChatClient chatClient;
    private final WeatherTool weatherTool;
    private final UserService userService;

    public ToolController(ChatClient.Builder builder, WeatherTool weatherTool, UserService userService) {
        this.chatClient = builder.build();
        this.weatherTool = weatherTool;
        this.userService = userService;
    }

    @GetMapping("/agent")
    public String getWeatherHandler(@RequestParam(name = "query") String query) {
        return chatClient
                .prompt()
                .user(query)
                .tools(weatherTool, userService)
                .call()
                .content();
    }

    @GetMapping("/user")
    public String getUserInfoHandler(@RequestParam(name = "query") String query) {
        return chatClient
                .prompt()
                .user(query)
                .tools(userService)
                .call()
                .content();
    }

}
