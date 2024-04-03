package com.api.cct.backend.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
public class MessagingController {

    @PostMapping("/send")
    public void sendMessage(@RequestBody Map<String, Object> data) {
        String device = (String) data.get("device");
        String sender = (String) data.get("sender");
        String message = (String) data.get("message");
        // and so on for the other fields

        Map<String, Object> reply = new HashMap<>();

        if ("test".equals(message)) {
            reply.put("message", "working great!");
        } else if ("image".equals(message)) {
            reply.put("message", "image message");
            reply.put("url", "https://filesamples.com/samples/image/jpg/sample_640×426.jpg");
        } // Add other conditions here

        sendFonnte(sender, reply);
    }

    private void sendFonnte(String target, Map<String, Object> data) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", "TOKEN");

        Map<String, String> map = new HashMap<>();
        map.put("target", target);
        map.put("message", (String) data.get("message"));
        map.put("url", (String) data.get("url"));
        map.put("filename", (String) data.get("filename"));
        // and so on for other fields

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(map, headers);

        String response = restTemplate.postForObject("https://api.fonnte.com/send", entity, String.class);
        System.out.println("Response from Fonnte: " + response);
    }
}

