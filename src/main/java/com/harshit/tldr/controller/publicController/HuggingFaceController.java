package com.harshit.tldr.controller.publicController;

import com.harshit.tldr.pojo.AnalysisRequest;
import com.harshit.tldr.pojo.ChatRequest;
import com.harshit.tldr.pojo.ChatResponse;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@RestController
@RequestMapping("/ats")
public class HuggingFaceController {

    private static final String chatAppUrl = "https://atslite-ai.onrender.com/chat";

    private final RestTemplate restTemplate = new RestTemplate();

    private final OpenAiLlm openAiLlm;

    public HuggingFaceController(OpenAiLlm openAiLlm) {
        this.openAiLlm = openAiLlm;
    }

    @PostMapping("/analyse")
    public ResponseEntity<?> evaluateResume(@RequestBody AnalysisRequest request){
        // Build the ATS analysis prompt
        String prompt = "Analyze the ATS score for this resume against the job description.\n\n" +
                "Resume:\n" + request.getResumeContent() + "\n\n" +
                "Job Description:\n" + request.getJobDescription() + "\n\n" +
                "Provide ATS score (0-100) and explain strengths and weaknesses.";


        if(request.getModel().equals("gpt-4o-mini")){
            String res= openAiLlm.analyzeUsingOpenAi(request.getModel(),prompt);
            return new ResponseEntity<>(res,HttpStatus.OK);
        }

        // Create request body for external API
        ChatRequest chatRequest = new ChatRequest(
                request.getModel(),
                Collections.singletonList(new ChatRequest.Message("user", prompt))
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ChatRequest> entity = new HttpEntity<>(chatRequest, headers);

        ResponseEntity<ChatResponse> response = restTemplate.exchange(
                chatAppUrl, HttpMethod.POST, entity, ChatResponse.class);

        if (response.getBody() != null){
            return response;
        } else {
            return new ResponseEntity<>("No response from AI service.",HttpStatus.BAD_REQUEST);
        }
    }
}
