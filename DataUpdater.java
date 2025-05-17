package com.movie.util;

import com.movie.bus.ShowtimeBUS;
import java.util.Timer;
import java.util.TimerTask;

public class DataUpdater {
    private static final long UPDATE_INTERVAL = 60000; // 1 phút

    public static void startAutoUpdate() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                new ShowtimeBUS().updateShowtimes();
                System.out.println("Dữ liệu đã được cập nhật tự động lúc " + new java.util.Date());
            }
        }, 0, UPDATE_INTERVAL);
    }

    public static void main(String[] args) {
        startAutoUpdate();
    }
}