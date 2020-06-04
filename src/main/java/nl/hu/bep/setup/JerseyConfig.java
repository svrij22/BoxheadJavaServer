package nl.hu.bep.setup;

import nl.hu.bep.security.AuthenticationFilter;
import nl.hu.bep.security.CORSFilter;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("rest")
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        this.register(new CORSFilter());
        this.register(new AuthenticationFilter());

        this.register(RolesAllowedDynamicFeature.class);
        packages("nl.hu.bep.webservices");
    }

    @ApplicationPath("/authentication")
    public static class AuthenticationConfig extends ResourceConfig {
        public AuthenticationConfig() {

            this.register(new CORSFilter());
            this.register(RolesAllowedDynamicFeature.class);

            packages("nl.hu.bep.security");
        }
    }
}