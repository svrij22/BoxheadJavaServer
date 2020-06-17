package nl.hu.bep.webservices;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.hu.bep.model.Notification;
import nl.hu.bep.model.Player;
import nl.hu.bep.security.Account;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import java.io.IOException;
import java.util.ArrayList;

import static nl.hu.bep.webservices.LogResource.addLog;

@Path("notification")
public class NotificationResource {

    @GET
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response doGetMessages(@Context SecurityContext securityContext) {
        addLog("[INFO] Getting Notifications");

        //Retrieve player
        Account acc = (Account) securityContext.getUserPrincipal();
        Player player = (Player) acc.getPlayer();

        //Clone list and if you are an admin also add the player's responses
        ArrayList<Notification> notifications = (ArrayList<Notification>) player.getNotifications().clone();
        if (acc.getRole().equals("Admin")) notifications.addAll(Notification.getAdminNotif());

        //Return 200 OK
        return Response.ok(notifications).build();
    }

    @DELETE
    @Path("{id}")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteMessage(@Context SecurityContext securityContext, @PathParam("id") String id) throws IOException {

        //Cant believe this works
        //Create fake notification to remove from notification list
        //If you wonder why I didn't just mark the notifications with numbers, I was afraid of the occurance that the wrong message would be deleted
        //when someone hasn't refreshed their notifcation list

        JSONObject object = (JSONObject) JSONValue.parse(id);
        Notification notification = new Notification(object.get("title").toString(), object.get("body").toString(), Notification.IsBy.admin, false);

        //Retrieve player
        Account acc = (Account) securityContext.getUserPrincipal();
        Player player = (Player) acc.getPlayer();

        //Remove from lists
        player.getNotifications().remove(notification);
        Notification.getAdminNotif().remove(notification);

        //Get list again
        ArrayList<Notification> notificationArrayList = (ArrayList<Notification>) player.getNotifications().clone();
        if (acc.getRole().equals("Admin")) notificationArrayList.addAll(Notification.getAdminNotif());

        //Retrieve notification list
        return Response.ok(notificationArrayList).build();
    }

    @PUT
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response readMessages(@Context SecurityContext securityContext) {
        addLog("[INFO] Setting on read Notifications");

        //Retrieve player
        Account acc = (Account) securityContext.getUserPrincipal();
        Player player = (Player) acc.getPlayer();

        //Retrieve all notifications
        ArrayList<Notification> notifications = (ArrayList<Notification>) player.getNotifications();
        if (acc.getRole().equals("Admin")) notifications.addAll(Notification.getAdminNotif());

        //Put all on read
        for (Notification msg : notifications) msg.setRead(true);

        //Return notifications
        return Response.ok(notifications).build();
    }
}
