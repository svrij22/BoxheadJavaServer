package nl.hu.bep.shopping.model.service;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

public class Authentication implements Serializable {

    @JsonIgnore
    public String authkey;
    public String authname;
    private boolean permissions;
    private Session session;

    public Authentication(String authkey, String authname, boolean permissions) {
        this.authkey = authkey;
        this.authname = authname;
        this.permissions = permissions;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public boolean doAuth(String authkey, String username){
        return (this.authkey.equals(authkey) && this.authname.equals(username));
    }

    public void setPermissions(boolean perm){
        this.permissions = perm;
    }

    public String getName() {
        return authname;
    }

    public boolean hasPerm() {
        return permissions;
    }
}
