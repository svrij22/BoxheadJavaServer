package nl.hu.bep.shopping.tests;

import nl.hu.bep.shopping.model.service.Message;
import nl.hu.bep.shopping.model.service.Player;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

class MessageTest {

    Message message1, message2, message3;
    Player player1, player2;

    @BeforeAll
    static void setUp1(){
        System.out.println("Alle attributen zijn public voor de serializer");
    }

    @BeforeEach
    void setUp() {
        Message.clearAllMessages();
        player1 = new Player("User1", "1234", new LinkedHashMap());
        player2 = new Player("User2", "1234", new LinkedHashMap());
        message1 = new Message("Test message", "Test body", player1, player2);
        message2 = new Message("Test message 2", "Test body 2", player2, player1);
        message3 = new Message("Test message 2", "Test body 2", player2, player1);
    }

    @Test
    void messageEquals(){
        assertFalse(message1.equals(message2));
        assertTrue(message2.equals(message3));
    }

    @Test
    void testData() {
        assertEquals(message1.title, "Test message");
        assertEquals(message1.body, "Test body");
        assertEquals(message1.sender, player1);
        assertEquals(message1.recipient, player2);
    }

    @Test
    void getMessages(){
        ArrayList<Message> messages = Message.getMessages();
        assertEquals(0, messages.size());

        message1.send();
        message2.send();

        messages = Message.getMessages();
        assertEquals(messages.size(), 2);
        assertEquals(messages.get(0), message1);
    }

    @Test
    void getMessagesForAll(){
        new Message("For", "all").send();
        assertEquals(Message.getMessagesForAll().size(), 1);
    }

    @Test
    void getMessagesForPlayer(){
        new Message("For", "all").send();
        message1.send();
        message2.send();

        ArrayList<Message> messages = player1.getMessages();
        assertEquals(messages.size(), 2);
    }

    @Test
    void getMessageForPlayer2(){
        message1.send();
        ArrayList<Message> messages = player2.getMessages();
        Message message = messages.get(0);
        assertEquals("Test message", message.title);
    }
}