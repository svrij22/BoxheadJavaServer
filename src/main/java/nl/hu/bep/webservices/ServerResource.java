package nl.hu.bep.webservices;

import nl.hu.bep.model.ServerManager;
import nl.hu.bep.setup.ContextListener;
import nl.hu.bep.setup.JerseyConfig;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import static nl.hu.bep.webservices.BoxheadServer.doRequest;
import static nl.hu.bep.webservices.LogResource.addLog;
import static nl.hu.bep.webservices.LogResource.getLogString;

@Path("server")
@DeclareRoles({"User", "Admin"})
public class ServerResource {

    public static HashMap<?, ?> getPerformanceItems() throws ParseException {

        //Do request
        String req = doRequest("dataFile.json");

        //Create JSON Factory
        BoxheadServer.jsonFactory containerFactory = new BoxheadServer.jsonFactory();
        LinkedHashMap parsed = (LinkedHashMap) new JSONParser().parse(req, containerFactory);

        //Iterate
        HashMap<String, Object> hashmap = new HashMap<>();
        hashmap.put("Ticks", parsed.get("ticks"));
        hashmap.put("Errors", parsed.get("errors"));
        hashmap.put("Total packets", parsed.get("packets"));
        hashmap.put("Unique IP's", parsed.get("uniqueips"));
        hashmap.put("Playtime", parsed.get("playtime"));
        hashmap.put("Memory Usage", parsed.get("memusage"));
        hashmap.put("CPU Usage", parsed.get("cpuusage"));
        hashmap.put("netusage", parsed.get("netusage"));

        return hashmap;
    }

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
            ServerManager.setPlayerData(new LinkedList<>());
            addLog("[INFO] Attempting to remove mounted folder");
            StateWriter.removeObjects();
            addLog("[INFO] Attempting to reset server");
            new JerseyConfig();
            addLog("[INFO] Attempting to reset faux data");
            ContextListener.startServer();
            addLog("[INFO] Server reset");

        } catch (Exception e) {
            addLog("[ERROR] " + Arrays.toString(e.getStackTrace()));
        }
        return Response.ok(getLogString()).build();
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
        ServerManager.setPlayerData(players);
        return Response.ok(players).build();
    }
}
