package com.jobfit.service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

@Service
public class HuggingFaceService {
    
    @Value("${huggingface.api.url}")
    private String apiUrl;
    
    @Value("${huggingface.api.token}")
    private String apiToken;
    
    @Value("${huggingface.model.similarity}")
    private String similarityModel;
    
    @Value("${huggingface.model.ner}")
    private String nerModel;
    
    @Autowired
    private RestTemplate restTemplate;

    // private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public float calculateSemanticSimilarity(String text1, String text2) {
        try {
            // Truncate very long texts to avoid API limits
            if (text1.length() > 1000) text1 = text1.substring(0, 1000);
            if (text2.length() > 1000) text2 = text2.substring(0, 1000);

            String url = apiUrl + "/" + similarityModel;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiToken);

            // Use LinkedHashMap to preserve field order
            Map<String, Object> inputs = new LinkedHashMap<>();
            inputs.put("source_sentence", text1);
            inputs.put("sentences", List.of(text2));

            Map<String, Object> request = new HashMap<>();
            request.put("inputs", inputs);

            String requestBody = objectMapper.writeValueAsString(request);
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                float[] scores = objectMapper.readValue(response.getBody(), float[].class);
                return scores.length > 0 ? scores[0] : 0.0f;
            }
        } catch (Exception e) {
            System.err.println("Hugging Face similarity API failed: " + e.getMessage());
            // Fallback to Jaccard similarity
            return calculateJaccardSimilarity(text1, text2);
        }
        return 0.0f;
    }

    
    public Map<String, List<String>> extractEntities(String text) {
        Map<String, List<String>> entities = new HashMap<>();
        try {
            String url = apiUrl + "/" + nerModel;
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiToken);
            
            Map<String, String> request = new HashMap<>();
            request.put("inputs", text);
            
            String requestBody = objectMapper.writeValueAsString(request);
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                // Parse the NER response
                List<Map<String, Object>> nerResults = objectMapper.readValue(
                    response.getBody(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class)
                );
                
                for (Map<String, Object> entityMap : nerResults) {
                    String word = (String) entityMap.get("word");
                    String entityGroup = (String) entityMap.get("entity_group");
                    
                    entities.computeIfAbsent(entityGroup, k -> new ArrayList<>()).add(word);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entities;
    }


    private float calculateJaccardSimilarity(String text1, String text2) {
    // Simple word-based similarity as fallback
    Set<String> words1 = new HashSet<>(Arrays.asList(text1.toLowerCase().split("\\s+")));
    Set<String> words2 = new HashSet<>(Arrays.asList(text2.toLowerCase().split("\\s+")));
    
    Set<String> intersection = new HashSet<>(words1);
    intersection.retainAll(words2);
    
    Set<String> union = new HashSet<>(words1);
    union.addAll(words2);
    
    return union.isEmpty() ? 0 : (float) intersection.size() / union.size();
}
}
