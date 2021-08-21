package xyz.ludwicz.townykoth.util;

public class TimeUtil {

    public static String digitalTime(long time) {
        String hour = "";

        if(time > 3600000) {
            hour = String.format("%02d", ((int) time/3600000)) + ":";
        }

        String out = String.format("%02d", ((int) time%3600000/60000)) + ":" + String.format("%02d", ((int) time%60000/1000));

        return hour + out;
    }
}
