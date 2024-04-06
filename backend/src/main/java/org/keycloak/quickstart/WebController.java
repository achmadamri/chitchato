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

import org.keycloak.quickstart.db.entity.Persona;
import org.keycloak.quickstart.db.repository.PersonaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/persona")
public class WebController {

	private static final Logger logger = LoggerFactory.getLogger(WebController.class);

	@Autowired
	private PersonaRepository personaRepository;

	@GetMapping("/list")
    public ResponseEntity<List<Persona>> getIndexingStatus(@AuthenticationPrincipal Jwt jwt) {
        // Load Persona from database
        Persona personaExample = new Persona();
        personaExample.setCreatedBy(jwt.getClaimAsString("preferred_username"));
        List<Persona> personas = personaRepository.findAll(Example.of(personaExample));

        // Return the list of personas as a JSON string
        return ResponseEntity.ok(personas);
    }
}
