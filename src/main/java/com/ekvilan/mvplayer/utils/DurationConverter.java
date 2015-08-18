package com.ekvilan.mvplayer.utils;


import java.util.concurrent.TimeUnit;

public class DurationConverter {

    public String convertDuration(long duration) {
        long hours = TimeUnit.MILLISECONDS.toHours(duration);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration)
                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration));

        String strHours = hours > 0 ? hours + ":" : "";
        String strMinutes = minutes < 10 ? "0" + minutes + ":" : minutes + ":";
        String strSeconds = seconds < 10 ? "0" + seconds : seconds + "";

        return strHours + strMinutes + strSeconds;
    }
}
