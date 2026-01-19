package com.example.practicaltest02v2.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class MyServer {
    private int port;

    public MyServer(int port) {
        this.port = port;
    }

    public void startServer() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("Serverul a pornit pe portul " + port);

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    new Thread(() -> handleClient(clientSocket)).start();
                }
            } catch (Exception e) {
                System.err.println("Eroare Server: " + e.getMessage());
            }
        }).start();
    }

    private void handleClient(Socket socket) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String input = in.readLine();
            if (input == null || !input.contains(",")) {
                out.println("Eroare: Format invalid. Folosește 'op, v1, v2'");
                return;
            }

            String[] parts = input.split(",");
            if (parts.length < 3) {
                out.println("Eroare: Lipsesc argumente.");
                return;
            }

            String operation = parts[0].trim().toLowerCase();
            double v1 = Double.parseDouble(parts[1].trim());
            double v2 = Double.parseDouble(parts[2].trim());
            double result = 0;

            switch (operation) {
                case "add": result = v1 + v2; break;
                case "mul": result = v1 * v2; Thread.sleep(2000); break;
                default:
                    out.println("Eroare: Operatie necunoscuta");
                    return;
            }

            out.println(result);
            System.out.println("Calculat: " + input + " = " + result);

        } catch (Exception e) {
            System.err.println("Eroare client: " + e.getMessage());
        } finally {
            try { socket.close(); } catch (Exception ignored) {}
        }
    }

    public static void main(String[] args) {
        MyServer server = new MyServer(5000);
        server.startServer();
    }
}