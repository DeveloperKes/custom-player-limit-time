package net.kesgroom.cplt;

import net.minecraft.text.Text;

public class CustomPlayerInfo {
    long sessionStartAgo;
    long remainingTime;
    boolean warningAlert;
    boolean banPlayer;
    String playerName;

    public CustomPlayerInfo(long remainingTime, Text playerName) {
        this.sessionStartAgo = System.currentTimeMillis();
        this.remainingTime = remainingTime;
        this.warningAlert = true;
        this.banPlayer = false;
        this.playerName = playerName.getString();
    }
}
