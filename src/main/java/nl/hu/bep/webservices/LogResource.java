package nl.hu.bep.webservices;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.TimerTask;

@Path("log")
@DeclareRoles({"User", "Admin"})
public class LogResource {

    private static StringBuilder logString = new StringBuilder();

    @GET
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getServerLog() {
        addLog("[INFO] Getting Server Log");
        return Response.ok(getLogString()).build();
    }

    public static void addLog(String str) {
        System.out.println(str);
        logString.append(str + "\n");
    }

    public static String getLogString() {
        return logString.toString();
    }

    public static class doPlayerUpdateTimer extends TimerTask {
        public void run() {
            try {
                addLog("[INFO] Doing Timer Task");
                PlayerResource.doPlayerUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

