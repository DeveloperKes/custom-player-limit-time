package net.kesgroom.cplt.managers;

import java.sql.Date;

public class PlayerTimeManager {
    private long remaining_time;
    private String uuid;
    private Date last_accumulation_date;

    public PlayerTimeManager() {

    }

    public long getRemaining_time() {
        return remaining_time;
    }

    public void setRemaining_time(long remaining_time) {
        this.remaining_time = remaining_time;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Date getLast_accumulation_date() {
        return last_accumulation_date;
    }

    public void setLast_accumulation_date(Date last_accumulation_date) {
        this.last_accumulation_date = last_accumulation_date;
    }

}
