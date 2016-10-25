package gui;

import client.Client;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class LoginGUI {

    private JPanel Login;

    private JTextField userTextField;

    private JTextField passTextField;

    private JButton loginButton;

    private JButton registerButton;

    private JLabel passLabel;

    private JLabel userLabel;

    private JLabel authorLabel;

    private JLabel titleLabel;

    private JLabel imageLabel;

    private Client client = new Client();

    /**
     * Init this window
     * @param loginFrame
     */
    public LoginGUI(JFrame loginFrame) {
        loginButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userTextField.getText();
                String password = passTextField.getText();
                String loginResp = null;
                try {
                    loginResp = client.connection(username, password);
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(null, "Server not start.");
                }

                try {
                    if (loginResp.split("\\|")[0].equals("1")) {
                        client.setIsRunning(true);
                        System.out.println(loginResp);
                        client.initUserList();
                        MainWindowsGUI.showGUI(client, loginFrame);
                        loginFrame.setVisible(false);
                    } else {
                        JOptionPane.showMessageDialog(null, "Username or password is wrong!");
                        System.out.println(loginResp);
                        client.getMessSendSocket().close();
                        client.getMessReceiveSocket().close();
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //???????????????
                RegisterGUI.showGUI(loginFrame);
                loginFrame.setVisible(false);
            }
        });
    }


    /**
     * Show this window when start this client
     * @param args
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame("LoginGUI");
        frame.setContentPane(new LoginGUI(frame).Login);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(260, 400);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}
