package net.kesgroom.cplt;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import java.lang.reflect.Type;

public class CustomPlayTimeManager {

    private static final String FILE_PATH = "config/playtimes.json";
    private static final Gson gson = new Gson();
    private Map<UUID, CustomPlayerInfo> playTimeMap;

    public CustomPlayTimeManager() {
        playTimeMap = new HashMap<>();
        loadPlayTimes();
    }

    public void loadPlayTimes() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                Type type = new TypeToken<Map<UUID, CustomPlayerInfo>>() {
                }.getType();
                playTimeMap = gson.fromJson(reader, type);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            savePlayTimes();
        }
    }

    public void savePlayTimes() {
        File file = new File(FILE_PATH);
        createParentDirectories(file);
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(playTimeMap, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveNewPlayer(UUID uuid, CustomPlayerInfo playerInfo) {
        playTimeMap.put(uuid, playerInfo);
        savePlayTimes();
    }

    private void createParentDirectories(File file) {
        File parentDirectory = file.getParentFile();
        if (parentDirectory != null && !parentDirectory.exists()) {
            parentDirectory.mkdirs();
        }
    }

    public long getPlayTime(UUID playerId) {
        CustomPlayerInfo player = playTimeMap.get(playerId);
        if (player != null) {
            return player.remainingTime;
        }
        return 0;
    }

    public CustomPlayerInfo getPlayer(UUID uuid) {
        return playTimeMap.get(uuid);
    }

    public void updatePlayTime(UUID playerId, long remaining) {
        CustomPlayerInfo player = playTimeMap.get(playerId);
        if (player != null) {
            player.remainingTime = remaining;
            playTimeMap.put(playerId, player);
            savePlayTimes();
        }
    }

    public boolean getBanUserByUUID(UUID uuid) {
        CustomPlayerInfo player = playTimeMap.get(uuid);
        return player != null && player.banPlayer;
    }

    public void banUser(UUID uuid) {
        CustomPlayerInfo player = playTimeMap.get(uuid);
        if (player != null) {
            player.banPlayer = true;
            playTimeMap.put(uuid, player);
            savePlayTimes();
        }
    }

    public void resetPlayTimes() {
        playTimeMap.clear();
        savePlayTimes();
    }

    public boolean resetPlayerByPlayerName(String playerName) {
        for (Map.Entry<UUID, CustomPlayerInfo> entry : playTimeMap.entrySet()) {
            if (Objects.equals(entry.getValue().playerName, playerName)) {
                CustomPlayerInfo playerInfo = entry.getValue();
                playerInfo.remainingTime = CustomConfigManager.getTimeLimit();
                playerInfo.banPlayer = false;
                playTimeMap.put(entry.getKey(), playerInfo);
                savePlayTimes();
                return true;
            }
        }

        return false;
    }

    public void startSession(UUID uuid) {
        CustomPlayerInfo player = playTimeMap.get(uuid);
        if (player != null) {
            player.sessionStartAgo = System.currentTimeMillis();
            playTimeMap.put(uuid, player);
            savePlayTimes();
        }
    }
}
