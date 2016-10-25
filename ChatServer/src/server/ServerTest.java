package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Test class.
 */
public class ServerTest {
    public static void main(String[] args) {
        String host = null;

        try {
            host = InetAddress.getLocalHost().toString();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        assert host != null;
        String ip = host.split("/")[1];
        ChatServer chatServer = new ChatServer(ip, 8001, 8002, "chatserver");
        try {
            chatServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
