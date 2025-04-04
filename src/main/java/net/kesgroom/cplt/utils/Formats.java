package net.kesgroom.cplt.utils;

public class Formats {
    public static String formatRemainingTime(long remainingTime) {
        long hours = remainingTime / 3600000; // 1 hora = 3600000 ms
        long minutes = (remainingTime % 3600000) / 60000; // 1 minuto = 60000 ms
        long seconds = (remainingTime % 60000) / 1000; // 1 segundo = 1000 ms
        String str = "";

        if (hours > 0) {
            str += String.format("%d horas ", hours);
        }
        if (minutes > 0) {
            str += String.format("%d minutos ", minutes);
        }
        if (hours <= 0 && seconds > 0) {
            str += String.format("%d segundos. ", seconds);
        }
        System.out.println(str);
        return str;
    }
}
