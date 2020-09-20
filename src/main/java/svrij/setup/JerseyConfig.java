package svrij.setup;

import svrij.security.AuthenticationFilter;
import svrij.security.CORSFilter;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("rest")
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        this.register(new CORSFilter());
        this.register(new AuthenticationFilter());
        this.register(RolesAllowedDynamicFeature.class);
        packages("svrij.webservices");
    }

    @ApplicationPath("/authentication")
    public static class AuthenticationConfig extends ResourceConfig {
        public AuthenticationConfig() {
            this.register(new CORSFilter());
            this.register(RolesAllowedDynamicFeature.class);
            packages("svrij.security");
        }
    }
}