package com.devconnector.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Service
public class AiModerationService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    public AiModerationService() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000); // 3 seconds
        factory.setReadTimeout(5000);    // 5 seconds
        this.restTemplate = new RestTemplate(factory);
    }

    public boolean isTechRelated(String postText) {
        if (postText == null || postText.isBlank()) return false;

        // EXACT URL FROM YOUR JSON PAYLOAD
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=" + apiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String safeText = postText.replace("\"", "\\\"").replace("\n", " ");

        String requestBody = """
            {
              "contents": [{
                "parts": [{"text": "You are a content filter for a developer social network. Analyze if the text is related to software development, programming, IT, or engineering. Examples: 'How to fix null pointer in Java?' -> YES. 'I bought cookies' -> NO. Analyze this text: %s. Reply ONLY with 'YES' or 'NO'."}]
              }]
            }
            """.formatted(safeText);

        try {
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            
            if (response.getBody() == null) return true;

            JsonNode root = mapper.readTree(response.getBody());
            
            JsonNode textNode = root.path("candidates").get(0)
                                    .path("content").path("parts").get(0)
                                    .path("text");

            if (textNode.isMissingNode()) return true;

            String aiAnswer = textNode.asText().toUpperCase().trim();
            System.out.println("DEBUG: Raw AI Response text: " + aiAnswer);

            return aiAnswer.contains("YES");
            
        } catch (Exception e) {
            System.err.println("AI Moderation API Error: " + e.getMessage());
            return true; 
        }
    }
}