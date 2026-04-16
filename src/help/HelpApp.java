package help;

import javax.swing.SwingUtilities;

public class HelpApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new HelpGui().setVisible(true);
        });
    }
}