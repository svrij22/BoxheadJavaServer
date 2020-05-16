package nl.hu.bep.shopping.webservices;

import com.jcraft.jsch.*;

import java.io.*;
import java.util.Properties;

public class SshShellRev {

    public OutputStream output = new OutputStream() {
        private StringBuilder string = new StringBuilder();

        @Override
        public void write(int b) throws IOException {
            this.string.append((char) b );
        }

        //Netbeans IDE automatically overrides this toString()
        public String toString() {
            return this.string.toString();
        }
    };

    public InputStream inputStream = new ByteArrayInputStream("test\n".getBytes());

    Channel channel;
    Session session;

    private String username = "paneluser";
    private String password = "testpassw";
    private String hostname = "136.144.191.118";

    public SshShellRev() {
        System.out.println("Test create");
    }

    public void run() throws IOException {
        System.out.println("Test run");
        output.write("Secure Shell is Running".getBytes());

        try {
            JSch jsch = new JSch();
            session = jsch.getSession(username, hostname, 22);
            session.setPassword(password);

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");  // not recommended
            session.setConfig(config);

            session.connect(30000);   // making a connection with timeout.

            this.exec("test" + "\n");

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public String toString() {
        return "SshShell";
    }

    public String getOutput(){
        if (output != null) return output.toString();
        if (channel != null) return output.toString();
        else return "empty";
    }

    public String exec(String command) {
        try{
            System.out.println("[INFO] Command: " + command);

            if(!session.isConnected())
                this.run();
            channel = session.openChannel("shell");
            inputStream = new ByteArrayInputStream(command.getBytes());
            channel.setInputStream(inputStream);
            output.flush();
            channel.setOutputStream(output);
            channel.connect(3 * 1000);
            System.out.println("Finished sending commands!");

        }catch (Exception e){
            e.printStackTrace();
        }
        return command;
    }

    public static abstract class MyUserInfo
            implements UserInfo, UIKeyboardInteractive {
        public String getPassword() {
            return null;
        }

        public boolean promptYesNo(String str) {
            return false;
        }

        public String getPassphrase() {
            return null;
        }

        public boolean promptPassphrase(String message) {
            return false;
        }

        public boolean promptPassword(String message) {
            return false;
        }

        public void showMessage(String message) {
        }

        public String[] promptKeyboardInteractive(String destination,
                                                  String name,
                                                  String instruction,
                                                  String[] prompt,
                                                  boolean[] echo) {
            return null;
        }
    }

}
