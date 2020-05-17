package nl.hu.bep.shopping.model.service;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

public class Authentication implements Serializable {

    @JsonIgnore
    public String authkey;
    public String authname;
    private boolean permissions;

    public Authentication(String authkey, String authname, boolean permissions) {
        this.authkey = authkey;
        this.authname = authname;
        this.permissions = permissions;
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
