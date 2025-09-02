package com.jobfit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.MediaType;
import java.util.Collections;

@Configuration
public class RestTemplateConfig {
    
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            return execution.execute(request, body);
        });
        return restTemplate;
    }
}