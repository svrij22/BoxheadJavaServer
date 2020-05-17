package nl.hu.bep.shopping.tests;

import nl.hu.bep.shopping.model.service.Session;
import org.apache.tomcat.jni.Local;
import org.junit.jupiter.api.BeforeEach;
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