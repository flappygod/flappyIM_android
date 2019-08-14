package com.flappygo.flappyim.Session;


import com.flappygo.flappyim.Models.Server.ChatSession;
import com.flappygo.flappyim.Models.Server.ChatUser;


//单聊，只有两个用户
public class SingleSessionData extends ChatSession {


    private ChatUser userOne;

    private ChatUser userTwo;

    public ChatUser getUserOne() {
        return userOne;
    }

    public void setUserOne(ChatUser userOne) {
        this.userOne = userOne;
    }

    public ChatUser getUserTwo() {
        return userTwo;
    }

    public void setUserTwo(ChatUser userTwo) {
        this.userTwo = userTwo;
    }

}
