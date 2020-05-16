package nl.hu.bep.shopping.model.service;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class Message implements Serializable{
    public String title;
    public String body;
    public Player sender;
    public Player recipient;
    public boolean toAll;

    private static ArrayList<Message> messages = new ArrayList<>();

    public Message(String title, String body, Player sender, Player recipient) {
        this.title = title;
        this.body = body;
        this.sender = sender;
        this.recipient = recipient;
        this.toAll = false;
    }

    public Message(String title, String body) {
        this.title = title;
        this.body = body;
        this.toAll = true;
    }

    public static ArrayList<Message> getMessages(){
        return messages;
    }

    public static ArrayList<Message> getMessagesForAll(){
        ArrayList<Message> allMessages = new ArrayList<>();
        for (Message message : Message.getMessages()){
            if (message.toAll){
                allMessages.add(message);
            }
        }
        return allMessages;
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
            if (!toAll){
                recipient.addMessage(this);
            }
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