package net.kesgroom.cplt.managers;

public class PlayerManager {
    private String uuid;
    private String name;
    private boolean banned;

    public PlayerManager() {

    }

    public String getName() {
        return name;
    }

    public String getUuid() {
        return uuid;
    }

    public boolean getBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
