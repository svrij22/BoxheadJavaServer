package nl.hu.bep.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ServerManager implements Serializable {

    public ArrayList<Player> players = new ArrayList<>();
    public ArrayList<PerformanceLog> serverPerfLog = new ArrayList<>();
    public static ServerManager serverManager;


    public ServerManager() {
        serverManager = this;
    }

    public static ArrayList<PerformanceLog> getServerPerfLog() {
        return ServerManager.getManager().serverPerfLog;
    }

    public static ServerManager getManager(){
        return (serverManager != null) ? serverManager : new ServerManager();
    }

    public static void setManager(ServerManager serverManager) {
        ServerManager.serverManager = serverManager;
    }

    public static void addPlayer(Player player){
        if (!ServerManager.getManager().players.contains(player)){
            ServerManager.getManager().players.add(player);
        }
    }

    public static void writeServerPerfLog(HashMap<?, ?> hashmap){
        ServerManager.getManager().serverPerfLog.add(new PerformanceLog(hashmap));
    }

    public static List<Player> getRegisteredPlayers() {
        return ServerManager.getManager().players.stream().filter(e->e.hasAccount).collect(Collectors.toList());
    }

    public List<Player> getPlayers() {
        return ServerManager.getManager().players;
    }

    public static void removePlayer(Player player){
        ServerManager.getManager().players.remove(player);
    }


    public static void setPlayerData(LinkedList playerData) {
        System.out.println("[INFO] Setting Player Data");
        if (playerData != null){
            ServerManager.getManager().players = new ArrayList<>(playerData);
        }
    }

    public static Player getPlayerByIdOrName(String searchstr) {
        for (Player player : ServerManager.getManager().players){
            if (player.clientid.equals(searchstr)) {
                return player;
            }
        }
        for (Player player : ServerManager.getManager().players){
            if (player.username.equals(searchstr)) {
                return player;
            }
        }
        return null;
    }
}