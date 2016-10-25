package gui;

import client.Client;
import client.FileReceiveThread;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Window for file received
 */
public class FileReceiveGUI {

    private JButton yesButton;

    private JButton noButton;

    private JLabel askLabel;

    private JPanel fileReceivePanel;

    /**
     * Init the windows
     * @param frame
     * @param client
     * @param mainWindowsGUI
     * @param message
     */
    public FileReceiveGUI(JFrame frame, Client client, MainWindowsGUI mainWindowsGUI, String message) {
        //fixme 设置在屏幕上的位置
        yesButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                //开启新端口 通知发送方并发送数据，接收数据保存
                String reciver = mainWindowsGUI.getUserList().getSelectedValue().split(":")[0];
                String filename = message.split("\\|@")[3];

                FileReceiveThread frThread = new FileReceiveThread(client, message);
                frThread.start();

                frame.dispose();

            }
        });

        noButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e
             */
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    /**
     * Show this window in main window
     * @param client
     * @param mainWindowsGUI
     * @param mess
     */
    public static void showGUI(Client client, MainWindowsGUI mainWindowsGUI, String mess) {
        JFrame frame = new JFrame("FileReceiveGUI");
        frame.setContentPane(new FileReceiveGUI(frame, client, mainWindowsGUI, mess).fileReceivePanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
