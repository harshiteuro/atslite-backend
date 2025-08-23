package com.harshit.tldr.controller.publicController;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OpenAiLlm {
    private final OpenAiService openAiService;

    public OpenAiLlm(@Value("${openai.api.key}") String apiKey) {
        this.openAiService = new OpenAiService(apiKey);
    }

    public String analyzeUsingOpenAi(String model, String prompt) {
        ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                .model(model) // or gpt-3.5-turbo
                .messages(List.of(new ChatMessage("user", prompt)))
                .temperature(0.0)
                .build();

        ChatCompletionResult result = openAiService.createChatCompletion(completionRequest);

        return result.getChoices().get(0).getMessage().getContent();
    }

}
