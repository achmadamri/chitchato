package org.keycloak.quickstart.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SendRequest {
    private String no;

    private String message;
}