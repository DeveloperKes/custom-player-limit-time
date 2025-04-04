package net.kesgroom.cplt.managers;

import java.sql.Timestamp;

public class SessionManager {
    private Timestamp login_time;
    private boolean ended;

    public SessionManager() {

    }

    public Timestamp getLogin_time() {
        return login_time;
    }

    public boolean getEnded() {
        return ended;
    }

    public void setEnded(boolean ended) {
        this.ended = ended;
    }

    public void setLogin_time(Timestamp login_time) {
        this.login_time = login_time;
    }
}
