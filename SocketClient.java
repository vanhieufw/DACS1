package com.movie.util;

import java.io.*;
import java.net.*;

public class SocketClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 5000;

    public static void sendMessage(String message) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}