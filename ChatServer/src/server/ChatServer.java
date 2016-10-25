package server;

import server.clientthread.ClientThread;
import user.UserData;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class is the main server provider.
 *
 * @author Zhanghan Ke
 */
public class ChatServer {

    private String host;

    private int port1;

    private int port2;

    private String name;

    private ServerSocket serverMessReceiveSerSocket;

    private ServerSocket serverMessSendSerSocket;

    private BlockingQueue<String> messQueue;

    private UserData userData;

    private Map<Integer, ClientThread> clientThreads;

    private int threadID;

    private int connectNum;

    private boolean serverRunning;

    /**
     * Init method
     *
     * @param host  The host of this server
     * @param port1 The port using
     * @param port2 The port using
     * @param name  The server's name
     */
    public ChatServer(String host, int port1, int port2, String name) {
        this.host = host;
        this.port1 = port1;
        this.port2 = port2;
        this.name = name;
        this.messQueue = new LinkedBlockingQueue<>();
        this.userData = new UserData();
        this.clientThreads = new HashMap<>();
        this.threadID = 1;
        this.connectNum = 0;
        this.serverRunning = true;
    }

    /**
     * Open server
     * Wait user connect, receive login in mess
     */
    public void start() throws IOException {
        serverMessSendSerSocket = new ServerSocket(port1);  //8001 发送
        serverMessReceiveSerSocket = new ServerSocket(port2);

        System.out.println("[*] Chat server start...");
        System.out.println("[*] listing on:(" + host + ":" + port1 + ") and (" + host + ":" + port2 + ")");
        while (serverRunning) {
            Socket messReceiveSocket = serverMessReceiveSerSocket.accept();
            Socket messSendSocket = serverMessSendSerSocket.accept();
            String cliAddr = messReceiveSocket.getInetAddress().toString();
            System.out.println("\n[*] Accepter connection from: " + cliAddr);
            //开启新用户线程
            ClientThread clientThread = new ClientThread(this, messSendSocket, messReceiveSocket, cliAddr, threadID);
            clientThread.start();
            threadID += 1;
            connectNum += 1;
            clientThreads.put(threadID, clientThread);
        }
    }

    /**
     * Get current time
     *
     * @return
     */
    public String curTimeGet() {
        SimpleDateFormat dateFormater = new SimpleDateFormat("MMM dd yyyy HH:mm:ss", new Locale("en"));
        String time = dateFormater.format(new Date());
        return time;
    }

    /**
     * Handle raw request
     *
     * @param req
     * @return
     */
    public String getReqType(String req) {
        String reqType = req.split("\\|@")[0];
        return reqType;
    }

    public UserData getUserData() {
        return userData;
    }

    public BlockingQueue<String> getMessQueue() {
        return messQueue;
    }
}
