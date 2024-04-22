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

import java.util.List;

import org.keycloak.quickstart.db.entity.Connector;
import org.keycloak.quickstart.db.entity.DocumentSet;
import org.keycloak.quickstart.db.entity.DocumentSetConnector;
import org.keycloak.quickstart.db.entity.Persona;
import org.keycloak.quickstart.db.entity.Prompt;
import org.keycloak.quickstart.db.entity.User;
import org.keycloak.quickstart.db.repository.ConnectorRepository;
import org.keycloak.quickstart.db.repository.DocumentSetConnectorRepository;
import org.keycloak.quickstart.db.repository.DocumentSetRepository;
import org.keycloak.quickstart.db.repository.PersonaRepository;
import org.keycloak.quickstart.db.repository.PromptRepository;
import org.keycloak.quickstart.db.repository.UserRepository;
import org.keycloak.quickstart.request.PersonaUpdateRequest;
import org.keycloak.quickstart.request.PromptUpdateRequest;
import org.keycloak.quickstart.request.UpdatePersonaRequest;
import org.keycloak.quickstart.response.GetPersonaListResponse;
import org.keycloak.quickstart.response.GetPersonaResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/persona")
public class PersonaController {

	private static final Logger logger = LoggerFactory.getLogger(PersonaController.class);

    private String baseUrl = "https://chitchato.danswer.ai/";

	@Autowired
	private PersonaRepository personaRepository;

	@Autowired
	private PromptRepository promptRepository;

	@Autowired
	private DocumentSetRepository documentSetRepository;

	@Autowired
	private DocumentSetConnectorRepository documentSetConnectorRepository;

	@Autowired
	private ConnectorRepository connectorRepository;

	@Autowired
	private UserRepository userRepository;

	private final WebClient webClient;

    public PersonaController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

	@GetMapping("/persona-list")
    public ResponseEntity<GetPersonaListResponse> getPersonaList(@AuthenticationPrincipal Jwt jwt) {
        GetPersonaListResponse response = new GetPersonaListResponse();

        // Load Persona from database
        Persona personaExample = new Persona();
        personaExample.setCreatedBy(jwt.getClaimAsString("preferred_username"));
        List<Persona> personas = personaRepository.findAll(Example.of(personaExample));

        // Iterate through the list of personas and remove NumberToken from each persona but do not remove it from the database
        personas.forEach(persona -> persona.setNumberToken(null));

        response.setLstPersona(personas);

        // Return the list of personas as a JSON string
        return ResponseEntity.ok(response);
    }

