package nl.hu.bep.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class Player implements Serializable {
    public String username;
    public String clientid;

    public LinkedHashMap clientdata;
    public boolean hasAccount;

    private ArrayList<Message> messages = new ArrayList<>();
    public static ArrayList<Player> players = new ArrayList<>();

    public Player(String username, String clientid, LinkedHashMap clientdata) {
        this.username = username;
        this.clientid = clientid;
        this.clientdata = clientdata;

        if (!Player.playerExists(this)){
            players.add(this);
        }
    }

    public static ArrayList<Player> getRegisteredPlayers() {
        return players.stream().filter(e->e.hasAccount).collect(Collectors.toCollection(ArrayList::new));
    }

    public void setHasAccount(boolean hasAccount) {
        this.hasAccount = hasAccount;
    }

    private static boolean playerExists(Player player) {
        for (Player tmpP : Player.players){
            if (tmpP.equals(player)) {
                return true;
            }
        }
        return false;
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

    @Override
    public String toString() {
        return "Player{" +
                "username='" + username + '\'' +
                ", clientid='" + clientid + '\'' +
                ", clientdata=" + clientdata +
                ", messages=" + messages +
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