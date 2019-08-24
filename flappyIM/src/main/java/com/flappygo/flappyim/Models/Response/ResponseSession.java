package com.flappygo.flappyim.Models.Response;

import com.flappygo.flappyim.Models.Server.ChatSession;
import com.flappygo.flappyim.Models.Server.ChatUser;

import java.util.List;

public class ResponseSession extends ChatSession {

    List<ChatUser> users;

    public List<ChatUser> getUsers() {
        return users;
    }

    public void setUsers(List<ChatUser> users) {
        this.users = users;
    }
}
