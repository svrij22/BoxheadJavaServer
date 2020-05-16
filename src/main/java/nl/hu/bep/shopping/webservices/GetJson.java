package nl.hu.bep.shopping.webservices;

import com.jcraft.jsch.JSchException;
import nl.hu.bep.setup.JerseyConfig;
import nl.hu.bep.setup.MyServletContextListener;
import nl.hu.bep.shopping.model.service.Message;
import nl.hu.bep.shopping.model.service.SessionManager;
import nl.hu.bep.shopping.model.service.Player;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@Path("game")
public class GetJson {

    private static StringBuilder strBuild = new StringBuilder();

    public static void startServer() {
        //Reset StringBuilder
        strBuild = new StringBuilder();

        addLog("[INFO] Starting Server v1.0.0");
        addLog("[INFO] Attempting Read Server State");

        LinkedList<Player> players = StateWriter.readObjects();
        Player.setPlayerData(players);

    }

    @GET
    @Path("login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response doLogin(@Context HttpServletRequest request){

        String name = request.getHeader("name");
        String auth = request.getHeader("authkey");

        addLog("[INFO] Name " + name);
        addLog("[INFO] Auth " + auth);

        SessionManager.addSession(request.getSession());

        Player player = Player.getPlayerByUsername(name);
        if (player == null) return Response.status(422).entity("User doesn't exist").build();

        String str = request.getSession().getId() + "\n" + auth;
        if (player.doAuth(auth, name)) return Response.ok(str).build();
        else return Response.status(409).build();
    }

    @GET
    @Path("register")
    @Produces(MediaType.APPLICATION_JSON)
    public Response doReg(@Context HttpServletRequest request){

        addLog("[INFO] Registering user");

        String name = request.getHeader("name");
        String auth = request.getHeader("authkey");
        String regkey = request.getHeader("regkey");

        addLog("[INFO] Name " + name);
        addLog("[INFO] Auth " + auth);
        addLog("[INFO] Regkey " + regkey);

        if (name.length() < 4) return Response.status(428).entity("Fill in info").build();
        if (auth.length() < 10) return Response.status(428).entity("Fill in info").build();

        if (Player.usernameExists(name)) return Response.status(409).entity("Username already taken").build();

        Player player = Player.getPlayerById(regkey);
        if (player == null) return Response.status(404).entity("Player not found").build();

        if (player.playerSetAuth(auth, name)) {
            addLog("[SUCCESS] Player Registered");
            return Response.ok("Registered!").build();
        }
        else return Response.status(401).entity("Register Key already taken").build();
    }

    @GET
    @Path("registered")
    @Produces(MediaType.APPLICATION_JSON)
    public Response doGetAllReg(@Context HttpServletRequest request){
        addLog("[INFO] Requesting registered players");
        //Check permissions
        if (!Player.checkPerm(request)) return Response.ok("Access Denied").build();
        return Response.ok(Player.getRegisteredPlayers()).build();
    }

    @GET
    @Path("startshell")
    @Produces(MediaType.APPLICATION_JSON)
    public Response doStartShell(@Context HttpServletRequest request) throws JSchException, IOException {
        addLog("[INFO] Attempting to start shell");
        //Check permissions
        if (!Player.checkPerm(request)) return Response.ok("Access Denied").build();

        SshConnectionManager.runCommand("uname -v");
        addLog("[INFO] Done.. doing return");
        return Response.ok(SshConnectionManager.getOutput()).build();
    }

