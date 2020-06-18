package nl.hu.bep.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ServerManager implements Serializable {

    public static ArrayList<Player> players = new ArrayList<>();
    public static ArrayList<PerformanceLog> serverPerfLog = new ArrayList<>();
    public static ServerManager serverManager;


    public ServerManager() {
        serverManager = this;
    }

    public static ArrayList<PerformanceLog> getServerPerfLog() {
        return serverPerfLog;
    }

    public ServerManager getManager(){
        return (serverManager != null) ? serverManager : new ServerManager();
    }

    public static void addPlayer(Player player){
        if (!players.contains(player)){
            players.add(player);
        }
    }

    public static void writeServerPerfLog(HashMap<?, ?> hashmap){
        serverPerfLog.add(new PerformanceLog(hashmap));
    }

    public static List<Player> getRegisteredPlayers() {
        return players.stream().filter(e->e.hasAccount).collect(Collectors.toList());
    }

    public static List<Player> getPlayers() {
        return players;
    }

    public static void removePlayer(Player player){
        players.remove(player);
    }


    public static void setPlayerData(LinkedList playerData) {
        System.out.println("[INFO] Setting Player Data");
        if (playerData != null){
            players = new ArrayList<>(playerData);
        }
    }

    public static Player getPlayerByIdOrName(String searchstr) {
        for (Player player : players){
            if (player.clientid.equals(searchstr)) {
                return player;
            }
        }
        for (Player player : players){
            if (player.username.equals(searchstr)) {
                return player;
            }
        }
        return null;
    }
}