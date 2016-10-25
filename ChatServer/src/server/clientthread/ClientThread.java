package server.clientthread;

import server.ChatServer;
import server.ResponseFormat;

import java.io.*;
import java.net.Socket;
import java.util.*;

/**
 * This thread class handle the client's call.
 *
 * @author Zhanghan Ke
 */
public class ClientThread extends Thread {
    private ChatServer chatServer;

    private Socket messSendSocket;

    private Socket messReceiveSocket;

    private MessSendThread messSendThread;

    private MessReceiveThread messReceiveThread;

    private ResponseFormat respFormat;

    private String addr;

    private String username;

    private int threadID;

    private Map<String, HandleThread> handleThreads;

    private boolean isRunning;

    /**
     * Init client thread
     *
     * @param chatServer
     * @param messSendSocket
     * @param messReceiveSocket
     * @param addr
     * @param threadID
     */
    public ClientThread(ChatServer chatServer, Socket messSendSocket, Socket messReceiveSocket, String addr, int threadID) {
        this.chatServer = chatServer;
        this.messSendSocket = messSendSocket;
        this.messReceiveSocket = messReceiveSocket;
        this.respFormat = new ResponseFormat();
        this.addr = addr;
        this.threadID = threadID;
        this.handleThreads = new HashMap<>();
        this.isRunning = false;
    }

    /**
     * Start client thread
     */
    @Override
    public void run() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(messReceiveSocket.getInputStream()));
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(messSendSocket.getOutputStream()));
            clientInit(br, pw);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        if (isRunning) {
            messReceiveThread = new MessReceiveThread(messReceiveSocket, messSendSocket, chatServer, this);
            messSendThread = new MessSendThread(messReceiveSocket, messSendSocket, chatServer, this);
            messReceiveThread.start();
            messSendThread.start();
        }
    }

    /**
     * Init a client
     *
     * @param br
     * @param pw
     * @throws IOException
     * @throws InterruptedException
     */
    public void clientInit(BufferedReader br, PrintWriter pw) throws IOException, InterruptedException {
        String req;
        String reqType;

        req = br.readLine();
        reqType = chatServer.getReqType(req);
        reqHandle(reqType, req, pw);

        if (isRunning) {
            req = br.readLine();
            reqType = chatServer.getReqType(req);
            reqHandle(reqType, req, pw);
        } else {
            br.close();
            pw.close();
            messSendSocket.close();
            messReceiveSocket.close();
        }
    }

    /**
     * Controller for request
     *
     * @param reqType
     * @param req
     * @param pw
     * @throws InterruptedException
     * @throws IOException
     */
    public void reqHandle(String reqType, String req, PrintWriter pw) throws InterruptedException, IOException {
        String resp;
        boolean tag;
        switch (reqType) {
            case "100":
                String user = req.split("\\|@")[1];
                Map<String, String> u = chatServer.getUserData().getOnlineUsers();
                u.remove(user);
                resp = respFormat.exitResp();
                pw.write(resp);
                pw.flush();
                break;
            case "101":
                boolean loginTag = userCheck(req);
                resp = respFormat.loginResp(loginTag);
                pw.write(resp);
                pw.flush();
                break;
            case "102":
                Map<String, String> allUsers = chatServer.getUserData().getAllUsers();
                Map<String, String> onlineUsers = chatServer.getUserData().getOnlineUsers();
                String userList = userListGet(allUsers, onlineUsers);
                resp = respFormat.listResp(username, userList, chatServer.curTimeGet());
                pw.write(resp);
                pw.flush();
                break;
            case "103":
                tag = true;
                System.out.println(req);
                try {
                    chatServer.getMessQueue().add(req);
                } catch (Exception e) {
                    tag = false;
                }
                resp = respFormat.messSendResp(tag);
                pw.write(resp);
                pw.flush();
                break;
            case "104":
                tag = true;
                System.out.println(req);
                try {
                    chatServer.getMessQueue().add(req);
                } catch (Exception e) {
                    tag = false;
                }
                resp = respFormat.FileSendResp(tag);
                pw.write(resp);
                pw.flush();
                break;
            case "105":
                tag = true;
                System.out.println(req);
                try {
                    chatServer.getMessQueue().add(req);
                } catch (Exception e) {
                    tag = false;
                }
                resp = respFormat.VoiceResp(tag);
                pw.write(resp);
                pw.flush();
                break;
            case "106":
                tag = true;
                System.out.println(req);
                try {
                    chatServer.getMessQueue().add(req);
                } catch (Exception e) {
                    tag = false;
                }
                resp = respFormat.portMessResp(tag);
                pw.write(resp);
                pw.flush();
                break;
            case "107":
                tag = true;
                System.out.println(req);
                try {
                    chatServer.getMessQueue().add(req);
                } catch (Exception e) {
                    tag = false;
                }
                resp = respFormat.portMessResp(tag);
                pw.write(resp);
                pw.flush();
                break;
            case "108":
                String u1 = req.split("\\|@")[1];
                String p1 = req.split("\\|@")[2];
                tag = chatServer.getUserData().addUser(u1, p1);
                resp = respFormat.registerMessResp(tag);
                pw.write(resp);
                pw.flush();
                break;
        }
    }

    /**
     * Check the request
     *
     * @param req
     * @return
     */
    public boolean userCheck(String req) {
        boolean isLogin = false;
        String[] loginReq = req.split("\\|@");
        String username = loginReq[1];
        String password = loginReq[2];
        Map<String, String> allUsers = chatServer.getUserData().getAllUsers();
        Map<String, String> onlineUsers = chatServer.getUserData().getOnlineUsers();
        boolean isOnline = false;

        for (String i : allUsers.keySet()) {
            if (username.equals(i)) {
                for (String i1 : onlineUsers.keySet()) { //�û��Ѿ���¼
                    if (i.equals(i1)) {
                        isOnline = true;
                        break;
                    }
                }
                if (isOnline) {
                    break;
                }
                if (password.equals(allUsers.get(i))) {
                    this.username = i;
                    onlineUsers.put(i, allUsers.get(i));
                    isRunning = true;
                    isLogin = true;  //��½�ɹ�
                    System.out.println("User: " + username + " Login");
                    break;
                }
            }
        }
        return isLogin;
    }

    /**
     * Get user state list
     *
     * @param allUsers
     * @param onlineUsers
     * @return
     */
    private String userListGet(Map allUsers, Map onlineUsers) {
        String userList = "";
        String tag;
        for (Object u : allUsers.keySet()) {
            if (onlineUsers.containsKey(u)) {
                tag = "online";
            } else {
                tag = "offline";
            }
            userList += u + ":" + tag + ",";
        }

        return userList;
    }


    /**
     * return the user name
     *
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     * return whether is running.
     *
     * @return
     */
    public boolean getisRunning() {
        return isRunning;
    }

}
