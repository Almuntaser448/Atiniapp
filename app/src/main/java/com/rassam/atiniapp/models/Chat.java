// Chat.java
package com.rassam.atiniapp.models;

import java.util.List;

public class Chat {
    private String chatId;
    private List<Message> messages;

    public Chat() {}

    public Chat(String chatId, List<Message> messages) {
        this.chatId = chatId;
        this.messages = messages;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
