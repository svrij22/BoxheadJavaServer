package nl.hu.bep.webservices;

import com.jcraft.jsch.JSchException;
import nl.hu.bep.model.Session;
import nl.hu.bep.setup.JerseyConfig;
import nl.hu.bep.setup.MyServletContextListener;
import nl.hu.bep.model.Authentication;
import nl.hu.bep.model.Player;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.swing.*;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.Timer;

import static nl.hu.bep.webservices.BoxheadServer.addLog;

@Path("game")
@DeclareRoles({"User", "Admin"})
public class BoxheadServer {

    private static StringBuilder logString = new StringBuilder();

    public static void startServer(){
        //Reset StringBuilder
        logString = new StringBuilder();

        addLog("[INFO] Starting Server v1.0.0");
        addLog("[INFO] Attempting Read Server State");

        LinkedList<Player> players = StateWriter.readObjects();
        Player.setPlayerData(players);

        addLog("[INFO] Setting Update Timer");
        Timer timer = new Timer();
        timer.schedule(new doPlayerUpdateTimer(), 0, 30 * 1000);
    }

    @GET
    @Path("login")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response doLogin(@Context HttpServletRequest request, @Context SecurityContext securityContext){
        Player player = (Player) securityContext.getUserPrincipal();
        if (player != null) {
            String token = player.getSessionToken();
            return Response.ok(token).build();
        }
        return Response.status(409).build();
    }

    @GET
    @Path("register")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response doReg(@Context HttpServletRequest request){
        addLog("[INFO] Registering user");

        String name = "";
        String auth = "";
        String regkey = "";

        if (name.length() < 4) return Response.status(428).entity("Fill in info").build();
        if (auth.length() < 10) return Response.status(428).entity("Fill in info").build();

        if (Player.usernameExists(name)) return Response.status(409).entity("Username already taken").build();

        Player player = Player.getPlayerById(regkey);
        if (player == null) return Response.status(404).entity("Player not found").build();

        if (player.setAuth(new Authentication(auth, name, "User"))) {
            addLog("[SUCCESS] Player Registered");
            return Response.ok("Registered!").build();
        }
        else return Response.status(401).entity("Register Key already taken").build();
    }

    @GET
    @Path("registered")
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response doGetAllReg(@Context HttpServletRequest request){
        addLog("[INFO] Requesting registered players");
        return Response.ok(Player.getRegisteredPlayers()).build();
    }

    @GET
    @Path("startshell")
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response doStartShell(@Context HttpServletRequest request) throws JSchException, IOException {
        addLog("[INFO] Attempting to start shell");

        SshConnectionManager.runCommand("uname -v");
        addLog("[INFO] Done.. doing return");
        return Response.ok(SshConnectionManager.getOutput()).build();
    }

    @GET
    @Path("shellexec")
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response doExecShell(@Context HttpServletRequest request){
        addLog("[INFO] Attempting to run shell command");

        //Testing
        try{
            return Response.ok("This function has been disabled for security reasons.").build();
        }catch (Exception e){
            e.printStackTrace();
        }

        try{
            SshConnectionManager.runCommand(request.getHeader("command"));
            addLog("[INFO] Done.. returning");
            return Response.ok(SshConnectionManager.getOutput()).build();
        }catch (Exception e){
            return Response.ok("Error running shell "+ Arrays.toString(e.getStackTrace())).build();
        }
    }

    @GET
    @Path("shell")
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getShellOutput(@Context HttpServletRequest request){
        addLog("[INFO] Attempting to get shell output");

        try{
            return Response.ok(SshConnectionManager.getOutput()).build();
        }catch (Exception e){
            return Response.ok("Error getting output "+ Arrays.toString(e.getStackTrace())).build();
        }
    }

    @GET
    @Path("shellreset")
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response doShellReset(@Context HttpServletRequest request){
        addLog("[INFO] Attempting to reset shell");

        try{
            SshConnectionManager.close();
            SshConnectionManager.runCommand("uname -v");
            addLog("[INFO] Done.. doing return");
            return Response.ok(SshConnectionManager.getOutput()).build();
        }catch (Exception e){
            return Response.ok("Error getting output "+ Arrays.toString(e.getStackTrace())).build();
        }
    }

