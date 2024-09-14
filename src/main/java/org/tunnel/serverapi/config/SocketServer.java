package org.tunnel.serverapi.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

@Component
public class SocketServer {
    private static final int PORT = 8070;
    private ServerSocket serverSocket;
    private Set<Socket> clients = new HashSet<>();
    private boolean running = true;

    @PostConstruct
    public void startServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started on port " + PORT);
            listenForConnections();
        } catch (IOException e) {
            System.out.println("Error starting server: " + e.getMessage());
        }
    }

    public void listenForConnections() {
        new Thread(() -> {
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    clients.add(clientSocket);
                    System.out.println("Connection established with " + clientSocket.getRemoteSocketAddress());
                 } catch (IOException e) {
                    System.out.println("Exception caught when listening for connections: " + e.getMessage());
                }
            }
        }).start();
    }



    @PreDestroy
    public void stopServer() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            for (Socket client : clients) {
                if (!client.isClosed()) {
                    client.close();
                }
            }
            System.out.println("Server stopped.");
        } catch (IOException e) {
            System.out.println("Error shutting down server: " + e.getMessage());
        }
    }

    public  Set<Socket> getClients() {
        return clients;
    }
}

