package io.github.kh4f;

import util.*;
import javax.swing.*;
import java.awt.*;

public class EndGameDialog extends JWindow {
    private JPanel mainPanel;
    private JButton restartButton;
    private JLabel headerLabel;

    JFrame mainFrame;

    private static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    public EndGameDialog(Runnable newGameAction, JFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.setContentPane(mainPanel);
        this.pack();

        restartButton.addActionListener(actionEvent -> {
            try {
                newGameAction.run();
                this.setVisible(false);
                mainFrame.setEnabled(true);
            } catch (Exception e) {
                SwingUtils.showErrorMessageBox(e);
            }
        });
    }

    public void showDialog(String dialogText, JFrame main) {
        headerLabel.setText(dialogText);
        this.setLocation(main.getLocation().x + main.getWidth() / 2 - this.getWidth() / 2, main.getLocation().y + main.getHeight() / 2 - this.getHeight() / 2);
        this.setVisible(true);
        this.setAlwaysOnTop(true);
    }

}
