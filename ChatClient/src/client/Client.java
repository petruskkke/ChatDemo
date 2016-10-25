package client;

import gui.MainWindowsGUI;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.text.*;
import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is a controller for client application.
 */
public class Client {
    private String host;

    private int port1;

    private int port2;

    private String username;

    /**
     * other username and online state list
     */
    private Vector otherUsers;

    private Socket messSendSocket;

    private Socket messReceiveSocket;

    private MessReceiveThread messReceiveThread;

    private CommandFormat commandFormat;

    private String resourcesPath;

    private boolean isRunning;

    private Map<String, JTextPane> userMessTextPane;

    private List<String> voiceMessList;

    /**
     * Init Client
     */
    public Client() {
        this.host = "127.0.0.1";  //fixme change every time
        this.port1 = 8001;
        this.port2 = 8002;
        this.commandFormat = new CommandFormat();
        this.resourcesPath = System.getProperty("user.dir") + "\\src\\resources";
        this.isRunning = false;
        this.otherUsers = new Vector<String>();
        this.userMessTextPane = new HashMap<>();
        this.voiceMessList = new ArrayList<>();
    }

    /**
     * Link request from client
     *
     * @param username
     * @param password
     * @return
     * @throws IOException
     */
    public String connection(String username, String password) throws IOException {
        this.messReceiveSocket = new Socket(this.host, this.port1);            //8001接收
        this.messSendSocket = new Socket(this.host, this.port2); //8002发送
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(messSendSocket.getOutputStream()));
        BufferedReader br = new BufferedReader(new InputStreamReader(messReceiveSocket.getInputStream()));
        this.username = username;
        pw.write(commandFormat.loginReq(username, password));
        pw.flush();
        String resp = br.readLine();
        return resp;
    }

    /**
     * User request register to server
     *
     * @param username
     * @param password
     * @return
     * @throws IOException
     */
    public String userRegisterReq(String username, String password) throws IOException {
        this.messReceiveSocket = new Socket(this.host, this.port1);
        this.messSendSocket = new Socket(this.host, this.port2);
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(messSendSocket.getOutputStream()));
        BufferedReader br = new BufferedReader(new InputStreamReader(messReceiveSocket.getInputStream()));
        pw.write(commandFormat.registerReq(username, password));
        pw.flush();
        String resp = br.readLine();
        return resp;
    }

    /**
     *
     */
    public void disConnect() {
        try {
            this.messReceiveSocket.close();
            this.messSendSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Init user list
     *
     * @throws IOException
     */
    public void initUserList() throws IOException {
        otherUsers.removeAllElements();
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(messSendSocket.getOutputStream()));
        BufferedReader br = new BufferedReader(new InputStreamReader(messReceiveSocket.getInputStream()));
        pw.write(commandFormat.listReq());
        pw.flush();
        String resp = br.readLine();
        System.out.println(resp);
        Pattern p = Pattern.compile("\\[.*\\]");
        Matcher m = p.matcher(resp);
        String userlist = "";  //fixme ?
        if (m.find()) {
            int s = m.start() + 1;
            int e = m.end() - 1;
            userlist = resp.substring(s, e);
        }
        String[] u = userlist.split(",");
        for (String s : u) {
            if (s.split(":")[0].equals(username)) {
                continue;
            }
            otherUsers.add(s);
            createUserMessTextPane(s);
        }
    }

    /**
     * Update user list
     *
     * @param resp
     */
    public void updateUserList(String resp) {
        Pattern p = Pattern.compile("\\[.*\\]");
        Matcher m = p.matcher(resp);
        String userlist = "";  //fixme ?
        if (m.find()) {
            int s = m.start() + 1;
            int e = m.end() - 1;
            userlist = resp.substring(s, e);
        }
        String[] u = userlist.split(",");
        for (String s : u) {
            if (s.split(":")[0].equals(username)) {
                continue;
            }
            for (Object i : otherUsers) {
                if (s.equals(i.toString())) {
                    break;
                } else if (s.split(":")[0].equals(i.toString().split(":")[0])) {
                    int index = otherUsers.indexOf(i);
                    otherUsers.set(index, s);
                }
            }
            createUserMessTextPane(s);
        }
    }

    /**
     * Request update user list
     *
     * @throws IOException
     */
    public void reqUserList() throws IOException {
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(messSendSocket.getOutputStream()));
        pw.write(commandFormat.listReq());
        pw.flush();
    }

    /**
     * Create TextArea for user and add into Map
     *
     * @param s
     */
    private void createUserMessTextPane(String s) {
        JTextPane j = new JTextPane();
        String username = s.split(":")[0];
        //判断如果字典中没有这个键
        if (!userMessTextPane.containsKey(username)) {
            userMessTextPane.put(username, j);
        }
    }

    /**
     * Send text message to server
     *
     * @param receiver
     * @param sendMess
     * @throws IOException
     */
    public void messageSend(String receiver, String sendMess) throws IOException {
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(messSendSocket.getOutputStream()));
        BufferedReader br = new BufferedReader(new InputStreamReader(messReceiveSocket.getInputStream()));
        String time = curTimeGet();
        String req = commandFormat.messSendReq(username, receiver, sendMess, time);
        //返回一个处理好的可以显示在区域的的信息
        pw.write(req);//将消息发送出去
        pw.flush();
    }

    /**
     * Send voice message to server
     *
     * @param receiver
     * @param filename
     * @throws IOException
     */
    public void voiceMessSend(String receiver, String filename) throws IOException {
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(messSendSocket.getOutputStream()));
        String time = curTimeGet();
        String req = commandFormat.voiceSendReq(username, receiver, filename, time);
        pw.write(req);
        pw.flush();
    }

    /**
     * send port message to server
     *
     * @param receiver
     * @param host
     * @param port
     * @param filename
     * @throws IOException
     */
    public void portMessSend(String receiver, String host, int port, String filename) throws IOException {
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(messSendSocket.getOutputStream()));
        String time = curTimeGet();
        String req = commandFormat.voicePortReq(username, receiver, host, port, filename, time);
        pw.write(req);
        pw.flush();
    }

    /**
     * Send file port message
     *
     * @param receiver
     * @param host
     * @param port
     * @param filename
     * @throws IOException
     */
    public void filePortMessSend(String receiver, String host, int port, String filename) throws IOException {
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(messSendSocket.getOutputStream()));
        String time = curTimeGet();
        String req = commandFormat.filePortReq(username, receiver, host, port, filename, time);
        System.out.println(req);  //fixme test
        pw.write(req);
        pw.flush();
    }

    /**
     * User exit
     *
     * @throws IOException
     */
    public void exit() throws IOException {
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(messSendSocket.getOutputStream()));
        String time = curTimeGet();
        String req = commandFormat.exitReq(username, time);
        System.out.println(req);  //fixme test
        pw.write(req);
        pw.flush();
    }

    /**
     * Play voice message
     *
     * @param chooseUser
     * @throws IOException
     * @throws UnsupportedAudioFileException
     * @throws LineUnavailableException
     */
    public void playVoice(String chooseUser) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        String filename;
        if (!voiceMessList.isEmpty()) {

            for (String m : voiceMessList) {

                String sender = m.split("\\|@")[1];
                chooseUser = chooseUser.split(":")[0];

                if (chooseUser.equals(sender)) {
                    filename = m.split("\\|@")[3];
                    File voice = new File(System.getProperty("user.dir") + "/src/client/receivevoice/" + filename);
                    if (voice.exists()) {
                        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(voice);
                        AudioFormat format = audioInputStream.getFormat();
                        SourceDataLine auline = null;
                        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
                        auline = (SourceDataLine) AudioSystem.getLine(info);
                        auline.open(format);
                        auline.start();
                        int nBytesRead = 0;
                        byte[] abData = new byte[1024];
                        try {
                            while (nBytesRead != -1) {
                                nBytesRead = audioInputStream.read(abData, 0, abData.length);
                                if (nBytesRead >= 0) {
                                    auline.write(abData, 0, nBytesRead);
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            auline.drain();
                            auline.close();
                        }
                        voiceMessList.remove(m);
                        break;
                    }
                }
            }
        }
    }

    /**
     * Request for a file
     *
     * @param receiver
     * @param filename
     * @throws IOException
     */
    public void fileSendReq(String receiver, String filename) throws IOException {
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(messSendSocket.getOutputStream()));
        String time = curTimeGet();
        String req = commandFormat.fileSendReq(username, receiver, filename, time);
        pw.write(req);
        pw.flush();
    }

    /**
     * @param sendMess
     * @param chatPane
     * @throws BadLocationException
     */
    public void showMessInChatPane(String sendMess, JTextPane chatPane) throws BadLocationException {
        String time = curTimeGet();
        String[] s = sendMess.split("#");
        SimpleAttributeSet attrset = new SimpleAttributeSet();
        StyleConstants.setFontSize(attrset, 12);
        SimpleAttributeSet attrsetTitle = new SimpleAttributeSet();
        StyleConstants.setFontSize(attrsetTitle, 14);
        StyledDocument docs = chatPane.getStyledDocument();
        docs.insertString(docs.getLength(), username + "    " + time + "\n", attrsetTitle);
        for (String s1 : s) {
            if (s1.endsWith(".gif")) {
                String iconName = s1.split("/")[s1.split("/").length - 1];
                JLabel cub1 = new JLabel(new ImageIcon(System.getProperty("user.dir") + "/src/resources/emoji/" + iconName));
                System.out.println(cub1);  //fixme test
                ImageIcon icon = (ImageIcon) cub1.getIcon();
                Style style = docs.addStyle("StyleName", null);
                StyleConstants.setIcon(style, icon);
                docs.insertString(docs.getLength(), "ignored text", style);
            } else {
                docs.insertString(docs.getLength(), s1, attrset);
            }
        }
        docs.insertString(docs.getLength(), "\n", attrset);
    }

    /**
     * Get current time
     *
     * @return
     */
    public String curTimeGet() {
        SimpleDateFormat dateFormater = new SimpleDateFormat("MMM dd yyyy HH:mm:ss", new Locale("en"));
        return dateFormater.format(new Date());
    }

    /**
     * handle the message recived
     *
     * @param mess
     * @param chatPane
     * @throws BadLocationException
     */
    public void receiveMessHandle(String mess, JTextPane chatPane) throws BadLocationException {
        String[] m = mess.split("\\|@");
        String time = m[4];
        String msg = m[3];
        String sender = m[1];
        String[] s = msg.split("#");

        SimpleAttributeSet attrset = new SimpleAttributeSet();
        StyleConstants.setFontSize(attrset, 12);
        SimpleAttributeSet attrsetTitle = new SimpleAttributeSet();
        StyleConstants.setFontSize(attrsetTitle, 14);
        StyledDocument docs = chatPane.getStyledDocument();
        if (m[0].equals("106") || m[0].equals("107")) {
            docs.insertString(docs.getLength(), sender + "    " + m[6] + "\n", attrsetTitle);
        } else {
            docs.insertString(docs.getLength(), sender + "    " + time + "\n", attrsetTitle);
        }

        for (String s1 : s) {
            if (s1.endsWith(".gif")) {
                String iconName = s1.split("/")[s1.split("/").length - 1];
                JLabel cub1 = new JLabel(new ImageIcon(System.getProperty("user.dir") + "/src/resources/emoji/" + iconName));
                System.out.println(cub1);   //fixme test
                ImageIcon icon = (ImageIcon) cub1.getIcon();

                Style style = docs.addStyle("StyleName", null);
                StyleConstants.setIcon(style, icon);

                docs.insertString(docs.getLength(), "ignored text", style);
            } else {
                docs.insertString(docs.getLength(), s1, attrset);
            }
        }
        docs.insertString(docs.getLength(), "\n", attrset);
    }


    /**
     * start the receive thread.
     *
     * @param mainWindowsGUI
     */
    public void startReceiveThread(MainWindowsGUI mainWindowsGUI) {
        messReceiveThread = new MessReceiveThread(mainWindowsGUI, this);
        messReceiveThread.start();
    }

//    public JTextPane changeUserMessTextPane(String selectName) {
//        System.out.println("text areachange :" + userMessTextPane.get(selectName));
//        return userMessTextPane.get(selectName);
//    }

    public Socket getMessSendSocket() {
        return messSendSocket;
    }

    public Socket getMessReceiveSocket() {
        return messReceiveSocket;
    }

    public CommandFormat getCommandFormat() {
        return commandFormat;
    }

    public String getUsername() {
        return username;
    }

    public Vector<String> getOtherUsers() {
        return otherUsers;
    }

    public boolean getIsRunning() {
        return isRunning;
    }

    public Map<String, JTextPane> getUserMessTextArea() {
        return userMessTextPane;
    }

    public List<String> getVoiceMessQueue() {
        return voiceMessList;
    }

    public void setIsRunning(boolean b) {
        isRunning = b;
    }

}
