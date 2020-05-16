package nl.hu.bep.shopping.model.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

class MessageTest {

    @BeforeEach
    void setUp() {
        Player player1 = new Player("User1", "1234", new LinkedHashMap());
        Player player2 = new Player("User2", "1234", new LinkedHashMap());
        Message message = new Message("Test message", "Test body", player1, player2, false);
    }

    @Test
    void setBody() {
    }

    @Test
    void getMessages() {
    }

    @Test
    void getMessagesByUsername() {
    }

    @Test
    void send() {
    }

    @Test
    void testEquals() {
    }

    @Test
    void testHashCode() {
    }
}