package client;

import java.io.*;
import java.net.Socket;

/**
 * File send thread
 */
public class FileSendThread extends Thread {
    private String message;

    private Socket sendSocket;

    /**
     * Init this thread.
     * @param message
     */
    public FileSendThread(String message) {
        this.message = message;
    }

    /**
     * Start this thread.
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
     * Main run method for this thread.
     * @throws IOException
     */
    public void threadRun() throws IOException {
        String[] s = message.split("\\|@");
        String host = s[3];
        int post = Integer.parseInt(s[4]);
        String filename = s[5];
        try {
            sendSocket = new Socket(host, post);
        } catch (IOException e) {
            e.printStackTrace();
        }
        File file = new File(filename);
        DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(sendSocket.getOutputStream()));
        byte[] buf = new byte[2048];
        int len;
        while ((len = dis.read(buf, 0, 2048)) != -1) {
            dos.write(buf, 0, len);
            dos.flush();
        }
        dos.flush();
        dos.close();
        dis.close();
        sendSocket.close();
    }

}
