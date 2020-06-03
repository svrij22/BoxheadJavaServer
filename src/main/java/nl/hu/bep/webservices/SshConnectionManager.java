package nl.hu.bep.webservices;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.InputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SshConnectionManager {

    private static Session session;
    private static ChannelShell channel;

    private static String username = "paneluser";
    private static String password = "testpassw";
    private static String hostname = "136.144.191.118";

    private static StringWriter stringWriter = new StringWriter();

    private static Session getSession() {
        if (session == null || !session.isConnected()) {
            session = connect(hostname, username, password);
        }
        return session;
    }

    private static Channel getChannel() {
        if (channel == null || !channel.isConnected()) {
            try {
                channel = (ChannelShell) getSession().openChannel("shell");
                channel.connect();
            } catch (Exception e) {
                System.out.println("Error while opening channel: " + e);
            }
        }
        return channel;
    }

    private static Session connect(String hostname, String username, String password) {

        JSch jSch = new JSch();

        try {

            session = jSch.getSession(username, hostname, 22);
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.setPassword(password);

            System.out.println("Connecting SSH to " + hostname + " - Please wait for few seconds... ");
            session.connect();
            System.out.println("Connected!");
        } catch (Exception e) {
            System.out.println("An error occurred while connecting to " + hostname + ": " + e);
        }

        return session;

    }

    private static void executeCommands(List<String> commands) {

        try {
            Channel channel = getChannel();

            System.out.println("Sending commands...");
            sendCommands(channel, commands);

            readChannelOutput(channel);
            System.out.println("Finished sending commands!");

        } catch (Exception e) {
            System.out.println("An error ocurred during executeCommands: " + e);
        }
    }

    private static void executeCommand(String cmd) {

        try {
            Channel channel = getChannel();

            System.out.println("Sending commands...");

            sendCommand(channel, cmd);
            readChannelOutput(channel);

            System.out.println("Finished sending commands!");

        } catch (Exception e) {
            System.out.println("An error ocurred during executeCommands: " + e);
        }
    }

    private static void sendCommands(Channel channel, List<String> commands) {

        try {
            PrintStream out = new PrintStream(channel.getOutputStream());

            out.println("#!/bin/bash");
            for (String command : commands) {
                out.println(command);
            }
            out.println("exit");

            out.flush();
        } catch (Exception e) {
            System.out.println("Error while sending commands: " + e);
        }

    }

    private static void sendCommand(Channel channel, String command) {

        try {
            PrintStream out = new PrintStream(channel.getOutputStream());
            out.println(command);
            out.flush();
        } catch (Exception e) {
            System.out.println("Error while sending commands: " + e);
        }
    }

    private static void readChannelOutput(Channel channel) {

        byte[] buffer = new byte[1024];

        try {
            InputStream in = channel.getInputStream();
            String line = "";
            for (int i2 = 0; i2 < 3; i2++) {
                while (in.available() > 0) {
                    int i = in.read(buffer, 0, 1024);
                    if (i < 0) {
                        break;
                    }
                    line = new String(buffer, 0, i);
                    System.out.println(line);
                    stringWriter.write(line);
                }

                if (line.contains("logout")) {
                    break;
                }

                if (channel.isClosed()) {
                    break;
                }
                try {
                    Thread.sleep(250);
                    System.out.println("[INFO] Thread running");
                } catch (Exception ee) {
                }
            }
        } catch (Exception e) {
            System.out.println("Error while reading channel output: " + e);
        }

    }

    public static void close() {
        channel.disconnect();
        session.disconnect();
        stringWriter = new StringWriter();
        System.out.println("Disconnected channel and session");
    }


    public static void main(String[] args) {
        List<String> commands = new ArrayList<String>();
        commands.add("ls -l");

        executeCommands(commands);
        close();
    }

    public static void runCommand(String cmd) {
        executeCommand(cmd);
    }

    public static String getOutput() {
        return stringWriter.toString();
    }
}