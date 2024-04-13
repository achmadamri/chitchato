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
import java.util.Collections;
import java.util.List;

import org.keycloak.quickstart.db.entity.User;
import org.keycloak.quickstart.db.entity.UserDanswer;
import org.keycloak.quickstart.db.repository.UserDanswerRepository;
import org.keycloak.quickstart.db.repository.UserRepository;
import org.keycloak.quickstart.request.LoginDanswerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/scheduler")
public class SchedulerController {

	private static final Logger logger = LoggerFactory.getLogger(SchedulerController.class);

	@Autowired
	private RestTemplate restTemplate;

    @Autowired
	private UserDanswerRepository userDanswerRepository;    

    @Autowired
	private UserRepository userRepository;

    // Schedule a task to run every midnight
    @Scheduled(cron = "0 0 0 * * *")
    // @Scheduled(fixedRate = 5000)
    public void loginDanswerScheduler() {
        logger.info("loginDanswerScheduler :: Execution Time - {}", LocalDateTime.now());

        // Load all user danswer from the database
        List<UserDanswer> userDanswers = userDanswerRepository.findAll();

        // Iterate over all user danswer
        for (UserDanswer userDanswer : userDanswers) {
            // Log the user danswer email
            logger.info("email: {}", userDanswer.getUsername());

            // Log the user danswer password
            logger.info("password: {}", userDanswer.getPassword());

            // Call the loginDanswer method with the user danswer email and password
            ResponseEntity<String> entity = loginDanswer(userDanswer.getUsername(), userDanswer.getPassword());

            // Log the header returned from the loginDanswer method named set-cookie
            logger.info("set-cookie: {}", entity.getHeaders().get("set-cookie"));

            // Example
            // set-cookie = fastapiusersauth=gAirAP360g3Ymi6KJGvv_hE0IjXTFZCSqrBxE_U8n00; Domain=.danswer.ai; HttpOnly; Max-Age=604800; Path=/; SameSite=lax; Secure

            // Log the set-cookie value named fastapiusersauth
            String fastapiusersauth = entity.getHeaders().get("set-cookie").get(0).split(";")[0].split("=")[1];

            // Load user from the database
            User userExample = new User();
            userExample.setUsernameDanswer(userDanswer.getUsername());
            List<User> users = userRepository.findAll(Example.of(userExample));

            // Update the user with the set-cookie value named fastapiusersauth
            // Iterate over all users
            for (User user : users) {
                // Set the user fastapiusersauth value
                user.setFastapiusersauth(fastapiusersauth);

                // Save the user to the database
                userRepository.save(user);
            }
        }

        // Log success
        logger.info("loginDanswerScheduler :: Success");
    }

    public ResponseEntity<String> loginDanswer(String email, String password) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.ALL));
		headers.set("Accept", "application/json");

        LoginDanswerRequest loginDanswerRequest = new LoginDanswerRequest();
        loginDanswerRequest.setEmail(email);
        loginDanswerRequest.setPassword(password);

		HttpEntity<LoginDanswerRequest> entity = new HttpEntity<>(loginDanswerRequest, headers);
		return restTemplate.exchange("https://app.danswer.ai/api/auth/login", HttpMethod.POST, entity, String.class);
	}
}
