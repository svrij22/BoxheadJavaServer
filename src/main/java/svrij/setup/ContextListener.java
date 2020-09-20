package svrij.setup;

import svrij.webservices.LocatieResource;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ContextListener implements ServletContextListener {


    @Override
    public void contextInitialized(ServletContextEvent sce) {
        LocatieResource.addLog("[INFO] Starting Server v1.0.0");
        startServer();
    }

    public static void startServer(){
        LocatieResource.addLog("[INFO] Test");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("terminating application");
    }

}