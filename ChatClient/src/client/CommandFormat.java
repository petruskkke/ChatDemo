package client;

/**
 * Command format.
 */
public class CommandFormat {

    /**
     *
     * @param username
     * @param password
     * @return
     */
    public String loginReq(String username, String password) {
        return "101|@" + username + "|@" + password + "\n";
    }

    /**
     *
     * @return
     */
    public String listReq() {
        return "102|@\n";
    }

    /**
     *
     * @param username
     * @param receiver
     * @param message
     * @param time
     * @return
     */
    public String messSendReq(String username, String receiver, String message, String time) {
        return "103|@" + username + "|@" + receiver + "|@" + message + "|@" + time + "|@@MESSAGEEND\n";
    }

    /**
     *
     * @param username
     * @param receiver
     * @param filename
     * @param time
     * @return
     */
    public String fileSendReq(String username, String receiver, String filename, String time) {
        return "104|@" + username + "|@" + receiver + "|@" + filename + "|@" + time + "\n";
    }

    /**
     *
     * @param username
     * @param receiver
     * @param filename
     * @param time
     * @return
     */
    public String voiceSendReq(String username, String receiver, String filename, String time) {
        return "105|@" + username + "|@" + receiver + "|@" + filename + "|@" + time + "\n";
    }

    /**
     *
     * @param username
     * @param receiver
     * @param host
     * @param port
     * @param filename
     * @param time
     * @return
     */
    public String voicePortReq(String username, String receiver, String host, int port, String filename, String time) {
        return "106|@" + username + "|@" + receiver + "|@" + host + "|@" + port + "|@" + filename + "|@" + time + "\n";
    }

    /**
     *
     * @param username
     * @param receiver
     * @param host
     * @param port
     * @param filename
     * @param time
     * @return
     */
    public String filePortReq(String username, String receiver, String host, int port, String filename, String time) {
        return "107|@" + username + "|@" + receiver + "|@" + host + "|@" + port + "|@" + filename + "|@" + time + "\n";
    }

    /**
     *
     * @param username
     * @param time
     * @return
     */
    public String exitReq(String username, String time) {
        return "100|@" + username + "|@" + time + "\n";
    }

    /**
     *
     * @param username
     * @param password
     * @return
     */
    public String registerReq(String username, String password) {
        return "108|@" + username + "|@" + password + "\n";
    }
}