    @GET
    @Path("info")
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response doCallInfo(@Context HttpServletRequest request){
        addLog("[INFO] Getting Server Info");
        return Response.ok(doRequest("dataFile.json")).build();
    }

    @GET
    @Path("resetserver")
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response doReset(@Context HttpServletRequest request){
        addLog("[INFO] Resetting Server");
        new JerseyConfig();
        return Response.ok("Server reset").build();
    }

    @GET
    @Path("resetdata")
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response doResetData(@Context HttpServletRequest request){
        addLog("[INFO] Resetting Data");

        try{
            addLog("[INFO] Attempting to reset players");
            Player.setPlayerData(new LinkedList<>());
            addLog("[INFO] Attempting to remove mounted folder");
            StateWriter.removeObjects();
            addLog("[INFO] Attempting to reset server");
            new JerseyConfig();
            addLog("[INFO] Attempting to reset faux data");
            MyServletContextListener.setFauxData();

        }catch (Exception e){
            addLog("[ERROR] "+ Arrays.toString(e.getStackTrace()));
        }
        return Response.ok("Data reset").build();
    }

    @GET
    @Path("save")
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response trySave(@Context HttpServletRequest request){
        addLog("[INFO] Attempting to save");
        return Response.ok(StateWriter.writeObjects()).build();
    }

    @GET
    @Path("read")
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response tryRead(@Context HttpServletRequest request){
        addLog("[INFO] Attempting to read");
        LinkedList players = StateWriter.readObjects();
        Player.setPlayerData(players);
        return Response.ok(players).build();
    }

    @GET
    @Path("serverlog")
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getServerLog(@Context HttpServletRequest request){
        addLog("[INFO] Getting Server Log");
        return Response.ok(logString.toString()).build();
    }

    public static void addLog(String str){
        System.out.println(str);
        logString.append(str + "\n");
    }

    @GET
    @Path("player")
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response doCallPlayer(@Context HttpServletRequest request){
        addLog("[INFO] Getting Player Data");

        return Response.ok(doRequest("playerData.json")).build();
    }

    @GET
    @Path("playerdata")
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Player> doCallJavaPlayer(@Context HttpServletRequest request){
        addLog("[INFO] Getting Player Data Java");
        return Player.players;
    }

    @GET
    @Path("messages")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response doGetMessages(@Context HttpServletRequest request, @Context SecurityContext securityContext){
        addLog("[INFO] Getting Message Data Java");
        Player player = (Player) securityContext.getUserPrincipal();
        if (player != null) {
            return Response.ok(player.getMessages()).build();
        }
        return Response.status(409).build();
    }

    @GET
    @Path("json")
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response doJsonParse(@Context HttpServletRequest request){
        addLog("[INFO] Getting Player Json");

        String req = doRequest("dataFile.json");
        try{
            Object object1 = JSONValue.parse(req);
            JSONObject obj = (JSONObject)object1;
            return Response.ok(obj.toJSONString()).build();
        }   catch (Exception e) {
            addLog("[ERROR] " + Arrays.toString(e.getStackTrace()));
            return Response.ok(Arrays.toString(e.getStackTrace())).build();
        }
    }

    @GET
    @Path("parsed")
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response doParsedGet(@Context HttpServletRequest request){
        addLog("[INFO] Getting Parsed Info");

        String req = doRequest("dataFile.json");
        StringBuilder stringBuilder = new StringBuilder();

        Map parsed = parseJson(req);
        if (parsed != null){
            parsed.forEach((k,v)->stringBuilder.append("Key : " + k + " Value : " + v + "\n"));
            return Response.ok(stringBuilder.toString()).build();
        }else{
            addLog("[WARNING] Parsed info is empty");
            return Response.ok("null").build();
        }
    }

    @GET
    @Path("sockets")
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response doSocketGet(@Context HttpServletRequest request){
        addLog("[INFO] Getting Sockets");

        String req = doRequest("dataFile.json");
        Map parsed = parseJson(req);
        if (parsed != null){
            LinkedList sockets = (LinkedList) parsed.get("sockets");
            return Response.ok(sockets).build();
        }else{
            addLog("[INFO] Parsed Info Empty");
            return Response.ok("null").build();
        }
    }

