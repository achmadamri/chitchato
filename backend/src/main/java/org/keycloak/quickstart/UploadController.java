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
import org.keycloak.quickstart.db.entity.User;
import org.keycloak.quickstart.db.repository.ConfigRepository;
import org.keycloak.quickstart.db.repository.ConnectorRepository;
import org.keycloak.quickstart.db.repository.DocumentSetRepository;
import org.keycloak.quickstart.db.repository.PersonaRepository;
import org.keycloak.quickstart.db.repository.PromptRepository;
import org.keycloak.quickstart.db.repository.UserRepository;
import org.keycloak.quickstart.request.CreateConnectorRequest;
import org.keycloak.quickstart.request.CreateConnectorRequest.ConnectorSpecificConfig;
import org.keycloak.quickstart.request.CreateCredentialRequest;
import org.keycloak.quickstart.request.DefaultPromptRequest;
import org.keycloak.quickstart.request.DeleteAttempt;
import org.keycloak.quickstart.request.DocumentSetRequest;
import org.keycloak.quickstart.request.DocumentSetUpdateRequest;
import org.keycloak.quickstart.request.PersonaRequest;
import org.keycloak.quickstart.request.RunConnectorOnceRequest;
import org.keycloak.quickstart.request.UpdateConnectorCredentialRequest;
import org.keycloak.quickstart.request.UpdateConnectorRequest;
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
@RequestMapping("/upload")
public class UploadController {

	private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

	private String baseUrl = "https://chitchato.danswer.ai/";

	private String systemPrompt;
	
	private String taskPrompt;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ConfigRepository configRepository;

	@Autowired
	private ConnectorRepository connectorRepository;

	@Autowired
	private DocumentSetRepository documentSetRepository;

	@Autowired
	private PromptRepository promptRepository;

	@Autowired
	private PersonaRepository personaRepository;

	@Autowired
	private UserRepository userRepository;

	private final WebClient webClient;

    public UploadController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

	@PostConstruct
    private void init() {
        systemPrompt = configRepository.findById("system_prompt").get().getValue();
        logger.info("systemPrompt: {}", systemPrompt);

        taskPrompt = configRepository.findById("task_prompt").get().getValue();
        logger.info("taskPrompt: {}", taskPrompt);
    }

	@PostMapping("/delete-document")
	public ResponseEntity<?> deleteDocument(@RequestParam String connectorUuid, @AuthenticationPrincipal Jwt jwt) throws JsonMappingException, JsonProcessingException {
		// Initialize username
		String username = jwt.getClaimAsString("preferred_username");
		logger.info("username: {}", username);

		// Load connector from database
		Connector connectorExample = new Connector();
		connectorExample.setUuid(connectorUuid);
		connectorExample.setCreatedBy(username);
		Connector connector = connectorRepository.findOne(Example.of(connectorExample)).orElse(null);

		if (connector == null) {
			return ResponseEntity.badRequest().body("{\"message\":\"Connector not found\"}");
		}

		// Minimal 1 connector should be available
		Connector connectorExampleCount = new Connector();
		connectorExampleCount.setCreatedBy(username);
		if (connectorRepository.count(Example.of(connectorExampleCount)) <= 1) {
			return ResponseEntity.status(500).body("{\"message\":\"Min connector limit\"}");
		}

		// Initialize ObjectMapper for JSON parsing
		ObjectMapper objectMapper = new ObjectMapper();

		// 1. Get CC Pair
		logger.info("1. Get CC Pair");
		ResponseEntity<String> ccPairResponse = getCcPair(String.valueOf(connector.getCcPairId()), userRepository.findByUsername(username).get().getFastapiusersauth());
		if (!ccPairResponse.getStatusCode().is2xxSuccessful()) {
			return ResponseEntity.status(ccPairResponse.getStatusCode()).body("Failed to get CC Pair");
		}
		JsonNode ccPairNode = objectMapper.readTree(ccPairResponse.getBody());
		String strConnector = ccPairNode.path("connector").toString();
		UpdateConnectorRequest updateConnectorRequest = objectMapper.readValue(strConnector, UpdateConnectorRequest.class);
		updateConnectorRequest.setDisabled(true);

		// 2. Pause Connector
		logger.info("2. Pause Connector");
		Mono<String> pauseConnectorMono = pauseConnector(updateConnectorRequest, connector.getConnectorId(), userRepository.findByUsername(username).get().getFastapiusersauth());
		String pauseConnectorResponse = pauseConnectorMono.block();
		if (Objects.isNull(pauseConnectorResponse)) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to pause connector");
		}

		// 3. Delete Attempt
		logger.info("3. Delete Attempt");
		DeleteAttempt deleteAttempt = new DeleteAttempt();
		deleteAttempt.setConnectorId(connector.getConnectorId());
		deleteAttempt.setCredentialId(connector.getCcPairId());

		ResponseEntity<String> deletionResponse = deletionAttempt(deleteAttempt, userRepository.findByUsername(username).get().getFastapiusersauth());
		if (!deletionResponse.getStatusCode().is2xxSuccessful()) {
			return ResponseEntity.status(deletionResponse.getStatusCode()).body("Failed to delete attempt");
		}

		// Delete connector from database
		connectorRepository.delete(connector);		
		
