package com.example.webhook.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/webhook")
public class WebhookController {
    @PostMapping("/send")
    public ResponseEntity<String> sendWebhook(
            @RequestParam String url,
            @RequestBody String payload
    ) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(payload, headers);
        String response = restTemplate.postForObject(url, entity, String.class);

        return ResponseEntity.ok("Sent! Response: " + response);
    }
}
