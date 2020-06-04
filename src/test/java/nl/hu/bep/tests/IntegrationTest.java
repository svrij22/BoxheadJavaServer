package nl.hu.bep.tests;

import nl.hu.bep.setup.ContextListener;
import nl.hu.bep.webservices.SshConnectionManager;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.*;
import org.junit.jupiter.api.BeforeAll;

import static org.junit.Assert.*;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.LinkedList;


public class IntegrationTest extends JerseyTest {

    @Override
    protected Application configure() {
        return new TestConfig();
    }

    @Test
    public void simpleTest(){
        Response response = target("/game/").request().get();
        String output = response.readEntity(String.class);
        assertEquals("test", output);
    }

    @Test
    public void getRegisteredPlayers() {
        Response response = target("/game/registered").request().header("command", "test").get();

        //Check response
        assertEquals("Http Response should be 200: ", Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("Http Content-Type should be: ", MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

        ArrayList<?> output = response.readEntity(ArrayList.class);
        System.out.println(output.size());

        //Test size if 1 then ok
        assertEquals(1, output.size());
    }

    @Test
    public void doSocketTest() {
        Response response = target("/game/sockets").request().get();

        //Check response
        assertEquals("Http Response should be 200: ", Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("Http Content-Type should be: ", MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

        LinkedList<?> output = response.readEntity(LinkedList.class);

        //Test if contains string
        assertTrue(output.get(0).toString().contains("heartbeat"));
    }

    @ApplicationPath("/")
    public static class TestConfig extends ResourceConfig {
        public TestConfig() {
            ContextListener.startServer();
            packages("nl.hu.bep.webservices");
        }
    }
}