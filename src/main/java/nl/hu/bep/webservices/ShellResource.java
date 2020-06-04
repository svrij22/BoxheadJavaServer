package nl.hu.bep.webservices;

import com.jcraft.jsch.JSchException;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Arrays;

import static nl.hu.bep.webservices.LogResource.addLog;

@Path("shell")
@DeclareRoles({"User", "Admin"})
public class ShellResource {

    @GET
    @Path("start")
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response doStartShell(@Context HttpServletRequest request) throws JSchException, IOException {
        addLog("[INFO] Attempting to start shell");

        SshConnectionManager.runCommand("uname -v");
        addLog("[INFO] Done.. doing return");
        return Response.ok(SshConnectionManager.getOutput()).build();
    }

    @GET
    @Path("exec")
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response doExecShell(@Context HttpHeaders headers) {
        addLog("[INFO] Attempting to run shell command");

        //Testing
        try {
            return Response.ok("This function has been disabled for security reasons.").build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            SshConnectionManager.runCommand(headers.getHeaderString("command"));
            addLog("[INFO] Done.. returning");
            return Response.ok(SshConnectionManager.getOutput()).build();
        } catch (Exception e) {
            return Response.ok("Error running shell " + Arrays.toString(e.getStackTrace())).build();
        }
    }

    @GET
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getShellOutput() {
        addLog("[INFO] Attempting to get shell output");

        try {
            return Response.ok(SshConnectionManager.getOutput()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Error getting output " + Arrays.toString(e.getStackTrace())).build();
        }
    }

    @GET
    @Path("reset")
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response doShellReset(@Context HttpServletRequest request) {
        addLog("[INFO] Attempting to reset shell");

        try {
            SshConnectionManager.close();
            SshConnectionManager.runCommand("uname -v");
            addLog("[INFO] Done.. doing return");
            return Response.ok(SshConnectionManager.getOutput()).build();
        } catch (Exception e) {
            return Response.ok("Error getting output " + Arrays.toString(e.getStackTrace())).build();
        }
    }
}