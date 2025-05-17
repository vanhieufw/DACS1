package com.movie.util;

public class ThreadManager {
    public static void execute(Runnable task) {
        new Thread(task).start();
    }
}