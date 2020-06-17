package nl.hu.bep.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.catalina.Server;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class Notification implements Serializable{
    private final IsBy isBy;
    private String title;
    private String body;
    private LocalDateTime date;
    private String dateStr;
    private long dateNum;
    private boolean isRead = false;

    public enum IsBy{
        player,
        admin
    };

    @JsonIgnore
    private static ArrayList<Notification> admin_notif = new ArrayList<>();

    public Notification(String title, String body, IsBy isBy, boolean isForAll) {
        this.title = title;
        this.body = body;
        this.isBy = isBy;

        //Date
        this.date = LocalDateTime.now();
        this.dateStr = new SimpleDateFormat("MM-dd HH:mm").format(new Date());
        this.dateNum = new Date().getTime();

        //Admin msg or player msg
        if (!admin_notif.contains(this) && isBy.equals(IsBy.player)) admin_notif.add(this);
        if (isForAll && isBy.equals(IsBy.admin))
            for (Player player : ServerManager.getPlayers()) {
                player.addNotification(this);
            }
    }

    public static ArrayList<Notification> getAdminNotif(){
        return admin_notif;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public IsBy getIsBy() {
        return isBy;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getDateStr() {
        return dateStr;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public long getDateNum() {
        return dateNum;
    }

    public void setDateNum(long dateNum) {
        this.dateNum = dateNum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification notification = (Notification) o;
        return title.equals(notification.title) &&
                body.equals(notification.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, body);
    }
}