    @GET
    @Path("playerUpdate")
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response doPlayerJavaUpdate(@Context HttpServletRequest request){
        addLog("[INFO] Updating Player Info");

        //Do player update
        try{
            doPlayerUpdate();
            return Response.ok(Player.players).build();
        }catch (Exception e){
            addLog("[ERROR] "+ Arrays.toString(e.getStackTrace()));
            return Response.ok(e.getMessage()).build();
        }
    }

    public static void doPlayerUpdate() throws Exception{

        String req = doRequest("playerData.json");
        StringBuilder stringBuilder = new StringBuilder();
        jsonFactory containerFactory = new jsonFactory();
        LinkedList parsed = (LinkedList) new JSONParser().parse(req, containerFactory);
        System.out.println(parsed.getClass());
        System.out.println(parsed.get(0).getClass());
        for (Object player : parsed){
            LinkedHashMap tmpPlayer = (LinkedHashMap) player;
            String username = (String) tmpPlayer.get("username");
            String clientid = String.valueOf(tmpPlayer.get("clientid"));
            new Player(username, clientid, tmpPlayer);
        }
        addLog("[INFO] Created " + Player.players.size() + " players.");
    }

    @GET
    @Path("players")
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response doGetPlayers(@Context HttpServletRequest request){
        addLog("[INFO] Getting Players Info ");
        return Response.ok(Player.players).build();
    }

    @GET
    @Path("player{id}")
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response doPlayerGetById(@PathParam("id") String id, @Context HttpServletRequest request){
        addLog("[INFO] Getting Player Info " + id);

        String req = doRequest("playerData.json");
        try {
            jsonFactory containerFactory = new jsonFactory();
            LinkedList parsed = (LinkedList) new JSONParser().parse(req, containerFactory);
            Player playerGet = Player.getPlayerById(id);
            if (playerGet != null) return Response.ok(playerGet).build();
            playerGet = Player.getPlayerByName(id);
            if (playerGet != null) return Response.ok(playerGet).build();
        } catch (Exception e) {
            addLog("[ERROR] "+ Arrays.toString(e.getStackTrace()));
            e.printStackTrace();
            return Response.ok(e.getMessage()).build();
        }
        addLog("[WARNING] Player Not Found");
        return Response.ok("Not found").build();
    }

    @GET
    @Path("socket{id}")
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response doSocketGetById(@PathParam("id") int id, @Context HttpServletRequest request){
        addLog("[INFO] Getting socket with ID " + id);

        String req = doRequest("dataFile.json");
        StringBuilder stringBuilder = new StringBuilder();

        Map parsed = parseJson(req);

        try{
            LinkedList sockets = (LinkedList) parsed.get("sockets");
            return Response.ok(sockets.get(id)).build();
        }catch (Exception e){
            addLog("[INFO] Parsed Info Empty");
            return Response.ok("Exception " + e.getMessage()).build();
        }
    }

    public Map parseJson(String str){
        try {
            addLog("[INFO] Try Parsing Json");
            JSONParser parser = new JSONParser();
            jsonFactory containerFactory = new jsonFactory();
            Map item = (Map)parser.parse(str, containerFactory);
            item.forEach((k,v)->System.out.println("Key : " + k + " Value : " + v));
            return item;
        } catch(Exception pe) {
            addLog("[ERROR] "+ Arrays.toString(pe.getStackTrace()));
            pe.printStackTrace();
            return null;
        }
    }

    public static String doRequest(String urladd){
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

        }   catch (Exception e) {
            addLog("[ERROR] " + Arrays.toString(e.getStackTrace()));
            return Arrays.toString(e.getStackTrace());
        }   finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

}

class jsonFactory implements ContainerFactory {
    @Override
    public Map createObjectContainer() {
        return new LinkedHashMap<>();
    }
    @Override
    public List creatArrayContainer() {
        return new LinkedList<>();
    }
}

class doPlayerUpdateTimer extends TimerTask {
    public void run() {
        try {
            addLog("[INFO] Doing Timer Task");
            BoxheadServer.doPlayerUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
