package client;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Client {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(LoginFrame::new);
    }
}
