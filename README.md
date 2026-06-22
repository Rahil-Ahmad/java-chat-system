# Java Chat Application 

A fully functional multi-client desktop chat app built with:
- **Java Swing** (UI)
- **Socket Programming** (networking)
- **Multithreading** (one thread per client)
- **Object Serialization** (Message objects over the wire)
- **OOP** (clean separation: Server / Client / Common)

---

## Screenshots

1. Login Page
![image alt](https://github.com/Rahil-Ahmad/java-chat-system/blob/8ec418c81a7555fb5189e427263489faf81ebb4b/chat1.png)


2. Enter user Details
![image alt](https://github.com/Rahil-Ahmad/java-chat-system/blob/0f4d4c542c1cbaebdd5f01d1633671e5f784a2be/chat2.png)


3. All users Available in chat
![image alt](https://github.com/Rahil-Ahmad/java-chat-system/blob/c95f09b1a4153a649c437ce827509376996781da/chat3.png)


4. When Aleena(user) sends the messege it broadcast it and seen by Every user
![image alt](https://github.com/Rahil-Ahmad/java-chat-system/blob/de85176812bae580319f4153d7c786913facda02/chat4.png)


5. Aleena(user) privatly sends messege to Anas(user)
![image alt](https://github.com/Rahil-Ahmad/java-chat-system/blob/a887290bfee64d50aa9d9f827d8a0d66efaf255f/%20chat5.png)


6.Anas(user) recieves the messege from Aleena(user)
![image alt](https://github.com/Rahil-Ahmad/java-chat-system/blob/c6610618bf093f035984c3abc2771c2c56d43319/chat6.png)


## Project Structure

```
JavaChatApp/
├── common/
│   ├── Message.java        ← Serializable message object (sent over socket)
│   ├── MessageType.java    ← Enum: PUBLIC, PRIVATE, JOIN, LEAVE, USER_LIST, ERROR
│   └── User.java           ← User model
├── server/
│   ├── Server.java         ← Opens ServerSocket, spawns threads
│   ├── ClientHandler.java  ← Runnable: one per connected client
│   └── UserManager.java    ← Thread-safe map of online users
├── client/
│   ├── Client.java         ← Entry point (launches LoginFrame)
│   ├── LoginFrame.java     ← Swing login screen
│   └── ChatFrame.java      ← Main chat window
├── database/
│   └── schema.sql          ← SQLite schema (Phase 5 / optional)
├── compile.bat             ← Windows compile script
└── compile.sh              ← Linux/Mac compile script
```

---

## How to Run

### Step 1 — Compile

**Windows:**
```
compile.bat
```

**Linux / Mac:**
```
chmod +x compile.sh
./compile.sh
```

This creates an `out/` folder with all `.class` files.

### Step 2 — Start the Server

Open a terminal:
```
java -cp out server.Server
```
You should see:
```

Starting on port 9999...
✓ Server ready. Waiting for connections...
```

### Step 3 — Launch Clients

Open one or more terminals (or on different machines):
```
java -cp out client.Client
```

Enter a username and connect. You can open many clients to simulate a multi-user chat.

---

## Features

| Feature | Status |
|---|---|
| Multi-client real-time chat | ✅ |
| Public broadcast messages | ✅ |
| Online users sidebar | ✅ |
| Dark-themed Swing UI | ✅ |
| Join / Leave notifications | ✅ |
| Double-click user to DM | ✅ |
| Duplicate username protection | ✅ |
| Graceful disconnect handling | ✅ |

---

## Java Concepts Used

| Concept | Where |
|---|---|
| Classes & Objects | All files |
| Encapsulation | Message.java, User.java |
| Inheritance | ChatFrame extends JFrame |
| Polymorphism | MessageType enum switching |
| Interfaces | ClientHandler implements Runnable |
| Exception Handling | All network code |
| Collections | UserManager (ConcurrentHashMap) |
| Threads | Server spawns new Thread() per client |
| Socket Programming | Server.java, Client connections |
| Serialization | Message implements Serializable |
| Swing | LoginFrame, ChatFrame |

---

## How It Works (Architecture)

```
Client A (Swing)               Server                    Client B (Swing)
   LoginFrame                ServerSocket                   LoginFrame
       │                          │                               │
       │── Socket connect ───────▶│                               │
       │── LOGIN message ────────▶│                               │
       │                   ClientHandler A                        │
       │                   (new Thread)                           │
       │                          │──────────────────────────────▶│
       │                          │      USER_LIST broadcast      │
       │                                                          │
       │── PUBLIC message ───────▶│                               │
       │                          │──────────────────────────────▶│
       │                          │      broadcast to all clients │
       │                                                          │
       │── PRIVATE msg to B ─────▶│                               │
       │◀─ echo back ─────────────│                               │
       │                          │──────────────────────────────▶│
       │                          │      only to Client B         │
```

---

## Running on Multiple Machines

1. Start the server on one machine
2. Note the server machine's **local IP address** (e.g. `192.168.1.5`)
3. In the client login screen, enter that IP instead of `localhost`
4. Both machines must be on the same network

---

## Phase 5 — Database (Optional Extension)

To add login/registration with SQLite:

1. Download `sqlite-jdbc-x.x.x.jar` from https://github.com/xerial/sqlite-jdbc
2. Add it to your classpath: `javac -cp sqlite-jdbc.jar:out ...`
3. Run the schema: `sqlite3 chat.db < database/schema.sql`
4. In `UserManager.java`, add JDBC connection and verify credentials on login

---

## Tips for Resume

- **"Developed a multi-client desktop chat application"** — yes, this is real
- **"Using Java Swing, Socket Programming, and Multithreading"** — all present
- **"Implemented client-server architecture with concurrent connection handling"** — ConcurrentHashMap + Threads
- **"Designed a serializable message protocol with typed message envelopes"** — Message + MessageType
- **"Extensible design for private messaging, authentication, and file transfer"** — architecture is ready for all of these
