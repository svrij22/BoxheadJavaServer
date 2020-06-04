package nl.hu.bep.webservices;

import nl.hu.bep.model.Player;
import nl.hu.bep.security.Account;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import static nl.hu.bep.webservices.LogResource.addLog;

@Path("game")
@DeclareRoles({"User", "Admin"})
public class BoxheadServer {

    @GET
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response doTest() {
        return Response.ok("test").build();
    }

    @GET
    @Path("registered")
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response doGetAllReg() {
        addLog("[INFO] Requesting registered players");
        return Response.ok(Player.getRegisteredPlayers()).build();
    }

    @GET
    @Path("info")
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response doCallInfo() {
        addLog("[INFO] Getting Server Info");
        return Response.ok(doRequest("dataFile.json")).build();
    }

    @GET
    @Path("messages")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response doGetMessages(@Context SecurityContext securityContext) {
        addLog("[INFO] Getting Message Data Java");
        Account acc = (Account) securityContext.getUserPrincipal();
        if (acc != null) {
            return Response.ok(acc.getPlayer().getMessages()).build();
        }
        return Response.status(409).build();
    }

    @GET
    @Path("json")
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response doJsonParse() {
        String req = doRequest("dataFile.json");
        try {
            Object object1 = JSONValue.parse(req);
            JSONObject obj = (JSONObject) object1;
            return Response.ok(obj.toJSONString()).build();
        } catch (Exception e) {
            addLog("[ERROR] " + Arrays.toString(e.getStackTrace()));
            return Response.ok(Arrays.toString(e.getStackTrace())).build();
        }
    }

    @GET
    @Path("parsed")
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response doParsedGet() {
        addLog("[INFO] Getting Parsed Info");

        String req = doRequest("dataFile.json");
        StringBuilder stringBuilder = new StringBuilder();

        Map parsed = parseJson(req);
        if (parsed != null) {
            parsed.forEach((k, v) -> stringBuilder.append("Key : " + k + " Value : " + v + "\n"));
            return Response.ok(stringBuilder.toString()).build();
        } else {
            addLog("[WARNING] Parsed info is empty");
            return Response.ok("null").build();
        }
    }

    @GET
    @Path("sockets")
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response doSocketGet() {
        addLog("[INFO] Getting Sockets");

        String req = doRequest("dataFile.json");
        Map parsed = parseJson(req);
        if (parsed != null) {
            LinkedList sockets = (LinkedList) parsed.get("sockets");
            return Response.ok(sockets).build();
        } else {
            addLog("[INFO] Parsed Info Empty");
            return Response.ok("null").build();
        }
    }

    @GET
    @Path("socket{id}")
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response doSocketGetById(@PathParam("id") int id, @Context HttpServletRequest request) {
        addLog("[INFO] Getting socket with ID " + id);

        String req = doRequest("dataFile.json");
        StringBuilder stringBuilder = new StringBuilder();

        Map parsed = parseJson(req);

        try {
            LinkedList sockets = (LinkedList) parsed.get("sockets");
            return Response.ok(sockets.get(id)).build();
        } catch (Exception e) {
            addLog("[INFO] Parsed Info Empty");
            return Response.ok("Exception " + e.getMessage()).build();
        }
    }

    public Map parseJson(String str) {
        try {
            addLog("[INFO] Try Parsing Json");
            JSONParser parser = new JSONParser();
            jsonFactory containerFactory = new jsonFactory();
            Map item = (Map) parser.parse(str, containerFactory);
            item.forEach((k, v) -> System.out.println("Key : " + k + " Value : " + v));
            return item;
        } catch (Exception pe) {
            addLog("[ERROR] " + Arrays.toString(pe.getStackTrace()));
            pe.printStackTrace();
            return null;
        }
    }

    public static String doRequest(String urladd) {
        HttpURLConnection connection = null;
        try {
            addLog("[INFO] Trying HTTP request for " + urladd);
            URL url = new URL("http://136.144.191.118:8090/" + urladd);
            connection = (HttpURLConnection) url.openConnection();

            String userCredentials = "123:123";
            String basicAuth = "auth" + new String(Base64.getEncoder().encode(userCredentials.getBytes()));

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Language", "en-US");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            connection.setRequestProperty("authkey", basicAuth);

            //Send request
            addLog("[INFO] Sending request");
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.close();

            //Get Response
            addLog("[INFO] Getting Response");
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;

            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }

            rd.close();
            return response.toString();

        } catch (Exception e) {
            addLog("[ERROR] " + Arrays.toString(e.getStackTrace()));
            return Arrays.toString(e.getStackTrace());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    static class jsonFactory implements ContainerFactory {
        @Override
        public Map createObjectContainer() {
            return new LinkedHashMap<>();
        }

        @Override
        public List creatArrayContainer() {
            return new LinkedList<>();
        }
    }
}

