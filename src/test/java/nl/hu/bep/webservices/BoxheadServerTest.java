package nl.hu.bep.webservices;

import nl.hu.bep.model.ServerManager;
import nl.hu.bep.setup.ContextListener;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class BoxheadServerTest {

    @BeforeAll
    public static void init(){
        ContextListener.startServer();
    }

    @Test
    void doGetAllReg() {
        BoxheadServer.doGetAllReg();
    }
}