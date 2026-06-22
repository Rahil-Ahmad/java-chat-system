package server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserManager {

    // username -> handler mapping, thread-safe
    private final Map<String, ClientHandler> clients = new ConcurrentHashMap<>();

    public boolean addUser(String username, ClientHandler handler) {
        if (clients.containsKey(username)) return false;
        clients.put(username, handler);
        return true;
    }

    public void removeUser(String username) {
        clients.remove(username);
    }

    public ClientHandler getClient(String username) {
        return clients.get(username);
    }

    public boolean isOnline(String username) {
        return clients.containsKey(username);
    }

    public List<String> getOnlineUsernames() {
        List<String> names = new ArrayList<>(clients.keySet());
        Collections.sort(names);
        return names;
    }

    public List<ClientHandler> getAllHandlers() {
        return new ArrayList<>(clients.values());
    }

    public int count() {
        return clients.size();
    }
}
