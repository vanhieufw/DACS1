package com.movie.network;

import java.io.*;
import java.net.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class SocketServer {
    private ServerSocket serverSocket;
    private int port;
    private CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();

    public SocketServer(int port) {
        this.port = port;
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server đang chạy trên cổng " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                clients.add(clientHandler);
                ThreadManager.execute(clientHandler); // Sử dụng thread pool thay vì new Thread
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcast(String message) {
        clients.removeIf(client -> !client.isValid());
        for (ClientHandler client : clients) {
            if (client.isValid()) {
                client.sendMessage(message);
            }
        }
    }

    public void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    public void stop() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            for (ClientHandler client : clients) {
                client.stop();
            }
            ThreadManager.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler implements Runnable {
    private Socket socket;
    private SocketServer server;
    private PrintWriter out;
    private BufferedReader in;
    private volatile boolean isValid;
    private String clientId; // Thêm để quản lý session

    public ClientHandler(Socket socket, SocketServer server) {
        this.socket = socket;
        this.server = server;
        this.isValid = true;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            isValid = false;
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            String message;
            while (isValid && (message = in.readLine()) != null) {
                System.out.println("Nhận từ client: " + message + " trên thread " + Thread.currentThread().getName());
                handleMessage(message); // Xử lý tin nhắn
            }
        } catch (IOException e) {
            System.err.println("Client disconnected: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    private void handleMessage(String message) {
        if (message.startsWith("Login:")) {
            clientId = message.split(":")[1];
            sendMessage("SESSION:" + clientId); // Gửi session ID
            System.out.println(clientId + " đã đăng nhập trên " + Thread.currentThread().getName());
        } else if (message.contains("?session=")) {
            String[] parts = message.split("\\?session=");
            String command = parts[0];
            String session = parts[1];
            if (command.startsWith("LOCK_SEATS:") && clientId != null && clientId.equals(session)) {
                server.broadcast(command + ":" + clientId); // Xử lý với session hợp lệ
            } else {
                sendMessage("ERROR: Session không hợp lệ");
            }
        } else {
            sendMessage("ERROR: Chưa đăng nhập hoặc tin nhắn không hợp lệ");
        }
    }

    public void sendMessage(String message) {
        if (isValid && out != null) {
            out.println(message);
        }
    }

    public boolean isValid() {
        return isValid && socket != null && !socket.isClosed();
    }

    public void stop() {
        isValid = false;
        cleanup();
    }

    private void cleanup() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            server.removeClient(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Getter cho clientId (nếu cần)
    public String getClientId() {
        return clientId;
    }
}