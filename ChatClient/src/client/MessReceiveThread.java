package client;

import gui.FileReceiveGUI;
import gui.MainWindowsGUI;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Received message from server and update to views.
 */
public class MessReceiveThread extends Thread {

    private MainWindowsGUI mainWindowsGUI;

    private Client client;

    private Socket messReceiveSocket;

    /**
     * Init this Thread.
     * @param mainWindowsGUI
     * @param client
     */
    public MessReceiveThread(MainWindowsGUI mainWindowsGUI, Client client) {
        this.mainWindowsGUI = mainWindowsGUI;
        this.client = client;
        this.messReceiveSocket = client.getMessReceiveSocket();
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
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Main run method for this thread
     * @throws IOException
     * @throws BadLocationException
     */
    public void threadRun() throws IOException, BadLocationException {
        BufferedReader br = new BufferedReader(new InputStreamReader(messReceiveSocket.getInputStream()));
        JTextPane c;

        while (client.getIsRunning()) {
            String mess = messReceive(br);
            if (mess != null && mess != "") {
                //选择接收消息的面板
                String sender = mess.split("\\|@")[1];
                c = client.getUserMessTextArea().get(sender);

                client.receiveMessHandle(mess, c);
                System.out.println("Receive message:" + mess);
            }
        }
    }

    /**
     * Judge whether a message received or not
     * @param br
     * @return
     * @throws IOException
     */
    private String messReceive(BufferedReader br) throws IOException {
        String resp = "";

        String mess = br.readLine();
        String[] m = mess.split("\\|@");
        switch (m[0]) {
            case "1":
                if (m[1].equals("102")) {
                    client.updateUserList(mess);
                } else {
                    System.out.println(mess);
                }
                resp = null;
                break;
            case "0":
                System.out.println(mess);
                resp = null;
                break;
            case "103":
                while (true) {
                    resp += mess + "\n";
                    if (mess.endsWith("|@@MESSAGEEND")) {
                        break;
                    }
                    mess = br.readLine();
                }
                break;
            case "104":
                resp = mess;
                //询问是否接受文件
                FileReceiveGUI.showGUI(client, mainWindowsGUI, mess);
                break;
            case "105":
                resp = mess;
                VoiceReceiveThread vrthread = new VoiceReceiveThread(client, mess);
                vrthread.start();
                //开启新端口 通知发送方并发送数据，接收数据保存
                //接收到的语音mess消息放入一个队列，每点击一次播放按钮就播放一条（注意要有顺序，先进先出）
                break;
            case "106":
                resp = mess;
                VoiceSendThread vsthread = new VoiceSendThread(mess);
                vsthread.start();
                break;
            case "107":
                resp = mess;
                FileSendThread fsthread = new FileSendThread(mess);
                fsthread.start();
                break;
        }
        return resp;
    }
}
