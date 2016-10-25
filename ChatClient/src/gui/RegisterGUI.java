package gui;

import client.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * This is a register window
 */
public class RegisterGUI {

    private JPanel registerPanel;

    private JTextField usernameTextField;

    private JTextField passwordTextField;

    private JTextField passwordTextField2;

    private JButton registerButton;

    private JButton backButton;

    private Client client;

    /**
     * Init this
     *
     * @param loginFrame
     * @param registerFrame
     */
    public RegisterGUI(JFrame loginFrame, JFrame registerFrame) {
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client = new Client();
                //��ȡ�������Ϣ�������͸�������
                String username = usernameTextField.getText();
                String password = passwordTextField.getText();
                String password2 = passwordTextField2.getText();
                String resp = null;
                if (!password.equals(password2)) {
                    JOptionPane.showMessageDialog(null, "Password not same.");
                } else {
                    try {
                        resp = client.userRegisterReq(username, password);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    //fixme ��÷����Ƿ�ע��ɹ�
                    System.out.println("ע�᷵��:" + resp); //fixme test
                    if (resp.startsWith("1|@")) {
                        JOptionPane.showMessageDialog(null, "Register successful.");
                    } else if (resp.startsWith("0|@")) {
                        JOptionPane.showMessageDialog(null, "Username has been used");
                    }
                    //fixme �Ͽ�����
                    client.disConnect();
                }
            }
        });
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerFrame.dispose();
                loginFrame.setVisible(true);
            }
        });
    }

//    public static void main(String[] args) {
//        JFrame frame = new JFrame("RegisterGUI");
//        frame.setContentPane(new RegisterGUI().registerPanel);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.pack();
//        frame.setSize(260, 400);
//        frame.setVisible(true);
//    }

    /**
     * Show this panel in window
     *
     * @param loginFrame
     */
    public static void showGUI(JFrame loginFrame) {
        JFrame frame = new JFrame("RegisterGUI");
        frame.setContentPane(new RegisterGUI(loginFrame, frame).registerPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(260, 400);
        frame.setVisible(true);
    }
}
