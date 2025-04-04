package net.kesgroom.cplt.utils;

import net.kesgroom.cplt.managers.ConfigManager;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class TitleHelper {

    public static void sendTitle(ServerPlayerEntity player, Text title, Text subtitle, int fadeIn, int stay, int fadeOut) {

        player.networkHandler.sendPacket(new TitleFadeS2CPacket(fadeIn, stay, fadeOut));

        player.networkHandler.sendPacket(new TitleS2CPacket(title));
        player.networkHandler.sendPacket(new SubtitleS2CPacket(subtitle));
    }

    public static Formatting choiceColorByRemainingTime(long remaining) {
        double high = (ConfigManager.getInstance().getMaxTime() * 0.50);
        double medium = (ConfigManager.getInstance().getMaxTime() * 0.35);
        double low = (ConfigManager.getInstance().getMaxTime() * 0.5);

        if (remaining > high) return Formatting.DARK_GREEN;
        if (remaining < high && remaining > medium) return Formatting.YELLOW;
        if (remaining < medium && remaining > low) return Formatting.RED;
        if (remaining < low) return Formatting.DARK_RED;
        return Formatting.GRAY;
    }
}