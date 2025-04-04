package net.kesgroom.cplt.utils;

import net.kesgroom.cplt.managers.ConfigManager;
import net.kesgroom.cplt.managers.PlayerTimeManager;
import net.kesgroom.cplt.services.PlayerTimeService;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;


public final class Alerts {

    private static final PlayerTimeService playerTimeService = new PlayerTimeService();

    public static void titleAllUsers(ServerWorld world, String titleText, @Nullable String subtitleText) {
        for (ServerPlayerEntity player : world.getPlayers()) {
            if (player.networkHandler == null) continue;
            String uuid = player.getUuid().toString();
            long remaining = getRemainingTime(uuid);

            Text title = Text.literal(titleText).formatted(Formatting.GOLD);
            Text subtitle = buildSubtitle(subtitleText, remaining);
            TitleHelper.sendTitle(player, title, subtitle, 10, 70, 20);
        }
    }

    private static long getRemainingTime(String uuid) {
        PlayerTimeManager time = playerTimeService.getPlayerTime(uuid);
        return time != null ? time.getRemaining_time() : ConfigManager.getInstance().getMaxTime();
    }

    private static Text buildSubtitle(@Nullable String customText, long remainingTime) {
        String text = customText != null ? customText :
                String.format("Tiempo restante: %s", Formats.formatRemainingTime(remainingTime));

        Formatting color = customText != null ? Formatting.GRAY :
                TitleHelper.choiceColorByRemainingTime(remainingTime);

        return Text.literal(text).formatted(color);
    }
}
