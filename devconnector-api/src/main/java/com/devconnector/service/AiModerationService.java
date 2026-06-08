package com.devconnector.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AiModerationService {

    @Value("${gemini.api.key}")
    private String apiKey;

    public boolean isTechRelated(String postText) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Escape double quotes in user text to prevent JSON breaking
        String safeText = postText.replace("\"", "\\\"");

        // Strict prompt using Java 17 Text Blocks
        String requestBody = """
            {
              "contents": [{
                "parts": [{"text": "You are a strict content moderator for a software developer community. Analyze this text. Is it related to software development, programming, computer science, technology, or IT careers? Reply strictly with the single word 'YES' or 'NO'. Text: %s"}]
              }]
            }
            """.formatted(safeText);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            
            // Parse the JSON response to grab the AI's actual text
            ObjectMapper mapper = new ObjectMapper(); 
            JsonNode root = mapper.readTree(response.getBody());
            String aiAnswer = root.path("candidates").get(0)
                                  .path("content").path("parts").get(0)
                                  .path("text").asText().trim().toUpperCase();

            return aiAnswer.contains("YES");
             
        } catch (Exception e) { 
            System.err.println("AI Moderation Error: " + e.getMessage());
            return true; 
        }
    }
}