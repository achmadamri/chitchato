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
import java.util.Collections;
import java.util.Objects;

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

@RestController
@RequestMapping("/upload")
public class UploadController {

	private final RestTemplate restTemplate;

    public UploadController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

	@GetMapping("/indexing-status")
	public ResponseEntity<String> getIndexingStatus() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", "application/json");
		headers.set("cookie", "fastapiusersauth=dSsPbCPKoZrhohWi1iGvMPNWq9KgMa4gJV7MqoHT6hg");

		HttpEntity<String> entity = new HttpEntity<>(headers);
		return restTemplate.exchange("https://dafba.danswer.ai/api/manage/admin/connector/indexing-status?secondary_index=false", HttpMethod.GET, entity, String.class);
	}

	@PostMapping("/upload-file")
	public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
		// Check if the file is empty or not
		if (file.isEmpty()) {
			return new ResponseEntity<>("Empty file cannot be uploaded", HttpStatus.BAD_REQUEST);
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		headers.set("Accept", "application/json");
		headers.set("cookie", "fastapiusersauth=dSsPbCPKoZrhohWi1iGvMPNWq9KgMa4gJV7MqoHT6hg");

		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("files", new FileSystemResource(convertMultiPartToFile(file)));

		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

		// Make the network call
		ResponseEntity<String> response = restTemplate.postForEntity("https://dafba.danswer.ai/api/manage/admin/connector/file/upload", requestEntity, String.class);

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
	public ResponseEntity<String> createConnector(@RequestBody ConnectorConfig connectorConfig) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Accept", "application/json");
		headers.set("cookie", "fastapiusersauth=dSsPbCPKoZrhohWi1iGvMPNWq9KgMa4gJV7MqoHT6hg");

		HttpEntity<ConnectorConfig> entity = new HttpEntity<>(connectorConfig, headers);
		return restTemplate.exchange("https://dafba.danswer.ai/api/manage/admin/connector", HttpMethod.POST, entity, String.class);
	}

	@PostMapping("/create-credential")
	public ResponseEntity<String> createCredential() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Accept", "application/json");
		headers.set("cookie", "fastapiusersauth=dSsPbCPKoZrhohWi1iGvMPNWq9KgMa4gJV7MqoHT6hg");

		String jsonPayload = "{ \"credential_json\": {}, \"admin_public\": true }";

		HttpEntity<String> entity = new HttpEntity<>(jsonPayload, headers);
		return restTemplate.exchange("https://dafba.danswer.ai/api/manage/credential", HttpMethod.POST, entity, String.class);
	}

	@PutMapping("/update-connector-credential")
	public ResponseEntity<String> updateConnectorCredential() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Accept", "application/json");
		headers.set("cookie", "fastapiusersauth=dSsPbCPKoZrhohWi1iGvMPNWq9KgMa4gJV7MqoHT6hg");

		String jsonPayload = "{ \"name\": \"Prabowo Subianto - Wikipedia bahasa Indonesia, ensiklopedia bebas\", \"is_public\": true }";

		HttpEntity<String> entity = new HttpEntity<>(jsonPayload, headers);
		return restTemplate.exchange("https://dafba.danswer.ai/api/manage/connector/7/credential/7", HttpMethod.PUT, entity, String.class);
	}


}
