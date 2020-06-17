package nl.hu.bep.shopping.tests;

import nl.hu.bep.model.Notification;
import nl.hu.bep.model.Player;
import nl.hu.bep.model.ServerManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

class MessageTest {

    Notification notification1, notification2, notification3;
    Player player1, player2;

    @BeforeAll
    static void setUp1(){
        System.out.println("Alle attributen zijn public voor de serializer");
    }

    @BeforeEach
    void setUp() {
        player1 = new Player("User1", "1234", new LinkedHashMap());
        player2 = new Player("User2", "1234", new LinkedHashMap());
    }

    @Test
    void messageEquals(){
    }

    @Test
    void testData() {
    }

    @Test
    void getMessages(){
    }

    @Test
    void getMessagesForPlayer(){
    }
}