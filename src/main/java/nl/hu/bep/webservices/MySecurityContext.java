package nl.hu.bep.webservices;

import nl.hu.bep.model.Authentication;
import nl.hu.bep.model.Player;
import nl.hu.bep.model.Session;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

public class MySecurityContext implements SecurityContext {
    private Player player;
    private String scheme;

    public MySecurityContext(Player player, String scheme) {
        this.player = player;
        this.scheme = scheme;
    }

    @Override
    public Principal getUserPrincipal() {
        return this.player;
    }

    @Override
    public boolean isUserInRole(String role) {
        if (player.hasAuth()){
            return player.getAuth().getRole().equals(role);
        }
        return false;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public String getAuthenticationScheme() {
        return null;
    }
}