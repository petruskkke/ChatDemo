package gui;

import client.Client;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 * MainWindows
 */
public class MainWindowsGUI {

    private JPanel mainGUI;

    private JList<String> userList;

    private JButton logoutButton;

    private JButton sendVoiceButton;

    private JButton playVoiceButton;

    private JTextPane messEditPane;

    private JButton sendButton;

    private JLabel friendLabel;

    private JLabel titleLabel;

    private JLabel usernameLabel;

    private JPanel chatPanel;

    private JButton filesButton;

    private JButton historyButton;

    private JTextPane chatPane;

    private JButton emojiButton;


    /**
     * Init this windows
     *
     * @param client
     * @param loginFrame
     * @param mainFrame
     */
    public MainWindowsGUI(Client client, JFrame loginFrame, JFrame mainFrame) {

        usernameLabel.setText(client.getUsername());
        userList.setListData(client.getOtherUsers());
        Map<String, JTextPane> usersTextArea = client.getUserMessTextArea();
        //获得userlist中的所有元素，循环遍历获得聊天框， 全部加入cradlayout中
        for (String name : usersTextArea.keySet()) {
            chatPanel.add(usersTextArea.get(name), name);
        }
        chatPane.setEditable(false);

        client.startReceiveThread(this);
        refreshUserList(client);

        // Login out
        logoutButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                //fixme relogin sending error.
                try {
                    client.exit();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                //fixme 这里清空 client的 userMessTextArea?
                mainFrame.dispose();
                loginFrame.setVisible(true);

            }
        });

        // Send message
        sendButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                //获取消息， 格式化消息， 发送消息
                List<ImageIcon> emojiList = new ArrayList<ImageIcon>();
                StyledDocument doc = messEditPane.getStyledDocument();

                for (int i = 0; i < doc.getRootElements()[0].getElementCount(); i++) {
                    Element root = doc.getRootElements()[0].getElement(i);
                    for (int j = 0; j < root.getElementCount(); j++) {
                        ImageIcon icon = (ImageIcon) StyleConstants.getIcon(root.getElement(j).getAttributes());
                        if (icon != null) {
                            emojiList.add(icon);
                        }
                    }
                }
                int k = 0;
                String msg = "";
                for (int i = 0; i < messEditPane.getText().length(); i++) {
                    if (messEditPane.getStyledDocument().getCharacterElement(i).getName().equals("icon")) {
                        msg += "#" + emojiList.get(k) + "#";
                        k++;
                    } else {
                        try {
                            msg += messEditPane.getStyledDocument().getText(i, 1);
                        } catch (BadLocationException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
                System.out.println(msg);

                String receiveUser = null;
                try {
                    receiveUser = userList.getSelectedValue().split(":")[0];
                } catch (Exception e1) {
                    System.out.println("Not choose the receiver.");
                }

                //选中指定聊天框,信息显示在自己的聊天框中
                JTextPane c = client.getUserMessTextArea().get(receiveUser);
                try {
                    client.messageSend(receiveUser, msg);   //todo 删了一个传入的c
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                //获得消息
                String showMess = null;
                try {
                    client.showMessInChatPane(msg, c);
                } catch (BadLocationException e1) {
                    e1.printStackTrace();
                }

//                //在自己界面显示消息
//                SimpleAttributeSet attrset = new SimpleAttributeSet();
//                StyleConstants.setFontSize(attrset, 12);
//                Document d = c.getDocument();
//                Document docs = chatPane.getDocument();
//                try {
//                    d.insertString(d.getLength(), showMess, attrset);
//                    docs.insertString(docs.getLength(), showMess, attrset);
//                } catch (BadLocationException e1) {
//                    e1.printStackTrace();
//                }

                messEditPane.setText("");
            }
        });

        // send file
        filesButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(fileChooser.FILES_AND_DIRECTORIES);
                fileChooser.showDialog(new JLabel(), "choose");
                File file = fileChooser.getSelectedFile();
                //显示选中的文件（夹）信息
                if (file.isDirectory()) {
                    System.out.println("Directory:" + file.getAbsolutePath());
                } else if (file.isFile()) {
                    System.out.println("File:" + file.getAbsolutePath());
                    //进行文件的传输
                    String receiver = userList.getSelectedValue();
                    String filename = file.getAbsolutePath();
                    try {
                        client.fileSendReq(receiver, filename);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                //System.out.println(fileChooser.getSelectedFile().getName());
            }
        });

        //change user list
        userList.addListSelectionListener(new ListSelectionListener() {
            /**
             * Called whenever the value of the selection changes.
             *
             * @param e the event that characterizes the change.
             */
            @Override
            public void valueChanged(ListSelectionEvent e) {
                String selectName = userList.getSelectedValue().split(":")[0];
                CardLayout cl = (CardLayout) chatPanel.getLayout();
                cl.show(chatPanel, selectName);
            }
        });

        // send voice message
        sendVoiceButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                String receiver = userList.getSelectedValue().split(":")[0];
                VoiceSendGUI.showGUI(client, sendVoiceButton, receiver);
            }
        });

        // play voice message
        playVoiceButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String chooseUser = userList.getSelectedValue();
                    client.playVoice(chooseUser);
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (UnsupportedAudioFileException e1) {
                    e1.printStackTrace();
                } catch (LineUnavailableException e1) {
                    e1.printStackTrace();
                }
            }
        });

        // send emoji
        emojiButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                EmojiWindows emojiWindows = new EmojiWindows(messEditPane, emojiButton);
                emojiWindows.setVisible(true);

                //else {
                //emojiWindows.dispose();  //fixme be better
                //}
            }
        });

    }

//    public static void main(String[] args) {
//        JFrame frame = new JFrame("MainWindowsGUI");
//        frame.setContentPane(new MainWindowsGUI().mainGUI);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.pack();
//        frame.setSize(640, 480);
//        frame.setResizable(false);
//        frame.setVisible(true);
//    }

    /**
     * Show this windows
     *
     * @param client
     * @param loginFrame
     * @throws IOException
     */
    public static void showGUI(Client client, JFrame loginFrame) throws IOException {
        JFrame frame = new JFrame("MainWindowsGUI");
        frame.setContentPane(new MainWindowsGUI(client, loginFrame, frame).mainGUI);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(640, 480);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    /**
     * Refresh the user list and user state
     *
     * @param client
     */
    private void refreshUserList(Client client) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        client.reqUserList();
                        Thread.currentThread().sleep(1000);
                        //自动更新好友状态
                        userList.revalidate();
                        userList.repaint();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

//    private void upChatTextArea() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while(true){
//                    chatTextArea.paintImmediately(chatTextArea.getBounds());
//                    System.out.println("hahaha===========");
//                    try {
//                        Thread.currentThread().sleep(100);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();
//    }

    public JPanel getMainGUI() {
        return mainGUI;
    }

    public JList<String> getUserList() {
        return userList;
    }

    public JButton getLogoutButton() {
        return logoutButton;
    }

    public JButton getSendVoiceButton() {
        return sendVoiceButton;
    }

    public JButton getPlayVoiceButton() {
        return playVoiceButton;
    }

    public JTextPane getMessEditPane() {
        return messEditPane;
    }

    public JButton getSendButton() {
        return sendButton;
    }

    public JTextPane getChatTextPane() {
        return chatPane;
    }

    public JLabel getFriendLabel() {
        return friendLabel;
    }

    public JLabel getTitleLabel() {
        return titleLabel;
    }

    public JLabel getUsernameLabel() {
        return usernameLabel;
    }

    public JPanel getChatPanel() {
        return chatPanel;
    }
}
