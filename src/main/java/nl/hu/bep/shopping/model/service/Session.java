package nl.hu.bep.shopping.model.service;

import javax.servlet.http.HttpServletRequest;
import java.security.SecureRandom;
import java.security.Timestamp;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;

public class Session {
    private static final SecureRandom secureRandom = new SecureRandom(); //threadsafe
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder(); //threadsafe

    private String sessionToken;
    private String sessionHost;

    private LocalDateTime expire;

    private static ArrayList<Session> sessionsList = new ArrayList<>();

    public Session(HttpServletRequest request) {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);

        this.sessionToken = base64Encoder.encodeToString(randomBytes);
        this.sessionHost = request.getRemoteAddr();

        this.expire = LocalDateTime.now().plusMinutes(10);
    }

    public boolean isValid(){
        return LocalDateTime.now().isBefore(this.expire);
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public String getSessionHost() {
        return sessionHost;
    }

    public static boolean sessionExists(String token, HttpServletRequest request){
        for (Session session : Session.getSessions()){
            if (session.getSessionToken().equals(token) && session.getSessionHost().equals(request.getRemoteAddr())){
                return session.isValid();
            }
        }
        return false;
    }

    private static ArrayList<Session> getSessions() {
        return sessionsList;
    }
}