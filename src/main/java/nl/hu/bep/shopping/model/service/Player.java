package nl.hu.bep.shopping.model.service;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.net.http.HttpRequest;
import java.util.*;

public class Player implements Serializable {
    public String username;
    public String clientid;
    public LinkedHashMap clientdata;

    public static ArrayList<Player> players = new ArrayList<>();

    public String authkey;
    public String authname;
    private boolean permissions;

    public Player(String username, String clientid, LinkedHashMap clientdata) {
        this.username = username;
        this.clientid = clientid;
        this.clientdata = clientdata;

        if (!Player.playerExists(this)){
            players.add(this);
        }
    }

    private static boolean playerExists(Player player) {
        for (Player tmpP : Player.players){
            if (tmpP.equals(player)) {
                return true;
            }
        }
        return false;
    }

    public static ArrayList<Player> getRegisteredPlayers() {
        ArrayList<Player> authplayer = new ArrayList<>();
        for (Player player : players){
            if (player.hasAuth()) authplayer.add(player);
        }
        return authplayer;
    }

    public static List<Player> getPlayers() {
        return players;
    }

    public static void setPlayerData(LinkedList playerData) {
        System.out.println("[INFO] Setting Player Data");
        if (playerData != null){
            players = new ArrayList<>(playerData);
        }
    }

    public boolean hasAuth(){
        return (this.authkey != null);
    }

    public boolean doAuth(String authkey, String username){

        System.out.println("[INFO] P Name " + this.authkey);
        System.out.println("[INFO] P Auth " + this.authname);

        System.out.println("[INFO] Match Name " + this.authname.equals(username));
        System.out.println("[INFO] Match Auth " + this.authkey.equals(authkey));

        return (this.authkey.equals(authkey) && this.authname.equals(username));
    }

    public boolean playerSetAuth(String authkey, String username){
        if (this.authkey == null){
            this.authkey = authkey;
            this.authname = username;
            return true;
        }
        return false;
    }

    public static Player getPlayerById(String id) {
        for (Player player : Player.players){
            if (player.clientid.equals(id)) {
                return player;
            }
        }
        return null;
    }

    public static Player getPlayerByName(String id) {
        for (Player player : Player.players){
            if (player.username.equals(id)) {
                return player;
            }
        }
        return null;
    }

    public void setPermissions(boolean perm){
        this.permissions = perm;
    }

    public static boolean checkPerm(String username, String auth){
        Player player = Player.getPlayerByUsername(username);
        if (player == null) return false;
        if (player.doAuth(auth, username)){
            return (player.permissions);
        }
        return false;
    }

    public static boolean checkPerm(HttpServletRequest request){
        String name = request.getHeader("name");
        String auth = request.getHeader("authkey");

        Player player = Player.getPlayerByUsername(name);
        if (player == null) {
            System.out.println("[WARNING] Access Denied");
            return false;
        }
        if (player.doAuth(auth, name)){
            return (player.permissions);
        }
        System.out.println("[WARNING] Access Denied");
        return false;
    }

    public static boolean usernameExists(String name){
        return (Player.getPlayerByUsername(name) != null);
    }

    public static Player getPlayerByUsername(String nm) {
        for (Player player : players){
            if (player.authname != null) {
                if (player.authname.equals(nm)) {
                    return player;
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Player{" +
                "username='" + username +
                ", clientid='" + clientid +
                ", clientdata=" + clientdata +
                "}\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return clientid.equals(player.clientid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientid);
    }
}