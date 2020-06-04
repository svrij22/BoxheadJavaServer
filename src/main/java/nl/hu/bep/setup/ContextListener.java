package nl.hu.bep.setup;

import nl.hu.bep.model.Message;
import nl.hu.bep.model.Player;
import nl.hu.bep.security.Account;
import nl.hu.bep.webservices.BoxheadServer;
import nl.hu.bep.webservices.LogResource;
import nl.hu.bep.webservices.StateWriter;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Timer;

import static nl.hu.bep.webservices.LogResource.addLog;

@WebListener
public class ContextListener implements ServletContextListener {


    @Override
    public void contextInitialized(ServletContextEvent sce) {
        addLog("[INFO] Starting Server v1.0.0");
        addLog("[INFO] Attempting Read Server State");
        startServer();
    }

    public static void startServer(){
        //Reading objects
        LinkedList<Player> players = StateWriter.readObjects();
        Player.setPlayerData(players);

        //Player
        Player player = new Player("svrij22", "1234", new LinkedHashMap());
        player.setHasAccount(true);
        Account account = new Account("svrij22", "1234", "Admin", player);

        Player player2 = new Player("test", "12345", new LinkedHashMap());

        Message message = new Message("Test message", "Test", null, player);
        new Message("Server message", "test");
        message.send();

        //Update Timer
        addLog("[INFO] Setting Update Timer");
        Timer timer = new Timer();
        timer.schedule(new LogResource.doPlayerUpdateTimer(), 0, 120 * 1000);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("terminating application");
    }

}