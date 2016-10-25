package client;


import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

/**
 * file transfer thread
 */
public class FileReceiveThread extends Thread {
    Client client;

    ServerSocket fileSendSocket;

    Socket senderSocket;

    String filename;

    String message;

    String receiver;

    /**
     *
     * @param client
     * @param message
     */
    public FileReceiveThread(Client client, String message) {
        this.client = client;
        this.message = message;
        this.receiver = message.split("\\|@")[1];
        this.filename = message.split("\\|@")[3];
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
                fileSendSocket = new ServerSocket(port);
                host = host.split("/")[1];
                client.filePortMessSend(receiver, host, port, filename);
                break;
            } catch (IOException e) {
                continue;
            }
        }

        senderSocket = fileSendSocket.accept();
        filename = filename.split("\\\\")[filename.split("\\\\").length - 1];
        System.out.println(filename);
        File file = new File(System.getProperty("user.dir") + "/src/client/receivefiles/" + filename);
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
    }
}