    @GET
    @Path("shellexec")
    @Produces(MediaType.APPLICATION_JSON)
    public Response doExecShell(@Context HttpServletRequest request){
        addLog("[INFO] Attempting to run shell command");
        //Check permissions
        if (!Player.checkPerm(request)) return Response.ok("Access Denied").build();
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
    @Produces(MediaType.APPLICATION_JSON)
    public Response getShellOutput(@Context HttpServletRequest request){
        addLog("[INFO] Attempting to get shell output");
        //Check permissions
        if (!Player.checkPerm(request)) return Response.ok("Access Denied").build();

        try{
            return Response.ok(SshConnectionManager.getOutput()).build();
        }catch (Exception e){
            return Response.ok("Error getting output "+ Arrays.toString(e.getStackTrace())).build();
        }
    }

    @GET
    @Path("shellreset")
    @Produces(MediaType.APPLICATION_JSON)
    public Response doShellReset(@Context HttpServletRequest request){
        addLog("[INFO] Attempting to reset shell");
        //Check permissions
        if (!Player.checkPerm(request)) return Response.ok("Access Denied").build();

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
    @Produces(MediaType.APPLICATION_JSON)
    public Response doCallInfo(@Context HttpServletRequest request){
        addLog("[INFO] Getting Server Info");
        //Check permissions
        if (!Player.checkPerm(request)) return Response.ok("Access Denied").build();

        return Response.ok(doRequest("dataFile.json")).build();
    }

    @GET
    @Path("resetserver")
    @Produces(MediaType.APPLICATION_JSON)
    public Response doReset(@Context HttpServletRequest request){
        addLog("[INFO] Resetting Server");
        //Check permissions
        if (!Player.checkPerm(request)) return Response.ok("Access Denied").build();
        new JerseyConfig();
        return Response.ok("Server reset").build();
    }

    @GET
    @Path("resetdata")
    @Produces(MediaType.APPLICATION_JSON)
    public Response doResetData(@Context HttpServletRequest request){
        addLog("[INFO] Resetting Data");
        //Check permissions
        if (!Player.checkPerm(request)) return Response.ok("Access Denied").build();

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
    @Produces(MediaType.APPLICATION_JSON)
    public Response trySave(@Context HttpServletRequest request){
        addLog("[INFO] Attempting to save");
        //Check permissions
        if (!Player.checkPerm(request)) return Response.ok("Access Denied").build();

        return Response.ok(StateWriter.writeObjects()).build();
    }

    @GET
    @Path("read")
    @Produces(MediaType.APPLICATION_JSON)
    public Response tryRead(@Context HttpServletRequest request){
        addLog("[INFO] Attempting to read");
        //Check permissions
        if (!Player.checkPerm(request)) return Response.ok("Access Denied").build();

        LinkedList players = StateWriter.readObjects();
        Player.setPlayerData(players);
        return Response.ok(players).build();
    }

    @GET
    @Path("serverlog")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getServerLog(@Context HttpServletRequest request){
        addLog("[INFO] Getting Server Log");
        //Check permissions
        if (!Player.checkPerm(request)) return Response.ok("Access Denied").build();

        return Response.ok(strBuild.toString()).build();
    }

    public static void addLog(String str){
        System.out.println(str);
        strBuild.append(str + "\n");
    }

    @GET
    @Path("player")
    @Produces(MediaType.APPLICATION_JSON)
    public Response doCallPlayer(@Context HttpServletRequest request){
        addLog("[INFO] Getting Player Data");
        //Check permissions
        if (!Player.checkPerm(request)) return Response.ok("Access Denied").build();

        return Response.ok(doRequest("playerData.json")).build();
    }

    @GET
    @Path("playerdata")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Player> doCallJavaPlayer(@Context HttpServletRequest request){
        addLog("[INFO] Getting Player Data Java");
        //Check permissions
        //if (!Player.checkPerm(request)) return Response.ok("Access Denied").build();
        return Player.players;
    }

    @GET
    @Path("messages")
    @Produces(MediaType.APPLICATION_JSON)
    public Response doGetMessages(@Context HttpServletRequest request){
        addLog("[INFO] Getting Message Data Java");
        //Check permissions
        if (!Player.checkPerm(request)) return Response.ok("Access Denied").build();
        Player player = Player.getPlayerByUsername(request.getHeader("name"));
        return Response.ok(player.getMessages()).build();
    }

    @GET
    @Path("json")
    @Produces(MediaType.APPLICATION_JSON)
    public Response doJsonParse(@Context HttpServletRequest request){
        addLog("[INFO] Getting Player Json");
        //Check permissions
        if (!Player.checkPerm(request)) return Response.ok("Access Denied").build();

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
    @Produces(MediaType.APPLICATION_JSON)
    public Response doParsedGet(@Context HttpServletRequest request){
        addLog("[INFO] Getting Parsed Info");
        //Check permissions
        if (!Player.checkPerm(request)) return Response.ok("Access Denied").build();

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
    @Produces(MediaType.APPLICATION_JSON)
    public Response doSocketGet(@Context HttpServletRequest request){
        addLog("[INFO] Getting Sockets");
        //Check permissions
        if (!Player.checkPerm(request)) return Response.ok("Access Denied").build();

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
    @Produces(MediaType.APPLICATION_JSON)
    public Response doPlayerUpdate(@Context HttpServletRequest request){
        addLog("[INFO] Updating Player Info");

        //Check permissions
        if (!Player.checkPerm(request)) return Response.ok("Access Denied").build();

        //Do player update
        try{
            doPlayerUpdate();
            return Response.ok(Player.players).build();
        }catch (Exception e){
            addLog("[ERROR] "+ Arrays.toString(e.getStackTrace()));
            return Response.ok(e.getMessage()).build();
        }
    }

    public void doPlayerUpdate() throws Exception{
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
    @Produces(MediaType.APPLICATION_JSON)
    public Response doGetPlayers(@Context HttpServletRequest request){
        addLog("[INFO] Getting Players Info ");
        //Check permissions
        if (!Player.checkPerm(request)) return Response.ok("Access Denied").build();

        StringBuilder stringBuilder = new StringBuilder("");
        for (Player player : Player.players){
            stringBuilder.append(player);
        }
        return Response.ok(stringBuilder.toString()).build();
    }

    @GET
    @Path("player{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response doSocketGet(@PathParam("id") String id, @Context HttpServletRequest request){
        addLog("[INFO] Getting Player Info " + id);
        //Check permissions
        if (!Player.checkPerm(request)) return Response.ok("Access Denied").build();

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
    @Produces(MediaType.APPLICATION_JSON)
    public Response doSocketGet(@PathParam("id") int id, @Context HttpServletRequest request){
        addLog("[INFO] Getting socket with ID " + id);
        //Check permissions
        if (!Player.checkPerm(request)) return Response.ok("Access Denied").build();

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

    public String doRequest(String urladd){
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