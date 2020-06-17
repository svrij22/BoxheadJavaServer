package nl.hu.bep.webservices;

import nl.hu.bep.model.Player;
import nl.hu.bep.model.ServerManager;
import org.apache.catalina.Server;
import org.json.simple.parser.JSONParser;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import static nl.hu.bep.model.ServerManager.players;
import static nl.hu.bep.webservices.BoxheadServer.doRequest;
import static nl.hu.bep.webservices.LogResource.addLog;

@Path("player")
@DeclareRoles({"User", "Admin"})
public class PlayerResource {

    @GET
    @Path("update")
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response doPlayerJavaUpdate(@Context HttpServletRequest request) {
        addLog("[INFO] Updating Player Info");

        //Do player update
        try {
            doPlayerUpdate();
            return Response.ok(players).build();
        } catch (Exception e) {
            addLog("[ERROR] " + Arrays.toString(e.getStackTrace()));
            return Response.ok(e.getMessage()).build();
        }
    }

    public static void doPlayerUpdate() throws Exception {

        String req = doRequest("playerData.json");

        StringBuilder stringBuilder = new StringBuilder();
        BoxheadServer.jsonFactory containerFactory = new BoxheadServer.jsonFactory();
        LinkedList parsed = (LinkedList) new JSONParser().parse(req, containerFactory);

        System.out.println(parsed.getClass());
        System.out.println(parsed.get(0).getClass());

        for (Object player : parsed) {
            LinkedHashMap tmpPlayer = (LinkedHashMap) player;
            String username = (String) tmpPlayer.get("username");
            String clientid = String.valueOf(tmpPlayer.get("clientid"));
            new Player(username, clientid, tmpPlayer);
        }

        addLog("[INFO] Created " + players.size() + " players.");
    }


    //Returns all java player instances
    @GET
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response doGetPlayers(@Context HttpServletRequest request) {
        addLog("[INFO] Getting Players Info");
        return Response.ok(players).build();
    }

    //Returns direct player data JSON from the node server
    @GET
    @Path("json")
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response doCallPlayer(@Context HttpServletRequest request) {
        addLog("[INFO] Getting Player Data");
        return Response.ok(doRequest("playerData.json")).build();
    }

    @GET
    @Path("{id}")
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response doPlayerGetById(@PathParam("id") String id) {
        addLog("[INFO] Getting Player Info By Client ID " + id);

        try {
            Player playerGet = ServerManager.getPlayerByIdOrName(id);
            if (playerGet != null) return Response.ok(playerGet).build();
        } catch (Exception e) {
            addLog("[ERROR] " + Arrays.toString(e.getStackTrace()));
            e.printStackTrace();
            return Response.ok(e.getMessage()).build();
        }
        addLog("[WARNING] Player Not Found");
        return Response.status(Response.Status.NOT_FOUND).entity("Not found").build();
    }

    @DELETE
    @Path("{id}")
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response doPlayerDelete(@PathParam("id") String id) {

        addLog("[INFO] Getting Player Info By Client ID " + id);
        String req = doRequest("playerData.json");

        try {
            Player playerGet = ServerManager.getPlayerByIdOrName(id);
            if (playerGet != null) {
                ServerManager.removePlayer(playerGet);
                return Response.ok(ServerManager.getPlayers()).build();
            }
        } catch (Exception e) {
            addLog("[ERROR] " + Arrays.toString(e.getStackTrace()));
            e.printStackTrace();
            return Response.ok(e.getMessage()).build();
        }
        addLog("[WARNING] Player Not Found");
        return Response.status(Response.Status.NOT_FOUND).entity("Not found").build();
    }
}
