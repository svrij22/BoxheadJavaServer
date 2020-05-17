package nl.hu.bep.setup;

import nl.hu.bep.shopping.model.service.Authentication;
import nl.hu.bep.shopping.model.service.Message;
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
        setFauxData();
    }

    public static void setFauxData(){
        Player player = new Player("svrij22", "1234", new LinkedHashMap());
        player.setAuth(new Authentication("crHzIChy6YxU6PbdSTI5ag5M2eNOs5jh4ogPuo4ip0TOwQrbFAk/oPlMy9ze5OxhoZDUP+3vkG/y/6PcAaJCwg==", "svrij22", true));


        Message message = new Message("Test message", "Test", null, player);
        new Message("Server message", "test");
        message.send();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("terminating application");
    }

}
