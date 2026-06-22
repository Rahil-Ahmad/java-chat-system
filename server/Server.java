
package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static final int PORT = 9999;
    private final UserManager userManager = new UserManager();

    public void start() {
        System.out.println("Server starting on port " + PORT + "...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Ready. Waiting for connections.");

            while (true) {
                Socket client = serverSocket.accept();
                System.out.println("New connection: " + client.getInetAddress().getHostAddress());
                new Thread(new ClientHandler(client, userManager)).start();
            }

        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new Server().start();
    }
}
