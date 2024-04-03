/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.api.cct.backend.controller;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.api.cct.backend.controller.SendMessageRequest.RetrievalOptions;
import com.api.cct.backend.controller.SendMessageRequest.RetrievalOptions.Filters;
import com.api.cct.backend.db.entity.Persona;
import com.api.cct.backend.db.entity.UserChat;
import com.api.cct.backend.db.entity.UserNumber;
import com.api.cct.backend.db.repository.PersonaRepository;
import com.api.cct.backend.db.repository.UserChatRepository;
import com.api.cct.backend.db.repository.UserNumberRepository;
import com.api.cct.backend.db.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/chat")
public class ChatController {

	private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

	private String baseUrl = "https://chitchato.danswer.ai/";

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private PersonaRepository personaRepository;

	@Autowired
	private UserNumberRepository userNumberRepository;

	@Autowired
	private UserChatRepository userChatRepository;

	@Autowired
	private UserRepository userRepository;

	@PostMapping("/create-chat-session")
	public ResponseEntity<String> createChatSession(@RequestBody CreateChatSessionRequest createChatSessionRequest, String fastapiusersauth) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Accept", "application/json");
		headers.set("cookie", "fastapiusersauth=" + fastapiusersauth);

		HttpEntity<CreateChatSessionRequest> entity = new HttpEntity<>(createChatSessionRequest, headers);
		return restTemplate.exchange(baseUrl + "/api/chat/create-chat-session", HttpMethod.POST, entity, String.class);
	}

	@PostMapping("/send-message")
	public ResponseEntity<String> sendMessage(@RequestBody SendMessageRequest sendMessageRequest, String fastapiusersauth) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Accept", "application/json");
		headers.set("cookie", "fastapiusersauth=" + fastapiusersauth);

		HttpEntity<SendMessageRequest> entity = new HttpEntity<>(sendMessageRequest, headers);
		return restTemplate.exchange(baseUrl + "/api/chat/send-message", HttpMethod.POST, entity, String.class);
	}

	@PostMapping("/send")
	public ResponseEntity<?> send(@RequestBody SendRequest sendRequest) throws JsonMappingException, JsonProcessingException {
		// Initialize ObjectMapper for JSON parsing
    	ObjectMapper objectMapper = new ObjectMapper();

		// Initialize username
		// String username = jwt.getClaimAsString("preferred_username");
		String username = sendRequest.getUsername();
		logger.info("username: {}", username);

		// Initialize fastapiusersauth
		String fastapiusersauth = userRepository.findByUsername(username).get().getFastapiusersauth();

		// Check is there any Persona exist with this user
		Persona personaExample = new Persona();
		personaExample.setCreatedBy(username);
		Optional<Persona> personaOptional = personaRepository.findOne(Example.of(personaExample));
		if (personaOptional.isPresent()) {
			// Check number is not exist
			UserNumber userNumberExample = new UserNumber();			
			userNumberExample.setCreatedBy(username);
			userNumberExample.setNo(sendRequest.getNo()); // Format Example : 6281380782318
			Optional<UserNumber> userNumberOptional = userNumberRepository.findOne(Example.of(userNumberExample));

			if (!userNumberOptional.isPresent()) {
				// 1. Create Chat Session
				logger.info("1. Create Chat Session");
				CreateChatSessionRequest createChatSessionRequest = new CreateChatSessionRequest();
				createChatSessionRequest.setPersonaId(personaOptional.get().getPersonaId());
				
				ResponseEntity<String> createChatSessionResponse = createChatSession(createChatSessionRequest, fastapiusersauth);
				if (!createChatSessionResponse.getStatusCode().is2xxSuccessful()) {
					return ResponseEntity.status(createChatSessionResponse.getStatusCode()).body("Failed to create chat session");
				}
				// Extract credential ID from createChatSessionResponse
				JsonNode createChatSessionNode = objectMapper.readTree(createChatSessionResponse.getBody());
				int chatSessionId = createChatSessionNode.path("chat_session_id").asInt();  
				logger.info("1. Create Chat Session. chatSessionId {}", chatSessionId);

				// Save number
				// Create a Date instance
				Date now = new Date();
				// Convert Date to LocalDateTime
				ZonedDateTime zdt = now.toInstant().atZone(ZoneId.systemDefault());
				LocalDateTime localDateTime = zdt.toLocalDateTime();

				UserNumber userNumber = new UserNumber();
				userNumber.setUuid(UUID.randomUUID().toString());
				userNumber.setCreatedBy(username);
				userNumber.setCreatedAt(localDateTime);
				userNumber.setNo(sendRequest.getNo());
				userNumber.setChatSessionId(chatSessionId);
				userNumberRepository.save(userNumber);

				// 2. Send Message
				logger.info("2. Send Message");
				SendMessageRequest sendMessageRequest = new SendMessageRequest();
				sendMessageRequest.setChatSessionId(chatSessionId);
				sendMessageRequest.setMessage(sendRequest.getMessage());
				sendMessageRequest.setParentMessageId(null);
				sendMessageRequest.setPromptId(personaOptional.get().getPromptId());
				RetrievalOptions retrievalOptions = new RetrievalOptions();
				Filters filters = new Filters();
				filters.setDocumentSet(null);
				filters.setSourceType(null);
				filters.setTags(new String[0]);
				filters.setTimeCutoff(null);
				retrievalOptions.setFilters(filters);
				retrievalOptions.setRealTime(false);
				retrievalOptions.setRunSearch("auto");
				sendMessageRequest.setRetrievalOptions(retrievalOptions);
				sendMessageRequest.setSearchDocIds(null);
				
				ResponseEntity<String> sendMessageResponse = sendMessage(sendMessageRequest, fastapiusersauth);
				if (!sendMessageResponse.getStatusCode().is2xxSuccessful()) {
					return ResponseEntity.status(sendMessageResponse.getStatusCode()).body("Failed to send message");
				}
				// Extract credential ID from createChatSessionResponse
				String jsonArray[] = sendMessageResponse.getBody().split("\n");
				JsonNode sendMessageNode = objectMapper.readTree(jsonArray[jsonArray.length - 1]);
				int messageId = sendMessageNode.path("message_id").asInt();
				int parentMessageId = sendMessageNode.path("parent_message").asInt();
				String message = sendMessageNode.path("message").asText();
				logger.info("2. Send Message. messageId {} message {}", messageId, message);

				// Save chat
				UserChat userChat = new UserChat();
				userChat.setUuid(UUID.randomUUID().toString());
				userChat.setCreatedBy(username);
				userChat.setCreatedAt(localDateTime);
				userChat.setUserNumber(sendRequest.getNo());
				userChat.setMessageIn(sendRequest.getMessage());
				userChat.setMessageOut(message);
				userChat.setMessageId(messageId);
				userChat.setParentMessageId(parentMessageId);
				userChatRepository.save(userChat);

				// Return message
				return ResponseEntity.status(HttpStatus.OK).body(message);
			} else {
				// 1. Get Chat Session ID
				logger.info("1. Get Chat Session ID");
				int chatSessionId = userNumberOptional.get().getChatSessionId();
				logger.info("1. Get Chat Session ID. chatSessionId {}", chatSessionId);

				// Create a Date instance
				Date now = new Date();
				// Convert Date to LocalDateTime
				ZonedDateTime zdt = now.toInstant().atZone(ZoneId.systemDefault());
				LocalDateTime localDateTime = zdt.toLocalDateTime();

				// 2. Send Message
				logger.info("2. Send Message");
				UserChat userChatLast = userChatRepository.findOneByCreatedByAndUserNumberOrderByMessageIdDesc(username, sendRequest.getNo()).get(0);

				SendMessageRequest sendMessageRequest = new SendMessageRequest();
				sendMessageRequest.setChatSessionId(chatSessionId);
				sendMessageRequest.setMessage(sendRequest.getMessage());
				sendMessageRequest.setParentMessageId(userChatLast.getMessageId());
				sendMessageRequest.setPromptId(personaOptional.get().getPromptId());
				RetrievalOptions retrievalOptions = new RetrievalOptions();
				Filters filters = new Filters();
				filters.setDocumentSet(null);
				filters.setSourceType(null);
				filters.setTags(new String[0]);
				filters.setTimeCutoff(null);
				retrievalOptions.setFilters(filters);
				retrievalOptions.setRealTime(false);
				retrievalOptions.setRunSearch("auto");
				sendMessageRequest.setRetrievalOptions(retrievalOptions);
				sendMessageRequest.setSearchDocIds(null);
				
				ResponseEntity<String> sendMessageResponse = sendMessage(sendMessageRequest, fastapiusersauth);
				if (!sendMessageResponse.getStatusCode().is2xxSuccessful()) {
					return ResponseEntity.status(sendMessageResponse.getStatusCode()).body("Failed to send message");
				}
				// Extract credential ID from createChatSessionResponse
				String jsonArray[] = sendMessageResponse.getBody().split("\n");
				JsonNode sendMessageNode = objectMapper.readTree(jsonArray[jsonArray.length - 1]);
				int messageId = sendMessageNode.path("message_id").asInt();
				int parentMessageId = sendMessageNode.path("parent_message").asInt();
				String message = sendMessageNode.path("message").asText();
				logger.info("2. Send Message. messageId {} message {}", messageId, message);

				// Save chat
				UserChat userChat = new UserChat();
				userChat.setUuid(UUID.randomUUID().toString());
				userChat.setCreatedBy(username);
				userChat.setCreatedAt(localDateTime);
				userChat.setUserNumber(sendRequest.getNo());
				userChat.setMessageIn(sendRequest.getMessage());
				userChat.setMessageOut(message);
				userChat.setMessageId(messageId);
				userChat.setParentMessageId(parentMessageId);
				userChatRepository.save(userChat);

				// Return message
				return ResponseEntity.status(HttpStatus.OK).body(message);
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No persona found for " + username);
		}		
	}
}
