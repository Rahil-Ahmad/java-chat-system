package client;

import common.Message;
import common.MessageType;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class ChatFrame extends JFrame {

    private final Socket             socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream  in;
    private final String             myUsername;

    private JTextPane              chatArea;
    private JTextField             inputField;
    private JButton                sendBtn;
    private JList<String>          userList;
    private DefaultListModel<String> userListModel;
    private JLabel                 statusLabel;
    private JComboBox<String>      recipientBox;

    private static final Color BG_DARK    = new Color(18,  18,  30);
    private static final Color BG_MID     = new Color(26,  26,  42);
    private static final Color BG_PANEL   = new Color(22,  22,  36);
    private static final Color ACCENT     = new Color(100, 70,  220);
    private static final Color TEXT_MAIN  = new Color(220, 220, 240);
    private static final Color TEXT_MUTED = new Color(120, 120, 150);
    private static final Color COL_PUBLIC  = new Color(140, 200, 140);
    private static final Color COL_PRIVATE = new Color(220, 170, 80);
    private static final Color COL_SYSTEM  = new Color(110, 140, 200);
    private static final Color COL_ERROR   = new Color(220, 80,  80);
    private static final Color COL_ME      = new Color(150, 120, 255);

    public ChatFrame(Socket socket, ObjectOutputStream out, ObjectInputStream in, String username) {
        this.socket     = socket;
        this.out        = out;
        this.in         = in;
        this.myUsername = username;

        setTitle("Chat — " + username);
        setSize(900, 640);
        setMinimumSize(new Dimension(700, 500));
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        buildUI();
        setVisible(true);
        startReceiver();
        inputField.requestFocusInWindow();

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { exit(); }
        });
    }

    private void buildUI() {
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout());
        add(topBar(),  BorderLayout.NORTH);
        add(mainArea(), BorderLayout.CENTER);
        add(inputBar(), BorderLayout.SOUTH);
    }

    private JPanel topBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BG_PANEL);
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(50, 50, 70)),
            new EmptyBorder(10, 16, 10, 16)
        ));

        JLabel title = new JLabel("Java Chat");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(new Color(130, 100, 240));

        statusLabel = new JLabel("Connected as " + myUsername);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(TEXT_MUTED);

        JButton leaveBtn = new JButton("Leave");
        leaveBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        leaveBtn.setBackground(new Color(180, 50, 50));
        leaveBtn.setForeground(Color.WHITE);
        leaveBtn.setFocusPainted(false);
        leaveBtn.setBorderPainted(false);
        leaveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        leaveBtn.addActionListener(e -> exit());

        bar.add(title,       BorderLayout.WEST);
        bar.add(statusLabel, BorderLayout.CENTER);
        bar.add(leaveBtn,    BorderLayout.EAST);
        return bar;
    }

    private JSplitPane mainArea() {
        chatArea = new JTextPane();
        chatArea.setEditable(false);
        chatArea.setBackground(BG_DARK);
        chatArea.setForeground(TEXT_MAIN);
        chatArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        chatArea.setBorder(new EmptyBorder(12, 16, 12, 16));

        JScrollPane chatScroll = new JScrollPane(chatArea);
        chatScroll.setBorder(null);
        chatScroll.setBackground(BG_DARK);

        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        userList.setBackground(BG_PANEL);
        userList.setForeground(TEXT_MAIN);
        userList.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userList.setSelectionBackground(new Color(60, 50, 100));
        userList.setSelectionForeground(Color.WHITE);
        userList.setBorder(new EmptyBorder(8, 10, 8, 10));
        userList.setCellRenderer(new UserCellRenderer());

        // double click to DM
        userList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String selected = userList.getSelectedValue();
                    if (selected != null && !selected.equals(myUsername)) {
                        recipientBox.setSelectedItem(selected);
                        inputField.requestFocusInWindow();
                    }
                }
            }
        });

        JPanel sidebarHeader = new JPanel(new BorderLayout());
        sidebarHeader.setBackground(BG_PANEL);
        sidebarHeader.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(50, 50, 70)),
            new EmptyBorder(8, 12, 8, 12)
        ));
        JLabel usersTitle = new JLabel("Online Users");
        usersTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        usersTitle.setForeground(TEXT_MUTED);
        sidebarHeader.add(usersTitle, BorderLayout.WEST);

        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(BG_PANEL);
        sidebar.add(sidebarHeader, BorderLayout.NORTH);
        sidebar.add(new JScrollPane(userList), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, chatScroll, sidebar);
        split.setDividerLocation(650);
        split.setDividerSize(1);
        split.setBorder(null);
        return split;
    }

    private JPanel inputBar() {
        JPanel bar = new JPanel(new BorderLayout(8, 0));
        bar.setBackground(BG_MID);
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(50, 50, 70)),
            new EmptyBorder(12, 14, 12, 14)
        ));

        recipientBox = new JComboBox<>();
        recipientBox.addItem("Everyone");
        recipientBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        recipientBox.setBackground(new Color(30, 30, 46));
        recipientBox.setForeground(TEXT_MAIN);
        recipientBox.setPreferredSize(new Dimension(130, 36));

        inputField = new JTextField();
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        inputField.setBackground(new Color(30, 30, 46));
        inputField.setForeground(TEXT_MAIN);
        inputField.setCaretColor(Color.WHITE);
        inputField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 90)),
            new EmptyBorder(6, 12, 6, 12)
        ));
        inputField.addActionListener(e -> sendMessage());
        inputField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) recipientBox.setSelectedIndex(0);
            }
        });

        sendBtn = new JButton("Send");
        sendBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        sendBtn.setBackground(ACCENT);
        sendBtn.setForeground(Color.WHITE);
        sendBtn.setFocusPainted(false);
        sendBtn.setBorderPainted(false);
        sendBtn.setPreferredSize(new Dimension(80, 36));
        sendBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendBtn.addActionListener(e -> sendMessage());

        JPanel right = new JPanel(new BorderLayout(8, 0));
        right.setOpaque(false);
        right.add(recipientBox, BorderLayout.WEST);
        right.add(sendBtn,      BorderLayout.EAST);
        bar.add(inputField, BorderLayout.CENTER);
        bar.add(right,      BorderLayout.EAST);
        return bar;
    }

    private void startReceiver() {
        Thread t = new Thread(() -> {
            try {
                while (true) {
                    Message msg = (Message) in.readObject();
                    // invokeLater required — Swing updates must run on EDT
                    SwingUtilities.invokeLater(() -> handleIncoming(msg));
                }
            } catch (EOFException | java.net.SocketException ignored) {
                SwingUtilities.invokeLater(() -> appendSystem("Disconnected from server."));
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> appendSystem("Error: " + e.getMessage()));
            }
        }, "receiver");
        t.setDaemon(true);
        t.start();
    }

    private void handleIncoming(Message msg) {
        switch (msg.getType()) {
            case PUBLIC    -> appendPublic(msg);
            case PRIVATE   -> appendPrivate(msg);
            case JOIN      -> appendSystem(msg.getContent());
            case LEAVE     -> appendSystem(msg.getContent());
            case USER_LIST -> refreshUserList(msg.getContent());
            case ERROR     -> appendError(msg.getContent());
        }
    }

    private void sendMessage() {
        String text = inputField.getText().trim();
        if (text.isEmpty()) return;

        String target   = (String) recipientBox.getSelectedItem();
        boolean isDM    = target != null && !target.equals("Everyone");
        Message msg     = isDM
                ? new Message(myUsername, target, text, MessageType.PRIVATE)
                : new Message(myUsername, text, MessageType.PUBLIC);

        try {
            out.writeObject(msg);
            out.flush();
            out.reset();
            inputField.setText("");
        } catch (IOException e) {
            appendError("Send failed: " + e.getMessage());
        }
    }

    private void appendPublic(Message msg) {
        boolean me = msg.getSender().equals(myUsername);
        append("[" + msg.getTimestamp() + "] ", TEXT_MUTED, false);
        append(msg.getSender() + ": ", me ? COL_ME : COL_PUBLIC, true);
        append(msg.getContent() + "\n", TEXT_MAIN, false);
    }

    private void appendPrivate(Message msg) {
        boolean me = msg.getSender().equals(myUsername);
        String  label = me ? "→ " + msg.getRecipient() : msg.getSender() + " → you";
        append("[" + msg.getTimestamp() + "] ", TEXT_MUTED, false);
        append("[PM] " + label + ": ", COL_PRIVATE, true);
        append(msg.getContent() + "\n", new Color(240, 210, 140), false);
    }

    private void appendSystem(String text) {
        append("─── " + text + " ───\n", COL_SYSTEM, false);
    }

    private void appendError(String text) {
        append("! " + text + "\n", COL_ERROR, false);
    }

    private void append(String text, Color color, boolean bold) {
        javax.swing.text.StyledDocument doc = chatArea.getStyledDocument();
        javax.swing.text.SimpleAttributeSet attr = new javax.swing.text.SimpleAttributeSet();
        javax.swing.text.StyleConstants.setForeground(attr, color);
        javax.swing.text.StyleConstants.setBold(attr, bold);
        try {
            doc.insertString(doc.getLength(), text, attr);
            chatArea.setCaretPosition(doc.getLength());
        } catch (Exception ignored) {}
    }

    private void refreshUserList(String csv) {
        String[] users = csv.split(",");
        Arrays.sort(users);
        userListModel.clear();
        for (String u : users) {
            if (!u.isBlank()) userListModel.addElement(u.trim());
        }
        statusLabel.setText("Connected as " + myUsername + "  |  " + userListModel.size() + " online");

        String current = (String) recipientBox.getSelectedItem();
        recipientBox.removeAllItems();
        recipientBox.addItem("Everyone");
        for (String u : users) {
            if (!u.isBlank() && !u.trim().equals(myUsername)) {
                recipientBox.addItem(u.trim());
            }
        }
        if (current != null) recipientBox.setSelectedItem(current);
    }

    private void exit() {
        try { socket.close(); } catch (IOException ignored) {}
        dispose();
        System.exit(0);
    }

    private class UserCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index, boolean isSelected, boolean hasFocus) {
            JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, hasFocus);
            String name = (String) value;
            boolean isMe = name.equals(myUsername);
            lbl.setText((isMe ? "● " : "○ ") + name + (isMe ? " (you)" : ""));
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lbl.setBorder(new EmptyBorder(4, 6, 4, 6));
            if (!isSelected) {
                lbl.setBackground(BG_PANEL);
                lbl.setForeground(isMe ? COL_ME : TEXT_MAIN);
            }
            return lbl;
        }
    }
}
