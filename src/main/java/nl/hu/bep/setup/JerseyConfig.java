package nl.hu.bep.setup;

import nl.hu.bep.shopping.model.service.Player;
import nl.hu.bep.shopping.webservices.CORSFilter;
import nl.hu.bep.shopping.webservices.GetJson;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;
import java.util.LinkedHashMap;

@ApplicationPath("restservices")
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        this.register(new CORSFilter());
        packages("nl.hu.bep.shopping.webservices");
    }
}