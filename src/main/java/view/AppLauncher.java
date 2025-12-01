package view;

import javax.swing.SwingUtilities;
import java.io.IOException;

public class AppLauncher {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new WatcherMainUI();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
