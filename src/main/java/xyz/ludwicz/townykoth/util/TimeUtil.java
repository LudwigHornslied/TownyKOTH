package xyz.ludwicz.townykoth.util;

public class TimeUtil {

    public static String digitalTime(long time) {
        String hour = "";

        if (time > 3600000) {
            hour = String.format("%02d", ((int) time / 3600000)) + ":";
        }

        String out = String.format("%02d", ((int) time % 3600000 / 60000)) + ":" + String.format("%02d", ((int) time % 60000 / 1000));

        return hour + out;
    }

    public static String formatTime(long time) {
        StringBuilder builder = new StringBuilder();

        if (time > 86400000L)
            builder.append(time / 86400000L + " days ");
        if (time > 3600000L)
            builder.append(time % 86400000L / 3600000L + " hours ");
        if (time > 60000L)
            builder.append(time % 3600000L / 60000L + " minutes ");
        builder.append(time % 60000L / 1000L + " seconds");

        return builder.toString();
    }
}
