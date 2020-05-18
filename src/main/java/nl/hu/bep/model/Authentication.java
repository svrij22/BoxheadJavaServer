package nl.hu.bep.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

public class Authentication implements Serializable {

    @JsonIgnore
    public String authkey;
    @JsonIgnore
    private String role;

    public String authname;
    @JsonIgnore
    private Session session;

    public Authentication(String authkey, String authname, String role) {
        this.authkey = authkey;
        this.authname = authname;
        this.role = role;
    }

    @JsonIgnore
    public Session getSession() {
        if (session == null) return null;
        if (!session.isValid()) return null;
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public boolean doAuth(String authkey, String username){
        return (this.authkey.equals(authkey) && this.authname.equals(username));
    }

    @JsonIgnore
    public String getRole(){
        return this.role;
    }

    public String getName() {
        return authname;
    }
}
