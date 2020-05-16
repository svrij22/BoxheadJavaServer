package nl.hu.bep.setup;

import nl.hu.bep.shopping.model.service.Player;
import nl.hu.bep.shopping.webservices.GetJson;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.LinkedHashMap;

@WebListener
public class MyServletContextListener implements ServletContextListener {


    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("initializing application");
        GetJson.startServer();
        Player player = new Player("svrij22", "1234", new LinkedHashMap());
        player.playerSetAuth("crHzIChy6YxU6PbdSTI5ag5M2eNOs5jh4ogPuo4ip0TOwQrbFAk/oPlMy9ze5OxhoZDUP+3vkG/y/6PcAaJCwg==", "svrij22");
        player.setPermissions(true);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("terminating application");
    }

}
