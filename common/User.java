package common;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String status;

    public User(String username) {
        this.username = username;
        this.status   = "Online";
    }

    public String getUsername() { return username; }
    public String getStatus()   { return status; }
    public void setStatus(String s) { this.status = s; }

    @Override
    public String toString() { return username; }
}
