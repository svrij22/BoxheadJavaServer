package nl.hu.bep.shopping.model.service;

import java.util.ArrayList;
import java.util.Objects;

public class Message {
    private String title;
    private String body;
    private Player sender;
    private Player recipient;
    private boolean toAll;
    private static ArrayList<Message> messages = new ArrayList<>();

    public Message(String title, Player sender, Player recipient, boolean toAll) {
        this.title = title;
        this.sender = sender;
        this.recipient = recipient;
        this.toAll = toAll;
    }

    public void setBody(String body){
        this.body = body;
    }

    public static ArrayList<Message> getMessages(){
        return messages;
    }

    public static ArrayList<Message> getMessagesByUsername(String username){
        ArrayList<Message> allMessages = new ArrayList<>();
        for (Message message : Message.getMessages()){
            if (message.recipient.username.equals(username) || message.toAll){
                allMessages.add(message);
            }
        }
        return allMessages;
    }

    public void send(){
        if (!Message.getMessages().contains(this)) {
            Message.addMessage(this);
        }
    }

    private static void addMessage(Message message) {
        messages.add(message);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return title.equals(message.title) &&
                body.equals(message.body) &&
                sender.equals(message.sender);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, body, sender);
    }
}