		return ResponseEntity.ok("{\"message\":\"Process deleteDocument completed successfully\"}");
	}

	@PostMapping("/add-document")
	public ResponseEntity<?> addDocument(@RequestParam String personaUuid, @RequestParam("file") MultipartFile file, @AuthenticationPrincipal Jwt jwt) throws JsonMappingException, JsonProcessingException {
		if (file.isEmpty()) {
			return ResponseEntity.badRequest().body("File is empty");
		}

		// Initialize username
		String username = jwt.getClaimAsString("preferred_username");
		logger.info("username: {}", username);

		// Load user from database
		User userExample = new User();
		userExample.setUsername(username);
		User user = userRepository.findOne(Example.of(userExample)).orElse(null);

		// Initialize uuid
		String uuid = UUID.randomUUID().toString();
		logger.info("uuid: {}", uuid);

		// Load persona from database
		Persona personaExample = new Persona();
		personaExample.setUuid(personaUuid);
		personaExample.setCreatedBy(username);		
		Persona persona = personaRepository.findOne(Example.of(personaExample)).orElse(null);

		if (persona == null) {
			return ResponseEntity.badRequest().body("Persona not found");
		}

		// Initialize ObjectMapper for JSON parsing
    	ObjectMapper objectMapper = new ObjectMapper();

		// If connector count is more than maxConnector
		Connector connectorExampleMaxConnector = new Connector();
		connectorExampleMaxConnector.setCreatedBy(username);
		if (connectorRepository.count(Example.of(connectorExampleMaxConnector)) >= user.getMaxConnector()) {
			return ResponseEntity.status(500).body("Max connector limit");
		}

		// 1. Upload the file
		logger.info("1. Upload the file");
		ResponseEntity<String> uploadResponse = uploadFile(file, user.getFastapiusersauth());
		if (!uploadResponse.getStatusCode().is2xxSuccessful()) {
			return ResponseEntity.status(uploadResponse.getStatusCode()).body("Failed to upload file");
		}
		JsonNode uploadNode = objectMapper.readTree(uploadResponse.getBody());
		String filePaths = uploadNode.get("file_paths").get(0).asText();
		logger.info("1. Upload the file. filePaths {}", filePaths);

		// 2. Create Connector
		// Assuming filePaths is extracted correctly from the upload response
		logger.info("2. Create Connector");
		CreateConnectorRequest connectorRequest = new CreateConnectorRequest();
		connectorRequest.setName("FileConnector-" + System.currentTimeMillis());
		connectorRequest.setSource("file");
		connectorRequest.setInputType("load_state");
		ConnectorSpecificConfig connectorSpecificConfig = new ConnectorSpecificConfig();
		List<String> fileLocations = new ArrayList<>();
		fileLocations.add(filePaths);
		connectorSpecificConfig.setFileLocations(fileLocations);
		connectorRequest.setConnectorSpecificConfig(connectorSpecificConfig);
		connectorRequest.setDisabled(false);
		connectorRequest.setRefreshFreq(null);		

		ResponseEntity<String> connectorResponse = createConnector(connectorRequest, user.getFastapiusersauth());
		if (!connectorResponse.getStatusCode().is2xxSuccessful()) {
			return ResponseEntity.status(connectorResponse.getStatusCode()).body("Failed to create connector");
		}
		// Extract connector ID from connectorResponse
		JsonNode connectorNode = objectMapper.readTree(connectorResponse.getBody());
		int connectorId = connectorNode.path("id").asInt();  
		logger.info("2. Create Connector. connectorId {}", connectorId);

		// Create a Date instance
		Date now = new Date();

		// Convert Date to LocalDateTime
		ZonedDateTime zdt = now.toInstant().atZone(ZoneId.systemDefault());
		LocalDateTime localDateTime = zdt.toLocalDateTime();

		Connector connector = new Connector();
		connector.setUuid(uuid);
		connector.setCreatedAt(localDateTime);
		connector.setCreatedBy(jwt.getClaimAsString("preferred_username"));
		connector.setConnectorId(connectorId);
		connector.setFileNames(file.getOriginalFilename());
		connectorRepository.save(connector);

		// 3. Create Credential
		logger.info("3. Create Credential");
		CreateCredentialRequest credentialRequest = new CreateCredentialRequest();
		credentialRequest.setCredentialJson(new HashMap<>());
		credentialRequest.setAdminPublic(true);

		ResponseEntity<String> credentialResponse = createCredential(credentialRequest, user.getFastapiusersauth());
		if (!credentialResponse.getStatusCode().is2xxSuccessful()) {
			return ResponseEntity.status(credentialResponse.getStatusCode()).body("Failed to create credential");
		}
		// Extract credential ID from credentialResponse
		JsonNode credentialNode = objectMapper.readTree(credentialResponse.getBody());
		int credentialId = credentialNode.path("id").asInt();  
		logger.info("3. Create Credential. credentialId {}", credentialId);

		// 4. Update Connector Credential
		logger.info("4. Update Connector Credential");
		UpdateConnectorCredentialRequest updateRequest = new UpdateConnectorCredentialRequest();
		updateRequest.setConnectorId(connectorId);
		updateRequest.setCredentialId(credentialId);
		updateRequest.setName(uuid);
		updateRequest.setPublic(true);

		ResponseEntity<String> updateResponse = updateConnectorCredential(updateRequest, user.getFastapiusersauth());
		if (!updateResponse.getStatusCode().is2xxSuccessful()) {
			return ResponseEntity.status(updateResponse.getStatusCode()).body("Failed to update connector credential");
		}

		// 5. Run Connector Once
		logger.info("5. Run Connector Once");
		RunConnectorOnceRequest runRequest = new RunConnectorOnceRequest();
		runRequest.setConnectorId(connectorId);
		List<Integer> credentialIds = new ArrayList<>();
		credentialIds.add(credentialId);
		runRequest.setCredentialIds(credentialIds);
		runRequest.setFromBeginning(false);

		ResponseEntity<String> runResponse = runConnectorOnce(runRequest, user.getFastapiusersauth());
		if (!runResponse.getStatusCode().is2xxSuccessful()) {
			return ResponseEntity.status(runResponse.getStatusCode()).body("Failed to run connector");
		}

		// 6. Get Indexing Status		
		logger.info("6. Get Indexing Status");
		ResponseEntity<String> indexingResponse = getIndexingStatus(user.getFastapiusersauth());
		if (!indexingResponse.getStatusCode().is2xxSuccessful()) {
			return ResponseEntity.status(indexingResponse.getStatusCode()).body("Failed to run indexing");
		}
		// Extract CC Pair ID from indexingResponse
		// Parse the JSON array from the response body
		JsonNode indexingArray = objectMapper.readTree(indexingResponse.getBody());

		// Initialize variable to store the found cc_pair_id
		int ccPairId = -1; // Default to -1 or another sentinel value to indicate not found

		// Iterate through each item in the array
		for (JsonNode item : indexingArray) {
			// Check if the name matches the given name
			if (item.path("name").asText().equals(uuid)) {
				// If a match is found, extract the cc_pair_id
				ccPairId = item.path("cc_pair_id").asInt();
				// Log the found ccPairId
				logger.info("6. Get Indexing Status. ccPairId {}", ccPairId);
				// Break out of the loop if you only expect one match
				break;
			}
		}

		// Check if the ccPairId was found
		if (ccPairId == -1) {
			// Handle the case where the given name was not found in the array
			return ResponseEntity.status(indexingResponse.getStatusCode()).body("Given name '" + uuid + "' not found in the indexing response.");
		}

		connector.setCcPairId(ccPairId);
		connectorRepository.save(connector);

		// 7. Update Document Set
		logger.info("7. Update Document Set");
		DocumentSet documentSetExample = new DocumentSet();
		documentSetExample.setDocumentSetId(persona.getDocumentSetId());
		documentSetExample.setCreatedBy(username);
		Optional<DocumentSet> documentSetOptional = documentSetRepository.findOne(Example.of(documentSetExample));

		DocumentSetUpdateRequest documentSetUpdateRequest = new DocumentSetUpdateRequest();
		documentSetUpdateRequest.setId(documentSetOptional.get().getDocumentSetId());
		documentSetUpdateRequest.setDescription(documentSetOptional.get().getUuid());

		List<Integer> ccPairIds = new ArrayList<>();
		Connector connectorExample = new Connector();
		connectorExample.setCreatedBy(username);
		List<Connector> lstConnector = connectorRepository.findAll(Example.of(connectorExample));
		for (Connector c : lstConnector) {
			ccPairIds.add(c.getCcPairId());
		}

		documentSetUpdateRequest.setCcPairIds(ccPairIds);

		ResponseEntity<?> responseEntity = updateDocumentSet(documentSetUpdateRequest, user.getFastapiusersauth())
		.flatMap(response -> {
			try {
				// Assuming the response is the document set ID as a string
				// int documentSetId = Integer.parseInt(response);
				// logger.info("7. Update Document Set. documentSetId {}", documentSetId);
				// Since we're making it synchronous, return the OK status directly
				return Mono.just(ResponseEntity.ok(HttpStatus.OK));
			} catch (NumberFormatException e) {
				logger.error("Failed to parse documentSetId from response", e);
				return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to run document"));
			}
		})
		.block(); // This blocks until the operation is completed

		// Handle the result synchronously
		if (responseEntity != null && !responseEntity.getStatusCode().is2xxSuccessful()) {
			logger.error("Failed to update document set");
			return ResponseEntity.status(connectorResponse.getStatusCode()).body("Failed to update document set");
		} else if (responseEntity != null) {
			logger.info("Successfully updated document set with ID: {}", responseEntity.getBody());
		}

		return ResponseEntity.status(HttpStatus.OK).body("Process addDocument completed successfully");
	}

	public ResponseEntity<String> getCcPair(String ccPairId, String fastapiusersauth) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", "application/json");
		headers.set("cookie", "fastapiusersauth=" + fastapiusersauth);

		HttpEntity<String> entity = new HttpEntity<>(headers);
		return restTemplate.exchange(baseUrl + "/api/manage/admin/cc-pair/" + ccPairId, HttpMethod.GET, entity, String.class);
	}

	public Mono<String> pauseConnector(@RequestBody UpdateConnectorRequest updateConnectorRequest, Integer ccPairId, String fastapiusersauth) {
        return this.webClient.patch()
                .uri("/api/manage/admin/connector/" + ccPairId)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .header("cookie", "fastapiusersauth=" + fastapiusersauth)
                .bodyValue(updateConnectorRequest)
                .retrieve() // Initiate the request
                .bodyToMono(String.class); // Convert the response body to String
    }

	public ResponseEntity<String> deletionAttempt(@RequestBody DeleteAttempt deleteAttempt, String fastapiusersauth) {
		String url = "https://chitchato.danswer.ai/api/manage/admin/deletion-attempt";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.set("authority", "chitchato.danswer.ai");
		headers.set("cookie", "fastapiusersauth=" + fastapiusersauth);

		HttpEntity<DeleteAttempt> entity = new HttpEntity<>(deleteAttempt, headers);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
		return response;
	}

	@PostMapping("/upload-combined")
	public ResponseEntity<?> uploadCombined(@RequestParam String personaUuid, @RequestParam("file") MultipartFile file, @AuthenticationPrincipal Jwt jwt) throws JsonMappingException, JsonProcessingException {
		if (file.isEmpty()) {
			return ResponseEntity.badRequest().body("File is empty");
		}

		// Initialize ObjectMapper for JSON parsing
    	ObjectMapper objectMapper = new ObjectMapper();

		// Initialize username
		String username = jwt.getClaimAsString("preferred_username");
		logger.info("username: {}", username);

		// Initialize fastapiusersauth
		String fastapiusersauth = userRepository.findByUsername(username).get().getFastapiusersauth();

		// Initialize uuid
		String uuid = UUID.randomUUID().toString();
		logger.info("uuid: {}", uuid);

		// Check is there any Persona exist with this user
		Persona personaExample = new Persona();
		personaExample.setCreatedBy(username);
		Optional<Persona> personaOptional = personaRepository.findOne(Example.of(personaExample));
		if (personaOptional.isPresent()) {
			DocumentSet documentSetCountExample = new DocumentSet();
			documentSetCountExample.setCreatedBy(username);
			if (documentSetRepository.count(Example.of(documentSetCountExample)) >= 2) {
				return ResponseEntity.status(500).body("Max document limit");
			}

			// 1. Upload the file
			logger.info("1. Upload the file");
			ResponseEntity<String> uploadResponse = uploadFile(file, fastapiusersauth);
			if (!uploadResponse.getStatusCode().is2xxSuccessful()) {
				return ResponseEntity.status(uploadResponse.getStatusCode()).body("Failed to upload file");
			}
			JsonNode uploadNode = objectMapper.readTree(uploadResponse.getBody());
			String filePaths = uploadNode.get("file_paths").get(0).asText();
			logger.info("1. Upload the file. filePaths {}", filePaths);

			// 2. Create Connector
			// Assuming filePaths is extracted correctly from the upload response
			logger.info("2. Create Connector");
			CreateConnectorRequest connectorRequest = new CreateConnectorRequest();
			connectorRequest.setName("FileConnector-" + System.currentTimeMillis());
			connectorRequest.setSource("file");
			connectorRequest.setInputType("load_state");
			ConnectorSpecificConfig connectorSpecificConfig = new ConnectorSpecificConfig();
			List<String> fileLocations = new ArrayList<>();
			fileLocations.add(filePaths);
			connectorSpecificConfig.setFileLocations(fileLocations);
			connectorRequest.setConnectorSpecificConfig(connectorSpecificConfig);
			connectorRequest.setDisabled(false);
			connectorRequest.setRefreshFreq(null);		

			ResponseEntity<String> connectorResponse = createConnector(connectorRequest, fastapiusersauth);
			if (!connectorResponse.getStatusCode().is2xxSuccessful()) {
				return ResponseEntity.status(connectorResponse.getStatusCode()).body("Failed to create connector");
			}
			// Extract connector ID from connectorResponse
			JsonNode connectorNode = objectMapper.readTree(connectorResponse.getBody());
			int connectorId = connectorNode.path("id").asInt();  
			logger.info("2. Create Connector. connectorId {}", connectorId);

			// Create a Date instance
			Date now = new Date();

			// Convert Date to LocalDateTime
			ZonedDateTime zdt = now.toInstant().atZone(ZoneId.systemDefault());
			LocalDateTime localDateTime = zdt.toLocalDateTime();

			Connector connector = new Connector();
			connector.setUuid(uuid);
			connector.setCreatedAt(localDateTime);
			connector.setCreatedBy(jwt.getClaimAsString("preferred_username"));
			connector.setConnectorId(connectorId);
			connector.setFileNames(file.getOriginalFilename());
			connectorRepository.save(connector);

			// 3. Create Credential
			logger.info("3. Create Credential");
			CreateCredentialRequest credentialRequest = new CreateCredentialRequest();
			credentialRequest.setCredentialJson(new HashMap<>());
			credentialRequest.setAdminPublic(true);

			ResponseEntity<String> credentialResponse = createCredential(credentialRequest, fastapiusersauth);
			if (!credentialResponse.getStatusCode().is2xxSuccessful()) {
				return ResponseEntity.status(credentialResponse.getStatusCode()).body("Failed to create credential");
			}
			// Extract credential ID from credentialResponse
			JsonNode credentialNode = objectMapper.readTree(credentialResponse.getBody());
			int credentialId = credentialNode.path("id").asInt();  
			logger.info("3. Create Credential. credentialId {}", credentialId);

			// 4. Update Connector Credential
			logger.info("4. Update Connector Credential");
			UpdateConnectorCredentialRequest updateRequest = new UpdateConnectorCredentialRequest();
			updateRequest.setConnectorId(connectorId);
			updateRequest.setCredentialId(credentialId);
			updateRequest.setName(uuid);
			updateRequest.setPublic(true);

			ResponseEntity<String> updateResponse = updateConnectorCredential(updateRequest, fastapiusersauth);
			if (!updateResponse.getStatusCode().is2xxSuccessful()) {
				return ResponseEntity.status(updateResponse.getStatusCode()).body("Failed to update connector credential");
			}

			// 5. Run Connector Once
			logger.info("5. Run Connector Once");
			RunConnectorOnceRequest runRequest = new RunConnectorOnceRequest();
			runRequest.setConnectorId(connectorId);
			List<Integer> credentialIds = new ArrayList<>();
			credentialIds.add(credentialId);
			runRequest.setCredentialIds(credentialIds);
			runRequest.setFromBeginning(false);

			ResponseEntity<String> runResponse = runConnectorOnce(runRequest, fastapiusersauth);
			if (!runResponse.getStatusCode().is2xxSuccessful()) {
				return ResponseEntity.status(runResponse.getStatusCode()).body("Failed to run connector");
			}

			// 6. Get Indexing Status		
			logger.info("6. Get Indexing Status");
			ResponseEntity<String> indexingResponse = getIndexingStatus(fastapiusersauth);
			if (!indexingResponse.getStatusCode().is2xxSuccessful()) {
				return ResponseEntity.status(indexingResponse.getStatusCode()).body("Failed to run indexing");
			}
			// Extract CC Pair ID from indexingResponse
			// Parse the JSON array from the response body
			JsonNode indexingArray = objectMapper.readTree(indexingResponse.getBody());

			// Initialize variable to store the found cc_pair_id
			int ccPairId = -1; // Default to -1 or another sentinel value to indicate not found

			// Iterate through each item in the array
			for (JsonNode item : indexingArray) {
				// Check if the name matches the given name
				if (item.path("name").asText().equals(uuid)) {
					// If a match is found, extract the cc_pair_id
					ccPairId = item.path("cc_pair_id").asInt();
					// Log the found ccPairId
					logger.info("6. Get Indexing Status. ccPairId {}", ccPairId);
					// Break out of the loop if you only expect one match
					break;
				}
			}

			// Check if the ccPairId was found
			if (ccPairId == -1) {
				// Handle the case where the given name was not found in the array
				return ResponseEntity.status(indexingResponse.getStatusCode()).body("Given name '" + uuid + "' not found in the indexing response.");
			}

			connector.setCcPairId(ccPairId);
			connectorRepository.save(connector);

			// 7. Update Document Set
			logger.info("7. Update Document Set");
			DocumentSet documentSetExample = new DocumentSet();
			documentSetExample.setCreatedBy(username);
			Optional<DocumentSet> documentSetOptional = documentSetRepository.findOne(Example.of(documentSetExample));

			DocumentSetUpdateRequest documentSetUpdateRequest = new DocumentSetUpdateRequest();
			documentSetUpdateRequest.setId(documentSetOptional.get().getDocumentSetId());
			documentSetUpdateRequest.setDescription(documentSetOptional.get().getUuid());

			List<Integer> ccPairIds = new ArrayList<>();
			Connector connectorExample = new Connector();
			connectorExample.setCreatedBy(username);
			List<Connector> lstConnector = connectorRepository.findAll(Example.of(connectorExample));
			for (Connector c : lstConnector) {
				ccPairIds.add(c.getCcPairId());
			}

			documentSetUpdateRequest.setCcPairIds(ccPairIds);

			ResponseEntity<?> responseEntity = updateDocumentSet(documentSetUpdateRequest, fastapiusersauth)
			.flatMap(response -> {
                try {
                    // Assuming the response is the document set ID as a string
                    // int documentSetId = Integer.parseInt(response);
                    // logger.info("7. Update Document Set. documentSetId {}", documentSetId);
                    // Since we're making it synchronous, return the OK status directly
                    return Mono.just(ResponseEntity.ok(HttpStatus.OK));
                } catch (NumberFormatException e) {
                    logger.error("Failed to parse documentSetId from response", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to run document"));
                }
            })
            .block(); // This blocks until the operation is completed

			// Handle the result synchronously
			if (responseEntity != null && !responseEntity.getStatusCode().is2xxSuccessful()) {
				logger.error("Failed to update document set");
				return ResponseEntity.status(connectorResponse.getStatusCode()).body("Failed to update document set");
			} else if (responseEntity != null) {
				logger.info("Successfully updated document set with ID: {}", responseEntity.getBody());
			}

			return ResponseEntity.status(HttpStatus.OK).body("Process uploadCombined completed successfully");
		} else {
			// 1. Upload the file
			logger.info("1. Upload the file");
			ResponseEntity<String> uploadResponse = uploadFile(file, fastapiusersauth);
			if (!uploadResponse.getStatusCode().is2xxSuccessful()) {
				return ResponseEntity.status(uploadResponse.getStatusCode()).body("Failed to upload file");
			}
			JsonNode uploadNode = objectMapper.readTree(uploadResponse.getBody());
			String filePaths = uploadNode.get("file_paths").get(0).asText();
			logger.info("1. Upload the file. filePaths {}", filePaths);

			// 2. Create Connector
			// Assuming filePaths is extracted correctly from the upload response
			logger.info("2. Create Connector");
			CreateConnectorRequest connectorRequest = new CreateConnectorRequest();
			connectorRequest.setName("FileConnector-" + System.currentTimeMillis());
			connectorRequest.setSource("file");
			connectorRequest.setInputType("load_state");
			ConnectorSpecificConfig connectorSpecificConfig = new ConnectorSpecificConfig();
			List<String> fileLocations = new ArrayList<>();
			fileLocations.add(filePaths);
			connectorSpecificConfig.setFileLocations(fileLocations);
			connectorRequest.setConnectorSpecificConfig(connectorSpecificConfig);
			connectorRequest.setDisabled(false);
			connectorRequest.setRefreshFreq(null);		

			ResponseEntity<String> connectorResponse = createConnector(connectorRequest, fastapiusersauth);
			if (!connectorResponse.getStatusCode().is2xxSuccessful()) {
				return ResponseEntity.status(connectorResponse.getStatusCode()).body("Failed to create connector");
			}
			// Extract connector ID from connectorResponse
			JsonNode connectorNode = objectMapper.readTree(connectorResponse.getBody());
			int connectorId = connectorNode.path("id").asInt();  
			logger.info("2. Create Connector. connectorId {}", connectorId);

			// Create a Date instance
			Date now = new Date();

			// Convert Date to LocalDateTime
			ZonedDateTime zdt = now.toInstant().atZone(ZoneId.systemDefault());
			LocalDateTime localDateTime = zdt.toLocalDateTime();

			Connector connector = new Connector();
			connector.setUuid(uuid);
			connector.setCreatedAt(localDateTime);
			connector.setCreatedBy(jwt.getClaimAsString("preferred_username"));
			connector.setConnectorId(connectorId);
			connector.setFileNames(file.getOriginalFilename());
			connectorRepository.save(connector);

			// 3. Create Credential
			logger.info("3. Create Credential");
			CreateCredentialRequest credentialRequest = new CreateCredentialRequest();
			credentialRequest.setCredentialJson(new HashMap<>());
			credentialRequest.setAdminPublic(true);

			ResponseEntity<String> credentialResponse = createCredential(credentialRequest, fastapiusersauth);
			if (!credentialResponse.getStatusCode().is2xxSuccessful()) {
				return ResponseEntity.status(credentialResponse.getStatusCode()).body("Failed to create credential");
			}
			// Extract credential ID from credentialResponse
			JsonNode credentialNode = objectMapper.readTree(credentialResponse.getBody());
			int credentialId = credentialNode.path("id").asInt();  
			logger.info("3. Create Credential. credentialId {}", credentialId);

			// 4. Update Connector Credential
			logger.info("4. Update Connector Credential");
			UpdateConnectorCredentialRequest updateRequest = new UpdateConnectorCredentialRequest();
			updateRequest.setConnectorId(connectorId);
			updateRequest.setCredentialId(credentialId);
			updateRequest.setName(uuid);
			updateRequest.setPublic(true);

			ResponseEntity<String> updateResponse = updateConnectorCredential(updateRequest, fastapiusersauth);
			if (!updateResponse.getStatusCode().is2xxSuccessful()) {
				return ResponseEntity.status(updateResponse.getStatusCode()).body("Failed to update connector credential");
			}

			// 5. Run Connector Once
			logger.info("5. Run Connector Once");
			RunConnectorOnceRequest runRequest = new RunConnectorOnceRequest();
			runRequest.setConnectorId(connectorId);
			List<Integer> credentialIds = new ArrayList<>();
			credentialIds.add(credentialId);
			runRequest.setCredentialIds(credentialIds);
			runRequest.setFromBeginning(false);

			ResponseEntity<String> runResponse = runConnectorOnce(runRequest, fastapiusersauth);
			if (!runResponse.getStatusCode().is2xxSuccessful()) {
				return ResponseEntity.status(runResponse.getStatusCode()).body("Failed to run connector");
			}

			// 6. Get Indexing Status		
			logger.info("6. Get Indexing Status");
			ResponseEntity<String> indexingResponse = getIndexingStatus(fastapiusersauth);
			if (!indexingResponse.getStatusCode().is2xxSuccessful()) {
				return ResponseEntity.status(indexingResponse.getStatusCode()).body("Failed to run indexing");
			}
			// Extract CC Pair ID from indexingResponse
			// Parse the JSON array from the response body
			JsonNode indexingArray = objectMapper.readTree(indexingResponse.getBody());

			// Initialize variable to store the found cc_pair_id
			int ccPairId = -1; // Default to -1 or another sentinel value to indicate not found

			// Iterate through each item in the array
			for (JsonNode item : indexingArray) {
				// Check if the name matches the given name
				if (item.path("name").asText().equals(uuid)) {
					// If a match is found, extract the cc_pair_id
					ccPairId = item.path("cc_pair_id").asInt();
					// Log the found ccPairId
					logger.info("6. Get Indexing Status. ccPairId {}", ccPairId);
					// Break out of the loop if you only expect one match
					break;
				}
			}

			// Check if the ccPairId was found
			if (ccPairId == -1) {
				// Handle the case where the given name was not found in the array
				return ResponseEntity.status(indexingResponse.getStatusCode()).body("Given name '" + uuid + "' not found in the indexing response.");
			}

			connector.setCcPairId(ccPairId);
			connectorRepository.save(connector);

			// 7. Create Document Set
			logger.info("7. Create Document Set");
			DocumentSetRequest documentRequest = new DocumentSetRequest();
			documentRequest.setName(uuid);
			documentRequest.setDescription(uuid);
			List<Integer> ccPairIds = new ArrayList<>();
			ccPairIds.add(ccPairId);
			documentRequest.setCcPairIds(ccPairIds);

			ResponseEntity<String> documentResponse = createDocumentSet(documentRequest, fastapiusersauth);
			if (!documentResponse.getStatusCode().is2xxSuccessful()) {
				return ResponseEntity.status(documentResponse.getStatusCode()).body("Failed to run document");
			}
			int documentSetId = Integer.parseInt(documentResponse.getBody());
			logger.info("7. Create Document Set. documentSetId {}", documentSetId);

			DocumentSet documentSet = new DocumentSet();
			documentSet.setUuid(uuid);
			documentSet.setCreatedAt(localDateTime);
			documentSet.setCreatedBy(jwt.getClaimAsString("preferred_username"));
			documentSet.setDocumentSetId(documentSetId);
			documentSet.setName(uuid);
			documentSet.setDescription(uuid);
			documentSetRepository.save(documentSet);

			// 8. Create Default Prompt
			logger.info("8. Create Default Prompt");
			DefaultPromptRequest promptRequest = new DefaultPromptRequest();
			promptRequest.setName("default-prompt__" + uuid);
			promptRequest.setDescription("Default prompt for persona " + uuid);
			promptRequest.setShared(true);
			promptRequest.setSystemPrompt(systemPrompt);
			promptRequest.setTaskPrompt(taskPrompt);
			promptRequest.setIncludeCitations(true);		

			ResponseEntity<String> promptResponse = createDefaultPrompt(promptRequest, fastapiusersauth);
			if (!promptResponse.getStatusCode().is2xxSuccessful()) {
				return ResponseEntity.status(promptResponse.getStatusCode()).body("Failed to run Create Default Prompt");
			}
			// Extract Prompt ID from promptResponse
			JsonNode promptNode = objectMapper.readTree(promptResponse.getBody());
			int promptId = promptNode.path("id").asInt();
			logger.info("8. Create Default Prompt. promptId {}", promptId);

			Prompt prompt = new Prompt();
			prompt.setUuid(uuid);
			prompt.setCreatedAt(localDateTime);
			prompt.setCreatedBy(jwt.getClaimAsString("preferred_username"));
			prompt.setPromptId(promptId);
			prompt.setName(promptRequest.getName());
			prompt.setDescription(promptRequest.getDescription());
			prompt.setSystemPrompt(promptRequest.getSystemPrompt());
			prompt.setTaskPrompt(promptRequest.getTaskPrompt());
			promptRepository.save(prompt);

			// 9. Create Persona
			logger.info("9. Create Persona");
			PersonaRequest personaRequest = new PersonaRequest();
			personaRequest.setName(uuid);
			personaRequest.setDescription(uuid);
			personaRequest.setIncludeCitations(false);
			personaRequest.setShared(true);
			personaRequest.setNumChunks(10);
			personaRequest.setLlmRelevanceFilter(false);
			personaRequest.setLlmFilterExtraction(false);
			personaRequest.setRecencyBias("base_decay");		
			List<Integer> promptIds = new ArrayList<>();
			promptIds.add(promptId);
			personaRequest.setPromptIds(promptIds);
			List<Integer> documentSetIds = new ArrayList<>();
			documentSetIds.add(documentSetId);
			personaRequest.setDocumentSetIds(documentSetIds);

			ResponseEntity<String> personaResponse = createPersona(personaRequest, fastapiusersauth);
			if (!personaResponse.getStatusCode().is2xxSuccessful()) {
				return ResponseEntity.status(personaResponse.getStatusCode()).body("Failed to run Create Persona");
			}
			// Extract Prompt ID from promptResponse
			JsonNode personaNode = objectMapper.readTree(personaResponse.getBody());
			int personaId = personaNode.path("id").asInt();
			logger.info("9. Create Persona. personaId {}", personaId);

			Persona persona = new Persona();
			persona.setUuid(uuid);
			persona.setCreatedAt(localDateTime);
			persona.setCreatedBy(jwt.getClaimAsString("preferred_username"));
			persona.setPersonaId(personaId);
			persona.setName(personaRequest.getName());
			persona.setDescription(personaRequest.getDescription());
			persona.setPromptId(promptId);
			persona.setDocumentSetId(documentSetId);
			personaRepository.save(persona);

			return ResponseEntity.status(HttpStatus.OK).body("Process uploadCombined completed successfully");
		}		
	}

	public ResponseEntity<String> getIndexingStatus(String fastapiusersauth) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", "application/json");
		headers.set("cookie", "fastapiusersauth=" + fastapiusersauth);

		HttpEntity<String> entity = new HttpEntity<>(headers);
		return restTemplate.exchange(baseUrl + "/api/manage/admin/connector/indexing-status?secondary_index=false", HttpMethod.GET, entity, String.class);
	}

	public ResponseEntity<String> uploadFile(@RequestParam("files") MultipartFile file, String fastapiusersauth) {
		// Check if the file is empty or not
		if (file.isEmpty()) {
			return new ResponseEntity<>("Empty file cannot be uploaded", HttpStatus.BAD_REQUEST);
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		headers.set("Accept", "application/json");
		headers.set("cookie", "fastapiusersauth=" + fastapiusersauth);

		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("files", new FileSystemResource(convertMultiPartToFile(file)));

		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

		// Make the network call
		ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/api/manage/admin/connector/file/upload", requestEntity, String.class);

		// Implement additional logic as required
		return response;
	}

	private File convertMultiPartToFile(MultipartFile file) {
		File convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
		try (FileOutputStream fos = new FileOutputStream(convFile)) {
			fos.write(file.getBytes());
		} catch (IOException e) {
			// Handle the exception properly
		}
		return convFile;
	}

	public ResponseEntity<String> createConnector(@RequestBody CreateConnectorRequest createConnectorRequest, String fastapiusersauth) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Accept", "application/json");
		headers.set("cookie", "fastapiusersauth=" + fastapiusersauth);

		HttpEntity<CreateConnectorRequest> entity = new HttpEntity<>(createConnectorRequest, headers);
		return restTemplate.exchange(baseUrl + "/api/manage/admin/connector", HttpMethod.POST, entity, String.class);
	}

	public ResponseEntity<String> createCredential(@RequestBody CreateCredentialRequest createCredentialRequest, String fastapiusersauth) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Accept", "application/json");
		headers.set("cookie", "fastapiusersauth=" + fastapiusersauth);

		HttpEntity<CreateCredentialRequest> entity = new HttpEntity<>(createCredentialRequest, headers);
		return restTemplate.exchange(baseUrl + "/api/manage/credential", HttpMethod.POST, entity, String.class);
	}

	public ResponseEntity<String> updateConnectorCredential(@RequestBody UpdateConnectorCredentialRequest updateConnectorCredentialRequest, String fastapiusersauth) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Accept", "application/json");
		headers.set("cookie", "fastapiusersauth=" + fastapiusersauth);

		HttpEntity<UpdateConnectorCredentialRequest> entity = new HttpEntity<>(updateConnectorCredentialRequest, headers);
		int connectorId = updateConnectorCredentialRequest.getConnectorId();
		int credentialId = updateConnectorCredentialRequest.getCredentialId();
		return restTemplate.exchange(baseUrl + "/api/manage/connector/" + connectorId + "/credential/" + credentialId, HttpMethod.PUT, entity, String.class);
	}

	public ResponseEntity<String> runConnectorOnce(@RequestBody RunConnectorOnceRequest runOnceRequest, String fastapiusersauth) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Accept", "application/json");
		headers.set("cookie", "fastapiusersauth=" + fastapiusersauth);

		HttpEntity<RunConnectorOnceRequest> entity = new HttpEntity<>(runOnceRequest, headers);
		return restTemplate.exchange(baseUrl + "/api/manage/admin/connector/run-once", HttpMethod.POST, entity, String.class);
	}

	public ResponseEntity<String> createDocumentSet(@RequestBody DocumentSetRequest documentSetRequest, String fastapiusersauth) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.ALL));
		headers.set("Accept", "application/json");
		headers.set("cookie", "fastapiusersauth=" + fastapiusersauth);

		HttpEntity<DocumentSetRequest> entity = new HttpEntity<>(documentSetRequest, headers);
		return restTemplate.exchange(baseUrl + "/api/manage/admin/document-set", HttpMethod.POST, entity, String.class);
	}

    public Mono<String> updateDocumentSet(@RequestBody DocumentSetUpdateRequest documentSetUpdateRequest, String fastapiusersauth) {
        return this.webClient.patch()
                .uri("/api/manage/admin/document-set")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .header("cookie", "fastapiusersauth=" + fastapiusersauth)
                .bodyValue(documentSetUpdateRequest)
                .retrieve() // Initiate the request
                .bodyToMono(String.class); // Convert the response body to String
    }

	public ResponseEntity<String> createDefaultPrompt(@RequestBody DefaultPromptRequest defaultPromptRequest, String fastapiusersauth) {
		String url = "https://chitchato.danswer.ai/api/prompt";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.set("authority", "chitchato.danswer.ai");
		headers.set("cookie", "fastapiusersauth=" + fastapiusersauth);

		HttpEntity<DefaultPromptRequest> entity = new HttpEntity<>(defaultPromptRequest, headers);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
		return response;
	}

	public ResponseEntity<String> createPersona(@RequestBody PersonaRequest personaRequest, String fastapiusersauth) {
		String url = "https://chitchato.danswer.ai/api/admin/persona";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.set("authority", "chitchato.danswer.ai");
		headers.set("cookie", "fastapiusersauth=" + fastapiusersauth);

		HttpEntity<PersonaRequest> entity = new HttpEntity<>(personaRequest, headers);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
		return response;
	}
}
