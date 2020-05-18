package nl.hu.bep.webservices;

import nl.hu.bep.model.Player;
import org.apache.tomcat.util.http.fileupload.FileUtils;

import java.io.*;
import java.util.LinkedList;

public class StateWriter {

    public static void main(String[] arg) throws IOException {
        writeObjects();
        readObjects();
    }

    public static LinkedList<Player> writeObjects() {
        try{
            System.out.println("[INFO] Mount attempt");
            File yourFile = new File("mount/dataObject.txt");
            try{
                new File("mount").mkdir();
                String path = new File("mount").getAbsolutePath();
                System.out.println("[INFO] Path " + path );
            }catch (Exception e){
                e.printStackTrace();
            }
            yourFile.createNewFile(); // if file already exists will do nothing
            FileOutputStream oFile = new FileOutputStream(yourFile, false);
            ObjectOutputStream oos = new ObjectOutputStream(oFile);
            LinkedList<Player> playersList = new LinkedList<>(Player.getPlayers());
            oos.writeObject(playersList);
            return playersList;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static void removeObjects() throws IOException {
        FileUtils.cleanDirectory(new File("mount"));
    }

    public static LinkedList<Player> readObjects() {
        try{
            File yourFile = new File("mount/dataObject.txt");
            if (!yourFile.exists()) return null;
            System.out.println("[INFO] File exists");
            FileInputStream fis = new FileInputStream(yourFile);
            ObjectInputStream ois = new ObjectInputStream(fis);
            LinkedList<Player> playersTest = (LinkedList<Player>) ois.readObject();
            ois.close();
            return playersTest;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}