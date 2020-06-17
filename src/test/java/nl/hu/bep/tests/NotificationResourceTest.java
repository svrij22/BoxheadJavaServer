package nl.hu.bep.tests;

import nl.hu.bep.model.Notification;
import nl.hu.bep.model.ServerManager;
import nl.hu.bep.security.AuthenticationFilter;
import nl.hu.bep.security.AuthenticationResource;
import nl.hu.bep.security.CORSFilter;
import nl.hu.bep.setup.ContextListener;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.glassfish.jersey.test.JerseyTest;
import org.json.simple.JSONObject;
import org.junit.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;
public class NotificationResourceTest extends JerseyTest {

    public String bearer = "Bearer" + AuthenticationResource.createToken("svrij22", "Admin");

    @Override
    protected Application configure() {
        return new TestConfig();
    }

    public void reset(){
        ServerManager.getPlayerByIdOrName("svrij22").clearNotifications();
        ContextListener.startServer();
    }

    @Test
    public void doGetMessages() {
        //Reset to default
        reset();

        //GET RESPONSE
        Response response = target("/notification/")
                .request()
                .header(HttpHeaders.AUTHORIZATION, bearer)
                .get();
        assertEquals("Http Response should be 200: ", Response.Status.OK.getStatusCode(), response.getStatus());

        //CONVERT TO LIST
        ArrayList<Notification> output = response.readEntity(ArrayList.class);
        System.out.println(output);

        //CHECK LENGTH
        assertEquals(5, output.size());
    }

    @Test
    public void deleteMessage() {
        //Reset to default
        reset();

        //GET RESPONSE
        Response response = target("/notification/")
                .request()
                .header(HttpHeaders.AUTHORIZATION, bearer)
                .get();
        assertEquals("Http Response should be 200: ", Response.Status.OK.getStatusCode(), response.getStatus());

        //CONVERT TO LIST
        ArrayList<Notification> output = response.readEntity(ArrayList.class);

        //CHECK LENGTH
        assertEquals(5, output.size());

        //CREATE MAP
        HashMap<Object, Object> map = new HashMap<>();
        map.put("title", "Server message");
        map.put("body", "test1");

        String json = JSONObject.toJSONString(map).replace("\"", "\\\"");

        //TODO Query param fixen!!
        ServerManager.getPlayerByIdOrName("svrij22").removeNotification(
                new Notification("Server message", "test1", Notification.IsBy.admin, false));

        //DO DELETE REQUEST
        response = target("/notification/")
//                .queryParam("id", json)
                .request()
                .header(HttpHeaders.AUTHORIZATION, bearer)
//                .delete();
                .get();

        assertEquals("Http Response should be 200: ", Response.Status.OK.getStatusCode(), response.getStatus());

        //CONVERT TO LIST
        output = response.readEntity(ArrayList.class);
        System.out.println(output);

        //CHECK LENGTH
        assertEquals(4, output.size());
    }

    @Test
    public void sendMessage() {
        //
        reset();

        //CREATE MAP
        MultivaluedHashMap<String, String> map = new MultivaluedHashMap<String, String>();
        map.put("title", Collections.singletonList("newTitle"));
        map.put("body", Collections.singletonList("newBody"));

        //GET RESPONSE
        Response response = target("/notification/")
                .request()
                .header(HttpHeaders.AUTHORIZATION, bearer)
                .post(Entity.form(map));

        assertEquals("Http Response should be 200: ", Response.Status.OK.getStatusCode(), response.getStatus());

        //CONVERT TO LIST
        ArrayList<Notification> output = response.readEntity(ArrayList.class);
        System.out.println(output);

        //CHECK LENGTH
        assertEquals(6, output.size());
    }

    @Test
    public void readMessages() {

        //
        reset();

        //GET RESPONSE
        Response response = target("/notification/")
                .request()
                .header(HttpHeaders.AUTHORIZATION, bearer)
                .get();
        assertEquals("Http Response should be 200: ", Response.Status.OK.getStatusCode(), response.getStatus());

        //CONVERT TO LIST
        String output = response.readEntity(String.class);
        System.out.println(output);

        //CHECK LENGTH
        assertTrue(output.contains("\"read\":false"));

        //PUT REQUEST SETS ALL NOTIFICATIONS ON READ RESPONSE
        response = target("/notification/")
                .request()
                .header(HttpHeaders.AUTHORIZATION, bearer)
                .put(Entity.text(""));
        assertEquals("Http Response should be 200: ", Response.Status.OK.getStatusCode(), response.getStatus());

        //CONVERT TO LIST
        output = response.readEntity(String.class);

        //CHECK LENGTH
        assertTrue(output.contains("\"read\":true"));
    }

    @ApplicationPath("/")
    public static class TestConfig extends ResourceConfig {
        public TestConfig() {

            this.register(new CORSFilter());
            this.register(new AuthenticationFilter());
            this.register(RolesAllowedDynamicFeature.class);

            packages("nl.hu.bep.webservices");
            ContextListener.startServer();
        }
    }
}