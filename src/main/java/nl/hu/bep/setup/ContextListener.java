package nl.hu.bep.setup;

import nl.hu.bep.model.Authentication;
import nl.hu.bep.model.Message;
import nl.hu.bep.model.Player;
import nl.hu.bep.webservices.BoxheadServer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.LinkedHashMap;

@WebListener
public class ContextListener implements ServletContextListener {


    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("initializing application");
        BoxheadServer.startServer();
        setFauxData();
    }

    public static void setFauxData(){
        Player player = new Player("svrij22", "1234", new LinkedHashMap());
        player.setAuth(new Authentication("crHzIChy6YxU6PbdSTI5ag5M2eNOs5jh4ogPuo4ip0TOwQrbFAk/oPlMy9ze5OxhoZDUP+3vkG/y/6PcAaJCwg==", "svrij22", "Admin"));

        Message message = new Message("Test message", "Test", null, player);
        new Message("Server message", "test");
        message.send();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("terminating application");
    }

}