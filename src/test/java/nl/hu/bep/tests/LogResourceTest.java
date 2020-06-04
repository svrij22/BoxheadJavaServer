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

public class LogResourceTest extends JerseyTest {

    @Override
    protected Application configure() {
        return new TestConfig();
    }

    @Test
    public void getServerLog() {
        Response response = target("/log/").request().get();

        assertEquals("Http Response should be 200: ", Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("Http Content-Type should be: ", MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

        String output = response.readEntity(String.class);
        System.out.println(output);

        assertTrue(output.contains("Getting Server Log"));
    }

    @Test
    public void addtoLog(){
        addLog("Test message");
        Response response = target("/log/").request().get();
        String output = response.readEntity(String.class);
        assertTrue(output.contains("Test message"));
    }

    @Test
    public void testLog(){
        addLog("Test message 2");
        String log = LogResource.getLogString();
        assertTrue(log.contains("Test message 2"));
    }

    @ApplicationPath("/")
    public static class TestConfig extends ResourceConfig {
        public TestConfig() {
            ContextListener.startServer();
            packages("nl.hu.bep.webservices");
        }
    }
}