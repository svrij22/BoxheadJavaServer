package nl.hu.bep.shopping.model.service;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.*;

public class Player implements Serializable {
    public String username;
    public String clientid;
    public LinkedHashMap clientdata;

    private ArrayList<Message> messages = new ArrayList<>();
    public static ArrayList<Player> players = new ArrayList<>();
    private Authentication authentication;

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
        return (this.authentication != null);
    }

    public Authentication getAuth(){
        return authentication;
    }

    public boolean doAuth(String auth, String name){
        if (this.hasAuth()){
            return this.getAuth().doAuth(auth, name);
        }
        return false;
    }

    public boolean setAuth(Authentication authentication){
        if (this.authentication == null){
            this.authentication = authentication;
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

    public static Player getPlayerByAuthName(String nm) {
        for (Player player : players){
            if (player.hasAuth()) {
                if (player.getAuth().getName().equals(nm)) {
                    return player;
                }
            }
        }
        return null;
    }

    public static boolean usernameExists(String name){
        return (Player.getPlayerByAuthName(name) != null);
    }

    public static boolean checkPerm(String username, String auth){
        Player player = Player.getPlayerByAuthName(username);

        if (player != null) {
            if (player.getAuth().doAuth(auth, username)){
                return (player.getAuth().hasPerm());
            }
        }

        System.out.println("[WARNING] Access Denied");
        return false;
    }

    public static boolean checkPerm(HttpServletRequest request){
        String name = request.getHeader("name");
        String auth = request.getHeader("authkey");
        return Player.checkPerm(name, auth);
    }

    @Override
    public String toString() {
        return "Player{" +
                "username='" + username + '\'' +
                ", clientid='" + clientid + '\'' +
                ", clientdata=" + clientdata +
                ", messages=" + messages +
                ", authentication=" + authentication +
                '}';
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

    @JsonIgnore
    public ArrayList<Message> getMessages() {
        ArrayList<Message> returnMsg = messages;
        returnMsg.addAll(Message.getMessagesForAll());
        return returnMsg;
    }

    public void addMessage(Message message) {
        this.messages.add(message);
    }
}