package user;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * This class provide the operation for user's data.
 *
 * @author Zhanghan Ke
 */
public class UserData {
    public Map<String, String> allUsers;
    public Map<String, String> onlineUsers;

    public UserData() {
        allUsers = new HashMap<>();
        onlineUsers = new HashMap<>();

        try {
            loadUsers();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public boolean addUser(String username, String password) throws IOException {
        //�ж��û��Ƿ����
        for (String i : allUsers.keySet()) {
            if (username.equals(i)) {
                //��ʾ�û����ڣ�������Ϊ0
                return false;
            }
        }
        // ѭ������û��Ȼ��˵���û�������
        allUsers.put(username, password);
        //���ļ�д�����û�
        File userfile = new File(System.getProperty("user.dir") + "/src/user/users.txt");
        System.out.println(System.getProperty("user.dir") + "/src/user/users.txt");
        if (userfile.isFile() && userfile.exists()) {
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(userfile, true), "GBK"));
            pw.println(username + " " + password + "\n");  //fixme  ׷������    ����ȡ�û�������Ϊnullʱ����
            pw.flush();
            pw.close();
        }
        return true;
    }

    public void loadUsers() throws IOException {
        File userfile = new File(System.getProperty("user.dir") + "/src/user/users.txt");
        if (userfile.exists()) {
            if (userfile.isFile()) {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(userfile), "GBK"));
                String u;
                while ((u = br.readLine()) != null) {
                    if(u.split(" ").length == 2){
                        String username = u.split(" ")[0];
                        String password = u.split(" ")[1];
                        allUsers.put(username, password);
                    }
                }
                br.close();
            }
        }
    }

    public Map<String, String> getAllUsers() {
        return allUsers;
    }

    public Map<String, String> getOnlineUsers() {
        return onlineUsers;
    }
}
