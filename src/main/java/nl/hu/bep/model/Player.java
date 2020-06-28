package nl.hu.bep.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.catalina.Server;

import java.io.Serializable;
import java.util.*;

public class Player implements Serializable {
    public String username;
    public String clientid;

    public LinkedHashMap clientdata;
    public boolean hasAccount;

    private ArrayList<Notification> notifications = new ArrayList<>();

    public Player(String username, String clientid, LinkedHashMap clientdata) {
        this.username = username;
        this.clientid = clientid;
        this.clientdata = clientdata;
        ServerManager.addPlayer(this);
    }

    public static void updatePlayer(String username, String clientid, LinkedHashMap clientdata){
        Player player = ServerManager.getPlayerByIdOrName(clientid);
        if (player != null){
            player.clientdata = clientdata;
        }else{
            new Player(username, clientid, clientdata);
        }
    }

    public void setHasAccount(boolean hasAccount) {
        this.hasAccount = hasAccount;
    }

    public boolean removeNotification(Notification notification){
        return notifications.remove(notification);
    }

    public void clearNotifications(){
        notifications = new ArrayList<>();
    }

    public void addNotification(Notification notification){
        if (!notifications.contains(notification) && hasAccount) notifications.add(notification);
    }

    public ArrayList<Notification> getNotifications(){
        return notifications;
    }

    @Override
    public String toString() {
        return "Player{" +
                "username='" + username + '\'' +
                ", clientid='" + clientid + '\'' +
                ", clientdata=" + clientdata +
                ", messages=" + notifications +
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
}