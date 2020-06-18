package nl.hu.bep.webservices;

import nl.hu.bep.model.Player;
import nl.hu.bep.model.ServerManager;
import org.apache.tomcat.util.http.fileupload.FileUtils;

import java.io.*;
import java.util.LinkedList;

public class StateWriter {

    public static boolean writeObjects(String fname, Object object) {
        try {
            System.out.println("[INFO] Mount attempt");
            File yourFile = new File("mount/" + fname + ".txt");
            try {
                new File("mount").mkdir();
                String path = new File("mount").getAbsolutePath();
                System.out.println("[INFO] Path " + path);
            } catch (Exception e) {
                e.printStackTrace();
            }
            yourFile.createNewFile(); // if file already exists will do nothing
            FileOutputStream oFile = new FileOutputStream(yourFile, false);
            ObjectOutputStream oos = new ObjectOutputStream(oFile);
            oos.writeObject(object);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void removeObjects() throws IOException {
        FileUtils.cleanDirectory(new File("mount"));
    }

    public static Object readObjects(String fname) {
        try {
            File yourFile = new File("mount/" + fname + ".txt");
            if (!yourFile.exists()) return null;
            System.out.println("[INFO] File exists");
            FileInputStream fis = new FileInputStream(yourFile);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Object object = ois.readObject();
            ois.close();
            return object;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}