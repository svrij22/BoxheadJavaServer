package sep.api.setup;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("rest")
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        this.register(RolesAllowedDynamicFeature.class);
        packages("sep.api.webservices");
    }
}