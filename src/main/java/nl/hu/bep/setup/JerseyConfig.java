package nl.hu.bep.setup;

import nl.hu.bep.webservices.CORSFilter;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("restservices")
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        this.register(new CORSFilter());
        this.register(RolesAllowedDynamicFeature.class);
        packages("nl.hu.bep.webservices");
    }
}