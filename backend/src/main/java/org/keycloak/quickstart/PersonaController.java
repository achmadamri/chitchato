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
import org.keycloak.quickstart.db.entity.Persona;
import org.keycloak.quickstart.db.entity.Prompt;
import org.keycloak.quickstart.db.repository.ConnectorRepository;
import org.keycloak.quickstart.db.repository.PersonaRepository;
import org.keycloak.quickstart.db.repository.PromptRepository;
import org.keycloak.quickstart.request.DocumentSetUpdateRequest;
import org.keycloak.quickstart.request.PromptUpdateRequest;
import org.keycloak.quickstart.response.GetPersonaListResponse;
import org.keycloak.quickstart.response.GetPersonaResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpHeaders;
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
	private ConnectorRepository connectorRepository;

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

        response.setLstPersona(personas);

        // Return the list of personas as a JSON string
        return ResponseEntity.ok(response);
    }

	@GetMapping("/persona")
    public ResponseEntity<GetPersonaResponse> getPersona(@AuthenticationPrincipal Jwt jwt, @RequestParam String uuid) {
        GetPersonaResponse response = new GetPersonaResponse();

        // Load Persona from database
        Persona personaExample = new Persona();
        personaExample.setCreatedBy(jwt.getClaimAsString("preferred_username"));
        personaExample.setUuid(uuid);
        Persona persona = personaRepository.findOne(Example.of(personaExample)).orElse(null);

        // Load Prompt from database
        Prompt promptExample = new Prompt();
        promptExample.setCreatedBy(jwt.getClaimAsString("preferred_username"));
        promptExample.setPromptId(persona.getPromptId());
        Prompt prompt = promptRepository.findOne(Example.of(promptExample)).orElse(null);

        // Load Connector from database
        Connector connectorExample = new Connector();
        connectorExample.setCreatedBy(jwt.getClaimAsString("preferred_username"));
        List<Connector> lstConnector = connectorRepository.findAll(Example.of(connectorExample));

        response.setPersona(persona);
        response.setPrompt(prompt);
        response.setLstConnector(lstConnector);        

        // Return the list of personas as a JSON string
        return ResponseEntity.ok(response);
    }

    // @PostMapping("/update-persona")
	// public ResponseEntity<?> postUpdatePersona(@RequestParam String personaUuid, @AuthenticationPrincipal Jwt jwt) throws JsonMappingException, JsonProcessingException {
		

	// 	return ResponseEntity.ok("{\"message\":\"Process upload document completed successfully\"}");
	// }

    public Mono<String> updatePrompt(PromptUpdateRequest promptUpdateRequest, String promptId, String fastapiusersauth) {
        return this.webClient.patch()
                .uri("/api/prompt/" + promptId)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .header("cookie", "fastapiusersauth=" + fastapiusersauth)
                .bodyValue(promptUpdateRequest)
                .retrieve() // Initiate the request
                .bodyToMono(String.class); // Convert the response body to String
    }

    public Mono<String> updatePersona(PromptUpdateRequest promptUpdateRequest, String personaId, String fastapiusersauth) {
        return this.webClient.patch()
                .uri("/api/admin/persona/" + personaId)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .header("cookie", "fastapiusersauth=" + fastapiusersauth)
                .bodyValue(promptUpdateRequest)
                .retrieve() // Initiate the request
                .bodyToMono(String.class); // Convert the response body to String
    }
}
