package nl.hu.bep.webservices;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.hu.bep.model.Notification;
import nl.hu.bep.model.Player;
import nl.hu.bep.model.ServerManager;
import nl.hu.bep.security.Account;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

import javax.annotation.security.PermitAll;
import javax.print.attribute.standard.Media;
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
        if (acc == null) return Response.status(Response.Status.FORBIDDEN).build();
        Player player = (Player) acc.getPlayer();
        if (player == null) return Response.status(Response.Status.FORBIDDEN).build();

        //Clone list and if you are an admin also add the player's responses
        ArrayList<Notification> notificationArrayList = this.getPersonalNotifications(acc);

        //Return 200 OK
        return Response.ok(notificationArrayList).build();
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

        //Remove from lists
        ServerManager.getPlayerByIdOrName(acc.getPlayer().clientid).getNotifications().remove(notification);
        if (securityContext.isUserInRole("Admin"))
            Notification.getAdminNotif().remove(notification);

        //Get list again
        ArrayList<Notification> notificationArrayList = this.getPersonalNotifications(acc);

        //Retrieve notification list
        return Response.ok(notificationArrayList).build();
    }

    public ArrayList<Notification> getPersonalNotifications(Account acc){
        ArrayList<Notification> notificationArrayList = (ArrayList<Notification>) ServerManager.getPlayerByIdOrName(acc.getPlayer().clientid).getNotifications().clone();
        if (acc.getRole().equals("Admin")) notificationArrayList.addAll(Notification.getAdminNotif());
        return notificationArrayList;
    }

    @POST
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendMessage(@Context SecurityContext securityContext, @FormParam("title") String title, @FormParam("body") String body) {

        //Retrieve player
        Account acc = (Account) securityContext.getUserPrincipal();
        String role = acc.getRole();

        //Create new notification
        Notification.IsBy isBy = (role.equals("Admin")) ? Notification.IsBy.admin : Notification.IsBy.player;
        new Notification(title, body, isBy, true);

        //Ik weet niet waarom, maar het Account object heeft een duplicate van het echte object.
        ArrayList<Notification> notificationArrayList = this.getPersonalNotifications(acc);

        //Return notifications
        return Response.ok(notificationArrayList).build();
    }

    @PUT
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response readMessages(@Context SecurityContext securityContext) {
        addLog("[INFO] Setting on read Notifications");

        //Retrieve player
        Account acc = (Account) securityContext.getUserPrincipal();

        //Retrieve all notifications
        ArrayList<Notification> notifications = ServerManager.getPlayerByIdOrName(acc.getPlayer().clientid).getNotifications();
        if (acc.getRole().equals("Admin")) notifications.addAll(Notification.getAdminNotif());

        //Put all on read
        for (Notification msg : notifications) msg.setRead(true);

        //Return notifications
        return Response.ok(notifications).build();
    }
}
