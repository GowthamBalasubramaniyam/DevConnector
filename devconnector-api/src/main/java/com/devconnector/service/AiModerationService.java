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

        String url = "https://generativelanguage.googleapis.com/v1/models/gemini-1.5-flash:generateContent?key=" + apiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Sanitize input to prevent JSON injection
        String safeText = postText.replace("\"", "\\\"").replace("\n", " ");

        String requestBody = """
        	    {
        	      "contents": [{
        	        "parts": [{"text": "You are a content filter for a developer social network. 
        	        Analyze if the text is related to software development, programming, IT, or engineering.
        	        Examples:
        	        - 'How to fix null pointer in Java?' -> YES
        	        - 'I just deployed my app to Render' -> YES
        	        - 'I bought cookies' -> NO
        	        - 'The weather is nice' -> NO
        	        
        	        Analyze this text: %s. 
        	        Reply ONLY with 'YES' or 'NO'."}]
        	      }]
        	    }
        	    """.formatted(safeText);

        try {
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            System.out.println("DEBUG: AI Raw Response: " + response.getBody()); // Add this!
            
            if (response.getBody() == null) return true;

            JsonNode root = mapper.readTree(response.getBody());
            
            // Safe navigation: Check if paths exist before calling .get(0)
            JsonNode textNode = root.path("candidates").get(0)
                                    .path("content").path("parts").get(0)
                                    .path("text");

            if (textNode.isMissingNode()) return true;

            return textNode.asText().toUpperCase().contains("YES");
            
        } catch (Exception e) {
            // Fail-open: Log the error but don't block the user if AI service is down
            System.err.println("AI Moderation API unreachable: " + e.getMessage());
            return true; 
        }
    }
}