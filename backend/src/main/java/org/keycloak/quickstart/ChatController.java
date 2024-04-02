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
package org.keycloak.quickstart;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.keycloak.quickstart.db.entity.Connector;
import org.keycloak.quickstart.db.entity.DocumentSet;
import org.keycloak.quickstart.db.entity.Persona;
import org.keycloak.quickstart.db.entity.Prompt;
import org.keycloak.quickstart.db.entity.UserNumber;
import org.keycloak.quickstart.db.repository.ConfigRepository;
import org.keycloak.quickstart.db.repository.ConnectorRepository;
import org.keycloak.quickstart.db.repository.DocumentSetRepository;
import org.keycloak.quickstart.db.repository.PersonaRepository;
import org.keycloak.quickstart.db.repository.PromptRepository;
import org.keycloak.quickstart.db.repository.UserNumberRepository;
import org.keycloak.quickstart.request.CreateChatSessionRequest;
import org.keycloak.quickstart.request.CreateConnectorRequest;
import org.keycloak.quickstart.request.CreateConnectorRequest.ConnectorSpecificConfig;
import org.keycloak.quickstart.request.SendMessageRequest.RetrievalOptions;
import org.keycloak.quickstart.request.SendMessageRequest.RetrievalOptions.Filters;
import org.keycloak.quickstart.request.CreateCredentialRequest;
import org.keycloak.quickstart.request.DefaultPromptRequest;
import org.keycloak.quickstart.request.DocumentSetRequest;
import org.keycloak.quickstart.request.DocumentSetUpdateRequest;
import org.keycloak.quickstart.request.PersonaRequest;
import org.keycloak.quickstart.request.RunConnectorOnceRequest;
import org.keycloak.quickstart.request.SendMessageRequest;
import org.keycloak.quickstart.request.SendRequest;
import org.keycloak.quickstart.request.UpdateConnectorCredentialRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/chat")
public class ChatController {

	private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

	private String baseUrl = "https://chitchato.danswer.ai/";

	private String fastapiusersauth = "AidQORsi5r20CyydjKwp-fiS-AxKqPCQEla815IedwI";

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private PersonaRepository personaRepository;

	@Autowired
	private UserNumberRepository userNumberRepository;

	@PostMapping("/create-chat-session")
	public ResponseEntity<String> createChatSession(@RequestBody CreateChatSessionRequest createChatSessionRequest) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Accept", "application/json");
		headers.set("cookie", "fastapiusersauth=" + fastapiusersauth);

		HttpEntity<CreateChatSessionRequest> entity = new HttpEntity<>(createChatSessionRequest, headers);
		return restTemplate.exchange(baseUrl + "/api/chat/create-chat-session", HttpMethod.POST, entity, String.class);
	}

	@PostMapping("/send-message")
	public ResponseEntity<String> sendMessage(@RequestBody SendMessageRequest sendMessageRequest) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Accept", "application/json");
		headers.set("cookie", "fastapiusersauth=" + fastapiusersauth);

		HttpEntity<SendMessageRequest> entity = new HttpEntity<>(sendMessageRequest, headers);
		return restTemplate.exchange(baseUrl + "/api/chat/send-message", HttpMethod.POST, entity, String.class);
	}

	@PostMapping("/send")
	public ResponseEntity<?> send(@RequestBody SendRequest sendRequest, @AuthenticationPrincipal Jwt jwt) throws JsonMappingException, JsonProcessingException {
		// Initialize ObjectMapper for JSON parsing
    	ObjectMapper objectMapper = new ObjectMapper();

		// Initialize username
		String username = jwt.getClaimAsString("preferred_username");
		logger.info("username: {}", username);

		// Check is there any Persona exist with this user
		Persona personaExample = new Persona();
		personaExample.setCreatedBy(username);
		Optional<Persona> personaOptional = personaRepository.findOne(Example.of(personaExample));
		if (personaOptional.isPresent()) {
			// Check number is exist
			UserNumber userNumberExample = new UserNumber();
			userNumberExample.setCreatedBy(username);
			userNumberExample.setNo(sendRequest.getNo()); // Format Example : 6281380782318
			Optional<UserNumber> userNumberOptional = userNumberRepository.findOne(Example.of(userNumberExample));

			if (userNumberOptional.isPresent()) {
				// 1. Create Chat Session
				logger.info("1. Create Chat Session");
				CreateChatSessionRequest createChatSessionRequest = new CreateChatSessionRequest();
				createChatSessionRequest.setPersonaId(personaOptional.get().getPersonaId());
				
				ResponseEntity<String> createChatSessionResponse = createChatSession(createChatSessionRequest);
				if (!createChatSessionResponse.getStatusCode().is2xxSuccessful()) {
					return ResponseEntity.status(createChatSessionResponse.getStatusCode()).body("Failed to create chat session");
				}
				// Extract credential ID from createChatSessionResponse
				JsonNode createChatSessionNode = objectMapper.readTree(createChatSessionResponse.getBody());
				int chatSessionId = createChatSessionNode.path("chat_session_id").asInt();  
				logger.info("1. Create Chat Session. personaId {}", chatSessionId);

				// 2. Send Message
				logger.info("2. Send Message");
				SendMessageRequest sendMessageRequest = new SendMessageRequest();
				sendMessageRequest.setChatSessionId(chatSessionId);
				sendMessageRequest.setMessage(sendRequest.getMessage());
				sendMessageRequest.setParentMessageId(null);
				sendMessageRequest.setPromptId(null);
				RetrievalOptions retrievalOptions = new RetrievalOptions();
				Filters filters = new Filters();
				filters.setDocumentSet(null);
				filters.setSourceType(null);
				filters.setTags(new String[0]);
				filters.setTimeCutoff(null);
				retrievalOptions.setFilters(filters);
				retrievalOptions.setRealTime(false);
				retrievalOptions.setRunSearch("auto");
				sendMessageRequest.setRetrievalOptions(null);
				sendMessageRequest.setSearchDocIds(null);
				
				ResponseEntity<String> sendMessageResponse = sendMessage(sendMessageRequest);
				if (!sendMessageResponse.getStatusCode().is2xxSuccessful()) {
					return ResponseEntity.status(sendMessageResponse.getStatusCode()).body("Failed to send message");
				}
				// Extract credential ID from createChatSessionResponse
				JsonNode sendMessageNode = objectMapper.readTree(sendMessageResponse.getBody());
				int messageId = sendMessageNode.path("message_id").asInt();  
				logger.info("2. Send Message. messageId {}", messageId);

				return ResponseEntity.status(HttpStatus.OK).body("Process send completed successfully");
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No number found for " + sendRequest.getNo());
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No persona found for " + username);
		}		
	}
}
