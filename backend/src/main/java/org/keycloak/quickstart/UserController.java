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

@RestController
@RequestMapping("/user")
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserRepository userRepository;
    
	@GetMapping("/get-user")
    public ResponseEntity<GetUserResponse> getPersonaList(@AuthenticationPrincipal Jwt jwt) {
        GetUserResponse response = new GetUserResponse();

        // Initialize username
		String username = jwt.getClaimAsString("preferred_username");
		logger.info("username: {}", username);

        // Load user from database
        User userExample = new User();
        userExample.setUsername(username);
        Optional<User> user = userRepository.findOne(Example.of(userExample));

        if (user.isPresent()) {
            response.setUuid(user.get().getUuid());
            response.setUsername(user.get().getUsername());
            response.setMaxConnector(user.get().getMaxConnector());
            response.setMaxPersona(user.get().getMaxPersona());
        }

        // Return the list of personas as a JSON string
        return ResponseEntity.ok(response);
    }
}
