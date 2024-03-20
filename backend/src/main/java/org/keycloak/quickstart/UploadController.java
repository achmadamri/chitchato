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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.keycloak.quickstart.CreateConnectorRequest.ConnectorSpecificConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/upload")
public class UploadController {

	private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

	private String baseUrl = "https://chitchato.danswer.ai/";

	private String fastapiusersauth = "cayVRKhdgA5PdgsFV2fqqxp12UA6h6ueRaJMdhzraZY";

	private final RestTemplate restTemplate;

    public UploadController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

	@GetMapping("/indexing-status")
	public ResponseEntity<String> getIndexingStatus() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", "application/json");
		headers.set("cookie", "fastapiusersauth=" + fastapiusersauth);

		HttpEntity<String> entity = new HttpEntity<>(headers);
		return restTemplate.exchange(baseUrl + "/api/manage/admin/connector/indexing-status?secondary_index=false", HttpMethod.GET, entity, String.class);
	}

	@PostMapping("/upload-file")
	public ResponseEntity<String> uploadFile(@RequestParam("files") MultipartFile file) {
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

	@PostMapping("/create-connector")
	public ResponseEntity<String> createConnector(@RequestBody CreateConnectorRequest createConnectorRequest) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Accept", "application/json");
		headers.set("cookie", "fastapiusersauth=" + fastapiusersauth);

		HttpEntity<CreateConnectorRequest> entity = new HttpEntity<>(createConnectorRequest, headers);
		return restTemplate.exchange(baseUrl + "/api/manage/admin/connector", HttpMethod.POST, entity, String.class);
	}

	@PostMapping("/create-credential")
	public ResponseEntity<String> createCredential(@RequestBody CreateCredentialRequest createCredentialRequest) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Accept", "application/json");
		headers.set("cookie", "fastapiusersauth=" + fastapiusersauth);

		HttpEntity<CreateCredentialRequest> entity = new HttpEntity<>(createCredentialRequest, headers);
		return restTemplate.exchange(baseUrl + "/api/manage/credential", HttpMethod.POST, entity, String.class);
	}

	@PutMapping("/update-connector-credential")
	public ResponseEntity<String> updateConnectorCredential(@RequestBody UpdateConnectorCredentialRequest updateConnectorCredentialRequest) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Accept", "application/json");
		headers.set("cookie", "fastapiusersauth=" + fastapiusersauth);

		HttpEntity<UpdateConnectorCredentialRequest> entity = new HttpEntity<>(updateConnectorCredentialRequest, headers);
		int id = updateConnectorCredentialRequest.getId();
		return restTemplate.exchange(baseUrl + "/api/manage/connector/" + id + "/credential/" + id, HttpMethod.PUT, entity, String.class);
	}

	@PostMapping("/run-connector-once")
	public ResponseEntity<String> runConnectorOnce(@RequestBody RunConnectorOnceRequest runOnceRequest) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Accept", "application/json");
		headers.set("cookie", "fastapiusersauth=" + fastapiusersauth);

		HttpEntity<RunConnectorOnceRequest> entity = new HttpEntity<>(runOnceRequest, headers);
		return restTemplate.exchange(baseUrl + "/api/manage/admin/connector/run-once", HttpMethod.POST, entity, String.class);
	}

	@PostMapping("/combined-endpoint")
	public ResponseEntity<?> combinedEndpoint(@RequestParam("file") MultipartFile file) throws JsonMappingException, JsonProcessingException {
		if (file.isEmpty()) {
			return ResponseEntity.badRequest().body("File is empty");
		}

		// Initialize ObjectMapper for JSON parsing
    	ObjectMapper objectMapper = new ObjectMapper();

		// Step 1: Upload the file
		logger.info("Step 1: Upload the file");
		ResponseEntity<String> uploadResponse = uploadFile(file);
		if (!uploadResponse.getStatusCode().is2xxSuccessful()) {
			return ResponseEntity.status(uploadResponse.getStatusCode()).body("Failed to upload file");
		}
		JsonNode uploadNode = objectMapper.readTree(uploadResponse.getBody());
		String filePaths = uploadNode.get("file_paths").get(0).asText();

		// Assuming filePaths is extracted correctly from the upload response
		// Step 2: Create Connector
		logger.info("Step 2: Create Connector");
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

		ResponseEntity<String> connectorResponse = createConnector(connectorRequest);
		if (!connectorResponse.getStatusCode().is2xxSuccessful()) {
			return ResponseEntity.status(connectorResponse.getStatusCode()).body("Failed to create connector");
		}
		// Extract connector ID from connectorResponse if needed
		JsonNode connectorNode = objectMapper.readTree(connectorResponse.getBody());
        int connectorId = connectorNode.path("id").asInt();  // Adjust the path if necessary based on your actual JSON structure

		// Step 3: Create Credential
		logger.info("Step 3: Create Credential");
		CreateCredentialRequest credentialRequest = new CreateCredentialRequest();
		credentialRequest.setCredentialJson(new HashMap<>());
		credentialRequest.setAdminPublic(true);

		ResponseEntity<String> credentialResponse = createCredential(credentialRequest);
		if (!credentialResponse.getStatusCode().is2xxSuccessful()) {
			return ResponseEntity.status(credentialResponse.getStatusCode()).body("Failed to create credential");
		}
		// Extract credential ID from credentialResponse if needed
		JsonNode credentialNode = objectMapper.readTree(credentialResponse.getBody());
        int credentialId = credentialNode.path("id").asInt();  // Adjust the path if necessary based on your actual JSON structure

		// Step 4: Update Connector Credential
		logger.info("Step 4: Update Connector Credential");
		UpdateConnectorCredentialRequest updateRequest = new UpdateConnectorCredentialRequest();
		updateRequest.setId(credentialId);
		updateRequest.setName("New Credential Name");
		updateRequest.setPublic(true);

		ResponseEntity<String> updateResponse = updateConnectorCredential(updateRequest);
		if (!updateResponse.getStatusCode().is2xxSuccessful()) {
			return ResponseEntity.status(updateResponse.getStatusCode()).body("Failed to update connector credential");
		}

		// Step 5: Run Connector Once
		logger.info("Step 5: Run Connector Once");
		RunConnectorOnceRequest runRequest = new RunConnectorOnceRequest();
		runRequest.setConnectorId(connectorId);
		List<Integer> credentialIds = new ArrayList<>();
		credentialIds.add(credentialId);
		runRequest.setCredentialIds(credentialIds);
		runRequest.setFromBeginning(false);

		ResponseEntity<String> runResponse = runConnectorOnce(runRequest);
		if (!runResponse.getStatusCode().is2xxSuccessful()) {
			return ResponseEntity.status(runResponse.getStatusCode()).body("Failed to run connector");
		}

		return ResponseEntity.ok("Process completed successfully");
	}

}
