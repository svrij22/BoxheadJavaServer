package nl.hu.bep.security;

import nl.hu.bep.model.Player;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

public class BoxSecurityContext implements SecurityContext {
    private Account account;
    private String scheme;

    public BoxSecurityContext(Account acc, String scheme) {
        this.account = acc;
        this.scheme = scheme;
    }

    @Override
    public Principal getUserPrincipal() {
        return this.account;
    }

    @Override
    public boolean isUserInRole(String role) {
        return role.equals(account.getRole());
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