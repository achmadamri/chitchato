package org.keycloak.quickstart.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GetUserResponse {
    private String uuid;

    private String username;

    private String device;

    private Integer maxConnector;

    private Integer maxPersona;
}