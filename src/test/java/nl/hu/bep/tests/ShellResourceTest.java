package nl.hu.bep.tests;

import nl.hu.bep.setup.ContextListener;
import nl.hu.bep.tests.IntegrationTest;
import nl.hu.bep.webservices.LogResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.*;

import static nl.hu.bep.webservices.LogResource.addLog;
import static org.junit.Assert.*;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

public class ShellResourceTest extends JerseyTest {

    @Override
    protected Application configure() {
        return new TestConfig();
    }

    @Test
    public void getSSH() {
        Response response = target("/shell/").request().get();

        assertEquals("Http Response should be 200: ", Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("Http Content-Type should be: ", MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

        String output = response.readEntity(String.class);
        System.out.println(output);

        assertTrue(output.contains(""));
    }

    @Test
    public void startSSH() {
        Response response = target("/shell/start").request().get();

        assertEquals("Http Response should be 200: ", Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("Http Content-Type should be: ", MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

        String output = response.readEntity(String.class);
        System.out.println(output);

        assertTrue(output.contains("paneluser@136-144-191-118"));
    }

    @ApplicationPath("/")
    public static class TestConfig extends ResourceConfig {
        public TestConfig() {
            packages("nl.hu.bep.webservices");
            ContextListener.startServer();
        }
    }
}