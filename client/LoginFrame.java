package client;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

import common.Message;
import common.MessageType;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JTextField hostField;
    private JTextField portField;
    private JButton    connectBtn;
    private JLabel     statusLabel;

    public LoginFrame() {
        setTitle("Java Chat");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(420, 340);
        setLocationRelativeTo(null);
        setResizable(false);
        buildUI();
        setVisible(true);
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(18, 18, 30));
        root.setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel title = new JLabel("Java Chat", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(130, 100, 240));
        title.setBorder(new EmptyBorder(0, 0, 6, 0));

        JLabel sub = new JLabel("Multi-client chat application", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(new Color(120, 120, 150));
        sub.setBorder(new EmptyBorder(0, 0, 22, 0));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(title, BorderLayout.NORTH);
        header.add(sub,   BorderLayout.SOUTH);

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 12));
        form.setOpaque(false);

        usernameField = field("Username");
        hostField     = field("localhost");
        portField     = field("9999");

        form.add(label("Username"));  form.add(usernameField);
        form.add(label("Server IP")); form.add(hostField);
        form.add(label("Port"));      form.add(portField);

        connectBtn = new JButton("Connect");
        connectBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        connectBtn.setBackground(new Color(90, 60, 200));
        connectBtn.setForeground(Color.WHITE);
        connectBtn.setFocusPainted(false);
        connectBtn.setBorderPainted(false);
        connectBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        connectBtn.addActionListener(e -> attemptConnect());
        form.add(new JLabel());
        form.add(connectBtn);

        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(220, 80, 80));
        statusLabel.setBorder(new EmptyBorder(10, 0, 0, 0));

        root.add(header,      BorderLayout.NORTH);
        root.add(form,        BorderLayout.CENTER);
        root.add(statusLabel, BorderLayout.SOUTH);

        add(root);
        getRootPane().setDefaultButton(connectBtn);
    }

    private JTextField field(String placeholder) {
        JTextField f = new JTextField(placeholder);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBackground(new Color(30, 30, 46));
        f.setForeground(new Color(100, 100, 130));
        f.setCaretColor(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 90)),
            new EmptyBorder(6, 10, 6, 10)
        ));
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (f.getText().equals(placeholder)) {
                    f.setText("");
                    f.setForeground(new Color(220, 220, 240));
                }
            }
            public void focusLost(FocusEvent e) {
                if (f.getText().isEmpty()) {
                    f.setText(placeholder);
                    f.setForeground(new Color(100, 100, 130));
                }
            }
        });
        return f;
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        l.setForeground(new Color(160, 160, 190));
        return l;
    }

    private void attemptConnect() {
        String username = usernameField.getText().trim();
        String host     = hostField.getText().trim();
        String portText = portField.getText().trim();

        if (username.isEmpty() || username.equals("Username")) {
            statusLabel.setText("Please enter a username.");
            return;
        }
        if (username.length() > 20) {
            statusLabel.setText("Username must be 20 characters or less.");
            return;
        }

        int port;
        try {
            port = Integer.parseInt(portText);
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid port number.");
            return;
        }

        connectBtn.setEnabled(false);
        statusLabel.setText("Connecting...");

        new Thread(() -> {
            try {
                Socket socket = new Socket(host, port);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                // register username with server
                out.writeObject(new Message(username, "", MessageType.JOIN));
                out.flush();

                SwingUtilities.invokeLater(() -> {
                    dispose();
                    new ChatFrame(socket, out, in, username);
                });

            } catch (IOException ex) {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Cannot connect: " + ex.getMessage());
                    connectBtn.setEnabled(true);
                });
            }
        }).start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}
