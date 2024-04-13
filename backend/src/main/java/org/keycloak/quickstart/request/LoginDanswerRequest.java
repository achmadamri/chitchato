package org.keycloak.quickstart.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginDanswerRequest {
    private String email;
    
    private String password;
}
