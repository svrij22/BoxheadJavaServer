package nl.hu.bep.shopping.webservices;/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
/**
 * This program enables you to connect to sshd server and get the shell prompt.
 * $ CLASSPATH=.:../build javac Shell.java
 * $ CLASSPATH=.:../build java Shell
 * You will be asked username, hostname and passwd.
 * If everything works fine, you will get the shell prompt. Output may
 * be ugly because of lacks of terminal-emulation, but you can issue commands.
 */

import com.jcraft.jsch.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;

public class SshShell {

    public static OutputStream output;

    public static void main(String[] arg) throws IOException {

        System.out.println("Test run");
        output = new OutputStream() {
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

        output.write("Secure Shell is Running".getBytes());

        try {
            JSch jsch = new JSch();
            String host = null;
            if (arg.length > 0) {
                host = arg[0];
            } else {
                host = "paneluser@136.144.191.118";
            }

            String user = host.substring(0, host.indexOf('@'));
            host = host.substring(host.indexOf('@') + 1);

            Session session = jsch.getSession(user, host, 22);

            String passwd = "testpassw";
            session.setPassword(passwd);

            UserInfo ui = new MyUserInfo() {
                public void showMessage(String message) {
                    //JOptionPane.showMessageDialog(null, message);
                }

                public boolean promptYesNo(String message) {
                    /*Object[] options={ "yes", "no" };
                    int foo=JOptionPane.showOptionDialog(null,
                            message,
                            "Warning",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.WARNING_MESSAGE,
                            null, options, options[0]);
                    return foo==0;*/
                    return true;
                }
            };

            session.setUserInfo(ui);
            session.connect(30000);   // making a connection with timeout.

            Channel channel = session.openChannel("shell");
            channel.setInputStream(System.in);

            channel.setOutputStream(System.out);
            channel.connect(3 * 1000);

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public String toString() {
        return "SshShell";
    }

    public static String getOutput(){
        return SshShell.output.toString();
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