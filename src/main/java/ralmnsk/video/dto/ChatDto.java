package ralmnsk.video.dto;

import ralmnsk.video.model.ChatMessage;
import ralmnsk.video.model.ChatType;
import ralmnsk.video.model.User;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

public class ChatDto {
    private Long chatId;

    private ChatType type;

    private String name;

    private Set<User> users = new HashSet<>();

    private Set<ChatMessage> messages = new HashSet<>();

    public ChatDto() {
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Set<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(Set<ChatMessage> messages) {
        this.messages = messages;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ChatType getType() {
        return type;
    }

    public void setType(ChatType type) {
        this.type = type;
    }
}
