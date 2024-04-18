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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;

import org.keycloak.quickstart.db.entity.User;
import org.keycloak.quickstart.db.repository.UserRepository;
import org.keycloak.quickstart.response.GetUserResponse;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@RestController
@RequestMapping("/user")
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserRepository userRepository;
    
	@GetMapping("/get-user")
    public ResponseEntity<GetUserResponse> getPersonaList(@AuthenticationPrincipal Jwt jwt) throws JsonMappingException, JsonProcessingException {
        GetUserResponse response = new GetUserResponse();

        // Initialize username
		String username = jwt.getClaimAsString("preferred_username");
		logger.info("username: {}", username);

        // Load user from database
        User userExample = new User();
        userExample.setUsername(username);
        Optional<User> user = userRepository.findOne(Example.of(userExample));

        if (user.isPresent()) {
            // Return user information
            logger.info("Return user information: {}", username);

            response.setUuid(user.get().getUuid());
            response.setUsername(user.get().getUsername());
            response.setMaxConnector(user.get().getMaxConnector());
            response.setMaxPersona(user.get().getMaxPersona());
        } else {
            // Register new user
            logger.info("Register new user: {}", username);

            // Create a Date instance
            Date now = new Date();

            // Convert Date to LocalDateTime
            ZonedDateTime zdt = now.toInstant().atZone(ZoneId.systemDefault());
            LocalDateTime localDateTime = zdt.toLocalDateTime();

            // Load master user
            User masterUserExample = new User();
            masterUserExample.setUsername("master");
            Optional<User> masterUser = userRepository.findOne(Example.of(masterUserExample));            

            User newUser = new User();
            newUser.setUuid(java.util.UUID.randomUUID().toString());
            newUser.setCreatedAt(localDateTime);
            newUser.setUsername(username);
            newUser.setUsernameDanswer(masterUser.get().getUsernameDanswer());
            newUser.setMaxConnector(1);
            newUser.setMaxPersona(1);
            newUser.setUsernameFonnte(masterUser.get().getUsernameFonnte());
            newUser.setFastapiusersauth(masterUser.get().getFastapiusersauth());
            newUser.setStatus("Active");
            newUser.setType("Free");
            userRepository.save(newUser);
        }

        // Return the list of personas as a JSON string
        return ResponseEntity.ok(response);
    }
}
