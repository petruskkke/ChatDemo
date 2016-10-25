package server.clientthread;

import server.ChatServer;

import java.io.*;
import java.net.Socket;

/**
 * Send message to server
 */
public class MessSendThread extends Thread {

    public Socket messReceiveSocket;

    private Socket messSendSocket;

    private ChatServer chatServer;

    private ClientThread clientThread;

    /**
     * Init this thread
     *
     * @param messReceiveSocket
     * @param messSendSocket
     * @param chatServer
     * @param clientThread
     */
    public MessSendThread(Socket messReceiveSocket, Socket messSendSocket, ChatServer chatServer, ClientThread clientThread) {
        this.messReceiveSocket = messReceiveSocket;
        this.messSendSocket = messSendSocket;
        this.chatServer = chatServer;
        this.clientThread = clientThread;
    }

    /**
     * Start this thread
     */
    @Override
    public void run() {
        try {
            threadRun();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Main handle for this thread
     * send message to server and add to message queue
     *
     * @throws IOException
     * @throws InterruptedException
     */
    private void threadRun() throws IOException, InterruptedException {
        BufferedReader br = new BufferedReader(new InputStreamReader(messReceiveSocket.getInputStream()));
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(messSendSocket.getOutputStream()));

        String req;
        String reqType;
        while (clientThread.getisRunning()) {
            req = messReceive(br);
            reqType = chatServer.getReqType(req);
            clientThread.reqHandle(reqType, req, pw);
        }
    }

    /**
     * Handle when message receive
     * @param br
     * @return
     * @throws IOException
     */
    private String messReceive(BufferedReader br) throws IOException {
        String req = "";
        while (true) {
            String mess = br.readLine();
            req += mess + "\n";

            if (mess.equals("102|@")) {
                break;
            }
            if (mess.startsWith("104|@") || mess.startsWith("105|@") || mess.startsWith("106|@")
                    || mess.startsWith("107|@") || mess.startsWith("100|@") || mess.startsWith("108|@")) {
                break;
            }
            if (mess.endsWith("|@@MESSAGEEND")) {
                break;
            }
        }
        return req;
    }
}
