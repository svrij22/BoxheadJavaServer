package nl.hu.bep.shopping.model.service;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;

public class SessionManager {
    private static ArrayList<HttpSession> sessions = new ArrayList<>();

    public static void addSession(HttpSession hs){
        if (!sessions.contains(hs)){
            sessions.add(hs);
        }
    }

    public static ArrayList<HttpSession> getSessions(){
        return sessions;
    }

    public static boolean sessionLoggedIn(HttpSession hs){
        for (HttpSession htTmp : SessionManager.getSessions()){
            if (htTmp.getId().equals(hs.getId())){
                return true;
            }
        }
        return false;
    }
}