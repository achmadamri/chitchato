package com.api.cct.backend.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.api.cct.backend.db.entity.Persona;
import com.api.cct.backend.db.entity.UserChat;
import com.api.cct.backend.db.entity.UserNumber;
import com.api.cct.backend.db.repository.PersonaRepository;
import com.api.cct.backend.db.repository.UserChatRepository;
import com.api.cct.backend.db.repository.UserNumberRepository;
import com.api.cct.backend.db.repository.UserRepository;
import com.api.cct.backend.request.CreateChatSessionRequest;
import com.api.cct.backend.request.SendMessageRequest;
import com.api.cct.backend.request.SendMessageRequest.RetrievalOptions;
import com.api.cct.backend.request.SendMessageRequest.RetrievalOptions.Filters;
import com.api.cct.backend.request.SendRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ChatService {

	private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

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

	private ResponseEntity<String> createChatSession(CreateChatSessionRequest createChatSessionRequest, String fastapiusersauth) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Accept", "application/json");
		headers.set("cookie", "fastapiusersauth=" + fastapiusersauth);

		HttpEntity<CreateChatSessionRequest> entity = new HttpEntity<>(createChatSessionRequest, headers);
		return restTemplate.exchange(baseUrl + "/api/chat/create-chat-session", HttpMethod.POST, entity, String.class);
	}

	private ResponseEntity<String> sendMessage(SendMessageRequest sendMessageRequest, String fastapiusersauth) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Accept", "application/json");
		headers.set("cookie", "fastapiusersauth=" + fastapiusersauth);

		HttpEntity<SendMessageRequest> entity = new HttpEntity<>(sendMessageRequest, headers);
		return restTemplate.exchange(baseUrl + "/api/chat/send-message", HttpMethod.POST, entity, String.class);
	}

	public ResponseEntity<?> postSend(SendRequest sendRequest) throws JsonProcessingException {
		// Initialize ObjectMapper for JSON parsing
		ObjectMapper objectMapper = new ObjectMapper();

		// Check if there is any Persona exist with this device
		Persona personaExample = new Persona();
		personaExample.setNumber(sendRequest.getDevice());
		Optional<Persona> personaOptional = personaRepository.findOne(Example.of(personaExample));
		if (personaOptional.isPresent()) {
			// Initialize username
			String username = personaOptional.get().getCreatedBy();
			logger.info("username: {}", username);

			// Initialize fastapiusersauth
			String fastapiusersauth = userRepository.findByUsername(username).get().getFastapiusersauth();

			// Check if number does not exist
			UserNumber userNumberExample = new UserNumber();
			userNumberExample.setCreatedBy(username);
			userNumberExample.setNo(sendRequest.getSender()); // Format Example: 6281380782318
			Optional<UserNumber> userNumberOptional = userNumberRepository.findOne(Example.of(userNumberExample));

			if (!userNumberOptional.isPresent()) {
				return handleNewNumber(sendRequest, personaOptional.get(), username, fastapiusersauth, objectMapper);
			} else {
				return handleExistingNumber(sendRequest, userNumberOptional.get(), username, personaOptional.get(), fastapiusersauth, objectMapper);
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No persona found for device " + sendRequest.getDevice());
		}
	}

	private ResponseEntity<?> handleNewNumber(SendRequest sendRequest, Persona persona, String username, String fastapiusersauth, ObjectMapper objectMapper) throws JsonProcessingException {
		// 1. Create Chat Session
		logger.info("1. Create Chat Session");
		CreateChatSessionRequest createChatSessionRequest = new CreateChatSessionRequest();
		createChatSessionRequest.setPersonaId(persona.getPersonaId());

		ResponseEntity<String> createChatSessionResponse = createChatSession(createChatSessionRequest, fastapiusersauth);
		if (!createChatSessionResponse.getStatusCode().is2xxSuccessful()) {
			return ResponseEntity.status(createChatSessionResponse.getStatusCode()).body("Failed to create chat session");
		}
		// Extract credential ID from createChatSessionResponse
		JsonNode createChatSessionNode = objectMapper.readTree(createChatSessionResponse.getBody());
		int chatSessionId = createChatSessionNode.path("chat_session_id").asInt();
		logger.info("1. Create Chat Session. chatSessionId {}", chatSessionId);

		// Save number
		LocalDateTime localDateTime = convertToLocalDateTime(new Date());

		UserNumber userNumber = new UserNumber();
		userNumber.setUuid(UUID.randomUUID().toString());
		userNumber.setCreatedBy(username);
		userNumber.setCreatedAt(localDateTime);
		userNumber.setNo(sendRequest.getSender());
		userNumber.setChatSessionId(chatSessionId);
		userNumberRepository.save(userNumber);

		// 2. Send Message
		logger.info("2. Send Message");
		SendMessageRequest sendMessageRequest = createSendMessageRequest(sendRequest, chatSessionId, persona.getPromptId(), null);

		ResponseEntity<String> sendMessageResponse = sendMessage(sendMessageRequest, fastapiusersauth);
		if (!sendMessageResponse.getStatusCode().is2xxSuccessful()) {
			return ResponseEntity.status(sendMessageResponse.getStatusCode()).body("Failed to send message");
		}
		return saveChatAndRespond(sendMessageResponse, sendRequest, username, localDateTime);
	}

	private ResponseEntity<?> handleExistingNumber(SendRequest sendRequest, UserNumber userNumber, String username, Persona persona, String fastapiusersauth, ObjectMapper objectMapper) throws JsonProcessingException {
		// 1. Get Chat Session ID
		logger.info("1. Get Chat Session ID");
		int chatSessionId = userNumber.getChatSessionId();
		logger.info("1. Get Chat Session ID. chatSessionId {}", chatSessionId);

		LocalDateTime localDateTime = convertToLocalDateTime(new Date());

		// 2. Send Message
		logger.info("2. Send Message");
		UserChat userChatLast = userChatRepository.findOneByCreatedByAndUserNumberOrderByMessageIdDesc(username, sendRequest.getSender()).get(0);

		SendMessageRequest sendMessageRequest = createSendMessageRequest(sendRequest, chatSessionId, persona.getPromptId(), userChatLast.getMessageId());

		ResponseEntity<String> sendMessageResponse = sendMessage(sendMessageRequest, fastapiusersauth);
		if (!sendMessageResponse.getStatusCode().is2xxSuccessful()) {
			return ResponseEntity.status(sendMessageResponse.getStatusCode()).body("Failed to send message");
		}
		return saveChatAndRespond(sendMessageResponse, sendRequest, username, localDateTime);
	}

	private LocalDateTime convertToLocalDateTime(Date date) {
		ZonedDateTime zdt = date.toInstant().atZone(ZoneId.systemDefault());
		return zdt.toLocalDateTime();
	}

	private SendMessageRequest createSendMessageRequest(SendRequest sendRequest, int chatSessionId, int promptId, Integer parentMessageId) {
		SendMessageRequest sendMessageRequest = new SendMessageRequest();
		sendMessageRequest.setChatSessionId(chatSessionId);
		sendMessageRequest.setMessage(sendRequest.getMessage());
		sendMessageRequest.setParentMessageId(parentMessageId);
		sendMessageRequest.setPromptId(promptId);

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

		return sendMessageRequest;
	}

	private ResponseEntity<?> saveChatAndRespond(ResponseEntity<String> sendMessageResponse, SendRequest sendRequest, String username, LocalDateTime localDateTime) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		String[] jsonArray = sendMessageResponse.getBody().split("\n");
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
		userChat.setUserNumber(sendRequest.getSender());
		userChat.setMessageIn(sendRequest.getMessage());
		userChat.setMessageOut(message);
		userChat.setMessageId(messageId);
		userChat.setParentMessageId(parentMessageId);
		// if message contains [admin] then follow up
		userChat.setFollowUp(message.contains("[admin]") ? "yes" : "no");

		userChatRepository.save(userChat);

		// remove flag [admin] from message
		message = message.replace("[admin]", "");

		// Return message
		return ResponseEntity.status(HttpStatus.OK).body(message);
	}
}
