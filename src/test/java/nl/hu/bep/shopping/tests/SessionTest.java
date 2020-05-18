package nl.hu.bep.shopping.tests;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SessionTest {

    @Test
    void setUp() {
        LocalDateTime dateTime = LocalDateTime.now().plusMinutes(1);
        assertTrue(LocalDateTime.now().isBefore(dateTime));
    }
}