	@GetMapping("/persona")
    public ResponseEntity<GetPersonaResponse> getPersona(@RequestParam String uuid, @AuthenticationPrincipal Jwt jwt) {
        GetPersonaResponse response = new GetPersonaResponse();

        // Load Persona from database
        Persona personaExample = new Persona();
        personaExample.setCreatedBy(jwt.getClaimAsString("preferred_username"));
        personaExample.setUuid(uuid);
        Persona persona = personaRepository.findOne(Example.of(personaExample)).orElse(null);

        // Remove NumberToken from persona but do not remove it from the database
        persona.setNumberToken(null);

        // Load Prompt from database
        Prompt promptExample = new Prompt();
        promptExample.setCreatedBy(jwt.getClaimAsString("preferred_username"));
        promptExample.setPromptId(persona.getPromptId());
        Prompt prompt = promptRepository.findOne(Example.of(promptExample)).orElse(null);

        // Load Document Set from database
        DocumentSet documentSetExample = new DocumentSet();
        documentSetExample.setCreatedBy(jwt.getClaimAsString("preferred_username"));
        documentSetExample.setDocumentSetId(persona.getDocumentSetId());
        DocumentSet documentSet = documentSetRepository.findOne(Example.of(documentSetExample)).orElse(null);

        // Load Document Set Connector from database
        DocumentSetConnector documentSetConnectorExample = new DocumentSetConnector();
        documentSetConnectorExample.setCreatedBy(jwt.getClaimAsString("preferred_username"));
        documentSetConnectorExample.setDocumentSetId(documentSet.getDocumentSetId());
        List<DocumentSetConnector> lstDocumentSetConnector = documentSetConnectorRepository.findAll(Example.of(documentSetConnectorExample));

        // Load Connector from database
        List<Connector> lstConnector = null;
        if (lstDocumentSetConnector != null && lstDocumentSetConnector.size() > 0) {
            lstConnector = lstDocumentSetConnector.stream().map(documentSetConnector -> {
                Connector connectorExample = new Connector();
                connectorExample.setCreatedBy(jwt.getClaimAsString("preferred_username"));
                connectorExample.setConnectorId(documentSetConnector.getConnectorId());
                return connectorRepository.findOne(Example.of(connectorExample)).orElse(null);
            }).toList();
        }

        response.setPersona(persona);
        response.setPrompt(prompt);
        response.setLstConnector(lstConnector);        

        // Return the list of personas as a JSON string
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update-persona")
	public ResponseEntity<?> postUpdatePersona(@RequestBody UpdatePersonaRequest updatePersonaRequest, @AuthenticationPrincipal Jwt jwt) {
        // Initialize username
		String username = jwt.getClaimAsString("preferred_username");
		logger.info("username: {}", username);

		// Load user from database
		User userExample = new User();
		userExample.setUsername(username);
		User user = userRepository.findOne(Example.of(userExample)).orElse(null);

        // Load persona from database
        Persona personaExample = new Persona();
        personaExample.setCreatedBy(username);
        personaExample.setUuid(updatePersonaRequest.getPersonaUuid());
        Persona persona = personaRepository.findOne(Example.of(personaExample)).orElse(null);

        if (persona == null) {
            return ResponseEntity.badRequest().body("{\"message\":\"Persona not found\"}");
        }

         // Load prompt from database
        Prompt promptExample = new Prompt();
        promptExample.setCreatedBy(username);
        promptExample.setPromptId(persona.getPromptId());
        Prompt prompt = promptRepository.findOne(Example.of(promptExample)).orElse(null);
        
        if (prompt == null) {
            return ResponseEntity.badRequest().body("{\"message\":\"Prompt not found\"}");
        }

        // Update prompt
        PromptUpdateRequest promptUpdateRequest = new PromptUpdateRequest();
        promptUpdateRequest.setName(prompt.getName());
        promptUpdateRequest.setDescription(prompt.getDescription());
        promptUpdateRequest.setShared(true);
        promptUpdateRequest.setSystemPrompt(updatePersonaRequest.getSystemPrompt());
        promptUpdateRequest.setTaskPrompt(updatePersonaRequest.getTaskPrompt());
        promptUpdateRequest.setIncludeCitations(false);

        logger.info("promptUpdateRequest: {}", promptUpdateRequest);
        ResponseEntity<?> updatePromptResponseEntity = updatePrompt(promptUpdateRequest, persona.getPromptId(), user.getFastapiusersauth())
        .flatMap(response -> {
			try {
				// Since we're making it synchronous, return the OK status directly
				return Mono.just(ResponseEntity.ok(HttpStatus.OK));
			} catch (NumberFormatException e) {
				logger.error("Failed to parse documentSetId from response", e);
				return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to run document"));
			}
		})
		.block(); // This blocks until the operation is completed

        if (updatePromptResponseEntity.getStatusCode() != HttpStatus.OK) {
            return ResponseEntity.badRequest().body("{\"message\":\"Failed to update prompt\"}");
        }

        // Update persona
        PersonaUpdateRequest personaUpdateRequest = new PersonaUpdateRequest();
        personaUpdateRequest.setName(updatePersonaRequest.getName());
        personaUpdateRequest.setDescription(updatePersonaRequest.getDescription());
        personaUpdateRequest.setShared(true);
        personaUpdateRequest.setNumChunks(10);
        personaUpdateRequest.setLlmRelevanceFilter(false);
        personaUpdateRequest.setLlmFilterExtraction(false);
        personaUpdateRequest.setRecencyBias("base_decay");
        personaUpdateRequest.setPromptIds(List.of(persona.getPromptId()));
        personaUpdateRequest.setDocumentSetIds(List.of(persona.getDocumentSetId()));
        personaUpdateRequest.setLlmModelVersionOverride(null);
        personaUpdateRequest.setStarterMessages(List.of());
        personaUpdateRequest.setPublic(false);

        logger.info("updatePersonaRequest: {}", updatePersonaRequest);
        ResponseEntity<?> updatePersonaResponseEntity = updatePersona(personaUpdateRequest, persona.getPersonaId(), user.getFastapiusersauth())
        .flatMap(response -> {
			try {
				// Since we're making it synchronous, return the OK status directly
				return Mono.just(ResponseEntity.ok(HttpStatus.OK));
			} catch (NumberFormatException e) {
				logger.error("Failed to parse documentSetId from response", e);
				return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to run document"));
			}
		})
		.block(); // This blocks until the operation is completed

        if (updatePersonaResponseEntity.getStatusCode() != HttpStatus.OK) {
            return ResponseEntity.badRequest().body("{\"message\":\"Failed to update persona\"}");
        }

        // Update persona in database
        persona.setName(updatePersonaRequest.getName());
        persona.setDescription(updatePersonaRequest.getDescription());
        personaRepository.save(persona);

        // Update prompt in database
        Prompt updatePromptExample = new Prompt();
        updatePromptExample.setCreatedBy(username);
        updatePromptExample.setPromptId(persona.getPromptId());
        Prompt updatePrompt = promptRepository.findOne(Example.of(updatePromptExample)).orElse(null);
        updatePrompt.setSystemPrompt(updatePersonaRequest.getSystemPrompt());
        updatePrompt.setTaskPrompt(updatePersonaRequest.getTaskPrompt());
        promptRepository.save(updatePrompt);

		return ResponseEntity.ok("{\"message\":\"Process update persona completed successfully\"}");
	}

    public Mono<String> updatePrompt(PromptUpdateRequest promptUpdateRequest, Integer promptId, String fastapiusersauth) {
        return this.webClient.patch()
                .uri("/api/prompt/" + promptId)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .header("cookie", "fastapiusersauth=" + fastapiusersauth)
                .bodyValue(promptUpdateRequest)
                .retrieve() // Initiate the request
                .bodyToMono(String.class); // Convert the response body to String
    }

    public Mono<String> updatePersona(PersonaUpdateRequest personaUpdateRequest, Integer personaId, String fastapiusersauth) {
        return this.webClient.patch()
                .uri("/api/admin/persona/" + personaId)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .header("cookie", "fastapiusersauth=" + fastapiusersauth)
                .bodyValue(personaUpdateRequest)
                .retrieve() // Initiate the request
                .bodyToMono(String.class); // Convert the response body to String
    }
}
