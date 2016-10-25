package server;


/**
 * Format response
 */
public class ResponseFormat {

    /**
     * Format login response
     *
     * @param loginTag
     * @return
     */
    public String loginResp(boolean loginTag) {
        String resp;
        if (loginTag) {
            resp = "1|@Login successful.\n";
        } else {
            resp = "0|@Login failed.\n";
        }
        return resp;
    }

    /**
     * Format get user list response
     *
     * @param username
     * @param userList
     * @param time
     * @return
     */
    public String listResp(String username, String userList, String time) {
        String resp;
        resp = "1|@102|@" + username + "|@[" + userList + "]|@" + time + "\n";
        return resp;
    }

    /**
     * Format send message response
     *
     * @param tag
     * @return
     */
    public String messSendResp(boolean tag) {
        String resp;
        if (tag) {
            resp = "1|@103|@Message Send Successful.\n";
        } else {
            resp = "0|@103|@Message Send Failed.\n";
        }
        return resp;
    }

    /**
     * Format voice response
     *
     * @param tag
     * @return
     */
    public String VoiceResp(boolean tag) {
        String resp;
        if (tag) {
            resp = "1|@105|@Voice message send successful.\n";
        } else {
            resp = "0|@105|@Voice message send failed.\n";
        }
        return resp;
    }

    /**
     * Format port message response
     *
     * @param tag
     * @return
     */
    public String portMessResp(boolean tag) {
        String resp;
        if (tag) {
            resp = "1|@106|@Port message send successful.\n";
        } else {
            resp = "0|@106|@Port message send failed.\n";
        }
        return resp;
    }

    /**
     * Format file send response
     *
     * @param tag
     * @return
     */
    public String FileSendResp(boolean tag) {
        String resp;
        if (tag) {
            resp = "1|@104|@File transfer request send successful.\n";
        } else {
            resp = "0|@104|@File transfer request send failed.\n";
        }
        return resp;
    }

    /**
     * Format quit response
     *
     * @return
     */
    public String exitResp() {
        return "1|Good Bye.\n";
    }

    /**
     * Format register response
     *
     * @param tag
     * @return
     */
    public String registerMessResp(boolean tag) {
        String resp;
        if (tag) {
            resp = "1|@108|@User register successful.\n";
        } else {
            resp = "0|@108|@User register fails.\n";
        }
        return resp;
    }
}
