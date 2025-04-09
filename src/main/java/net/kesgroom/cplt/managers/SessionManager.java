package net.kesgroom.cplt.managers;

import java.sql.Timestamp;

public class SessionManager {
    private Timestamp login_time;
    private boolean connect;
    private String uuid;

    public SessionManager() {

    }

    public Timestamp getLogin_time() {
        return login_time;
    }

    public boolean getConnect() {
        return connect;
    }

    public void setConnect(boolean connect) {
        this.connect = connect;
    }

    public void setLogin_time(Timestamp login_time) {
        this.login_time = login_time;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
