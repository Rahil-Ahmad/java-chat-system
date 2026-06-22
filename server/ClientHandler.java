package server;

import common.Message;
import common.MessageType;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final UserManager userManager;
    private ObjectOutputStream out;
    private ObjectInputStream  in;
    private String username;

    public ClientHandler(Socket socket, UserManager userManager) {
        this.socket      = socket;
        this.userManager = userManager;
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in  = new ObjectInputStream(socket.getInputStream());

            Message loginMsg = (Message) in.readObject();
            username = loginMsg.getSender().trim();

            if (username.isEmpty() || !userManager.addUser(username, this)) {
                sendMessage(new Message("SERVER", username,
                        "Username '" + username + "' is already taken.",
                        MessageType.ERROR));
                close();
                return;
            }

            System.out.println("[+] " + username + " connected. Online: " + userManager.count());
            broadcastUserList();
            broadcast(new Message("SERVER", username + " joined the chat.", MessageType.JOIN));

            Message msg;
            while ((msg = (Message) in.readObject()) != null) {
                handleMessage(msg);
            }

        } catch (EOFException | java.net.SocketException ignored) {
            // client disconnected
        } catch (Exception e) {
            System.err.println("Error [" + username + "]: " + e.getMessage());
        } finally {
            disconnect();
        }
    }

    private void handleMessage(Message msg) {
        switch (msg.getType()) {
            case PUBLIC -> {
                System.out.println("[MSG] " + msg);
                broadcast(msg);
            }
            case PRIVATE -> {
                ClientHandler target = userManager.getClient(msg.getRecipient());
                if (target != null) {
                    target.sendMessage(msg);
                    sendMessage(msg);
                } else {
                    sendMessage(new Message("SERVER", username,
                            msg.getRecipient() + " is not online.", MessageType.ERROR));
                }
            }
            default -> broadcast(msg);
        }
    }

    public synchronized void sendMessage(Message msg) {
        try {
            out.writeObject(msg);
            out.flush();
            out.reset();
        } catch (IOException e) {
            System.err.println("Send failed [" + username + "]: " + e.getMessage());
        }
    }

    private void broadcast(Message msg) {
        for (ClientHandler c : userManager.getAllHandlers()) {
            c.sendMessage(msg);
        }
    }

    private void broadcastUserList() {
        String csv = String.join(",", userManager.getOnlineUsernames());
        broadcast(new Message("SERVER", csv, MessageType.USER_LIST));
    }

    private void disconnect() {
        if (username != null) {
            userManager.removeUser(username);
            System.out.println("[-] " + username + " disconnected. Online: " + userManager.count());
            broadcastUserList();
            broadcast(new Message("SERVER", username + " left the chat.", MessageType.LEAVE));
        }
        close();
    }

    private void close() {
        try { socket.close(); } catch (IOException ignored) {}
    }

    public String getUsername() { return username; }
}
