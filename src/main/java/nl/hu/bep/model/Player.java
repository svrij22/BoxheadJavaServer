package nl.hu.bep.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.security.Principal;
import java.util.*;

public class Player implements Serializable, Principal {
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

    public String getSessionToken() {
        Authentication auth = this.getAuth();
        if (auth != null){
            Session session = auth.getSession();
            if (session != null) return session.getSessionToken();
            auth.setSession(new Session());
            return auth.getSession().getSessionToken();
        }
        return null;
    }

    public static boolean usernameExists(String name){
        return (Player.getPlayerByAuthName(name) != null);
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

    @Override
    public String getName() {
        if (!this.hasAuth()) return null;
        return this.getAuth().authname;
    }

    @Override
    public boolean implies(Subject subject) {
        return false;
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