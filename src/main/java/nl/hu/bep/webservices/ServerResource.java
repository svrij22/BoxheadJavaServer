package nl.hu.bep.webservices;

import nl.hu.bep.model.Player;
import nl.hu.bep.setup.ContextListener;
import nl.hu.bep.setup.JerseyConfig;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.LinkedList;

import static nl.hu.bep.webservices.LogResource.addLog;

@Path("server")
@DeclareRoles({"User", "Admin"})
public class ServerResource {

    @GET
    @Path("reset")
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response doReset(@Context HttpServletRequest request) {
        addLog("[INFO] Resetting Server");
        new JerseyConfig();
        return Response.ok("Server reset").build();
    }

    @GET
    @Path("data")
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response doResetData(@Context HttpServletRequest request) {
        addLog("[INFO] Resetting Data");

        try {
            addLog("[INFO] Attempting to reset players");
            Player.setPlayerData(new LinkedList<>());
            addLog("[INFO] Attempting to remove mounted folder");
            StateWriter.removeObjects();
            addLog("[INFO] Attempting to reset server");
            new JerseyConfig();
            addLog("[INFO] Attempting to reset faux data");
            ContextListener.startServer();

        } catch (Exception e) {
            addLog("[ERROR] " + Arrays.toString(e.getStackTrace()));
        }
        return Response.ok("Data reset").build();
    }

    @GET
    @Path("save")
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response trySave(@Context HttpServletRequest request) {
        addLog("[INFO] Attempting to save");
        return Response.ok(StateWriter.writeObjects()).build();
    }

    @GET
    @Path("read")
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response tryRead(@Context HttpServletRequest request) {
        addLog("[INFO] Attempting to read");
        LinkedList players = StateWriter.readObjects();
        Player.setPlayerData(players);
        return Response.ok(players).build();
    }
}
