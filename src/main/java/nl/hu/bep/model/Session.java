package nl.hu.bep.model;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;

public class Session implements Serializable {
    private static final SecureRandom secureRandom = new SecureRandom(); //threadsafe
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder(); //threadsafe

    private String sessionToken;

    private LocalDateTime expire;

    private static ArrayList<Session> sessionsList = new ArrayList<>();

    public Session() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        this.sessionToken = base64Encoder.encodeToString(randomBytes);
        this.expire = LocalDateTime.now().plusMinutes(10);
    }

    public boolean checkSession(String sessionid){
        return (this.sessionToken.equals(sessionid) && this.isValid());
    }

    public boolean isValid(){
        return LocalDateTime.now().isBefore(this.expire);
    }

    public String getSessionToken() {
        return sessionToken;
    }

    private static ArrayList<Session> getSessions() {
        return sessionsList;
    }
}