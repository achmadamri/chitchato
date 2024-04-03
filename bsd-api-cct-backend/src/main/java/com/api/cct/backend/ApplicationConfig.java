package com.api.cct.backend;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class ApplicationConfig {

	@Bean(value = "ObjectMapper")
	public ObjectMapper configObjectMapper() {
		return new ObjectMapper().configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	@Bean
	public Docket apiDocket() {
		String appContexUrl = "/.*";
		return new Docket(DocumentationType.SWAGGER_2).apiInfo(getApiInfo()).useDefaultResponseMessages(false)
				.genericModelSubstitutes(ResponseEntity.class).select().paths(PathSelectors.regex(appContexUrl))
				.apis(RequestHandlerSelectors.any()).paths(PathSelectors.any()).build();
	}

	private ApiInfo getApiInfo() {
		String appName = "bsd-api-cct-backend";
		
		return new ApiInfo(appName, appName, "1.0", "",
				new Contact(appName, appName, "EMAIL"), "LICENSE", "LICENSE URL",
				Collections.emptyList());
	}

	@Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
