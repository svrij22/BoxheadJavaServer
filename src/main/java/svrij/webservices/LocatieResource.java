package svrij.webservices;

import svrij.model.ZorgLocatie;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;

@Path("locatie")
@DeclareRoles({"User", "Admin"})
public class LocatieResource {

    @GET
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response doTest() {
        return Response.ok("test").build();
    }

    @GET
    @Path("alle")
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response doTest2() {
        return Response.ok("test").build();
    }


    @POST
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQuery(@Context SecurityContext securityContext,
                             @FormParam("title") String title,
                             @FormParam("body") String body) {

        ArrayList<ZorgLocatie> zorgLocatieList = ZorgLocatie.geefAlle();

        //Return notifications
        return Response.ok(zorgLocatieList).build();
    }

    public static void addLog(String message){
        System.out.println(message);
    }

}
