package client;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

/**
 * Voice received thread.
 */
public class VoiceReceiveThread extends Thread {

    Client client;

    ServerSocket voiceSendSocket;

    Socket senderSocket;

    String voiceFilename;

    String message;

    String receiver;

    /**
     * Init this thread
     *
     * @param client
     * @param message
     */
    public VoiceReceiveThread(Client client, String message) {
        this.client = client;
        this.message = message;
        this.receiver = message.split("\\|@")[1];   //收到的消息的对方
        this.voiceFilename = message.split("\\|@")[3];
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
     * Main run method for this thread
     *
     * @throws IOException
     */
    private void threadRun() throws IOException {
        int port_high;
        int port_low;
        int port;

        Random ra = new Random();
        String host;
        while (true) {
            port_high = 1 + ra.nextInt(20);
            port_low = 100 + ra.nextInt(1000);
            try {
                host = InetAddress.getLocalHost().toString();
                port = port_high * 256 + port_low;
                voiceSendSocket = new ServerSocket(port);
                host = host.split("/")[1];
                client.portMessSend(receiver, host, port, voiceFilename);
                break;
            } catch (IOException e) {
                continue;
            }
        }
        senderSocket = voiceSendSocket.accept();

        // start transfer file
        File file = new File(System.getProperty("user.dir") + "/src/client/receivevoice/" + voiceFilename);
        if (!file.exists()) {
            file.createNewFile();
        }
        DataInputStream dis = new DataInputStream(new BufferedInputStream(senderSocket.getInputStream()));
        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
        byte[] buf = new byte[2048];
        int len;
        while ((len = dis.read(buf, 0, 2048)) != -1) {
            dos.write(buf, 0, len);
            dos.flush();
        }
        dos.flush();
        dos.close();
        dis.close();

        client.getVoiceMessQueue().add(message);
    }

}
