package nl.hu.bep.shopping.tests;

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


public class IntegrationTest extends JerseyTest {

    @Override
    protected Application configure() {
        //return new JerseyConfig();
        return new TestConfig();
    }

    @BeforeAll
    public static void init(){

    }

    @Test
    public void getRegisteredPlayers() {
        Response response = target("/game/registered").request().header("command", "test")
                .get();

        assertEquals("Http Response should be 200: ", Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("Http Content-Type should be: ", MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));
        System.out.println(response.toString());
    }

    @Test
    public void getSSH() {
        Response response = target("/game/shellexec").request().header("command", "shellexec")
                .get();

        assertEquals("Http Response should be 200: ", Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("Http Content-Type should be: ", MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));
    }

    @ApplicationPath("/")
    public static class TestConfig extends ResourceConfig {
        public TestConfig() {
            //this.register(new CORSFilter());
            packages("nl.hu.bep.webservices");
        }
    }
}