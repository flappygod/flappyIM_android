package com.flappygo.flappyim.Models.Response;

import com.flappygo.flappyim.Models.Server.ChatSession;
import com.flappygo.flappyim.Models.Server.ChatUser;

import java.io.Serializable;
import java.util.List;

public class SessionData extends ChatSession implements Serializable {

    List<ChatUser> users;

    public List<ChatUser> getUsers() {
        return users;
    }

    public void setUsers(List<ChatUser> users) {
        this.users = users;
    }
}
