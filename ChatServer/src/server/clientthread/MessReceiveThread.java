package server.clientthread;

import server.ChatServer;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

/**
 * MainReceiveThread
 */
public class MessReceiveThread extends Thread {

    private Socket messReceiveSocket;

    private Socket messSendSocket;

    private ChatServer chatServer;

    private ClientThread clientThread;

    /**
     * Init this thread
     *
     * @param messReceiveThread
     * @param messSendSocket
     * @param chatServer
     * @param clientThread
     */
    public MessReceiveThread(Socket messReceiveThread, Socket messSendSocket,
                             ChatServer chatServer, ClientThread clientThread) {
        this.messReceiveSocket = messReceiveThread;
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
        }
    }

    /**
     * Main handle for this thread
     * get other users message from server
     *
     * @throws IOException
     */
    private void threadRun() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(messReceiveSocket.getInputStream()));
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(messSendSocket.getOutputStream()));

        while (clientThread.getisRunning()) {
            BlockingQueue<String> messQueue = chatServer.getMessQueue();
            for (String m : messQueue) {
                String[] mess = m.split("\\|@");
                String receiver = mess[2].split(":")[0];
                if (receiver.equals(clientThread.getUsername())) {  //找到该客户的消息，发送给客户端
                    pw.write(m);
                    pw.flush();
                    chatServer.getMessQueue().remove(m);
                }
            }
            //fixme if message is null?
        }
    }
}
