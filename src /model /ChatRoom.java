package model;

import java.util.ArrayList;

public class ChatRoom {

    private ArrayList<Message> messages;

    public ChatRoom() {
        this.messages = new ArrayList<>();
    }

    public void addMessage(Message message) {
        messages.add(message);
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }
}
