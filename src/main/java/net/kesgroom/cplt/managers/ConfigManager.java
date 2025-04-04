package net.kesgroom.cplt.managers;

public class ConfigManager {
    private static ConfigManager instance;
    private long max_time;
    private boolean active;
    private String hour_reset;

    private ConfigManager() {
        max_time = 10800000;
        active = false;
        hour_reset = "00:00";
    }

    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    public long getMaxTime() {
        return max_time;
    }

    public void setMaxTime(long value) {
        this.max_time = value;
    }

    public String getHourReset() {
        return hour_reset;
    }

    public void setHourReset(String value) {
        this.hour_reset = value;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean value) {
        this.active = value;
    }

}
