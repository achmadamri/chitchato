package com.api.cct.backend.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
public class MessagingController {

    @Autowired
	private RestTemplate restTemplate;

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
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", "ZLzXYm4cUtyk4Acxs16Y");

        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("target", target);
        map.add("message", (String) data.get("message"));
        map.add("url", (String) data.get("url"));
        map.add("filename", (String) data.get("filename"));
        
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        String response = restTemplate.postForObject("https://api.fonnte.com/send", request, String.class);

        System.out.println(response);
    }
}

