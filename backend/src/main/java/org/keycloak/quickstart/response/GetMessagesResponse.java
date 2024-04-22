package org.keycloak.quickstart.response;

import java.util.List;

import org.keycloak.quickstart.db.entity.UserChat;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GetMessagesResponse {
    private List<UserChat> lstUserChat;
}