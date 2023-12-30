package io.github.kh4f;

import util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Objects;

public class MainFrame extends JFrame {

    private JPanel mainPanel;
    private JTable deck1Table;
    private JTable deck2Table;
    private JTable shownCards1Table;
    private JTable shownCards2Table;
    private JButton playCardButton;
    private JLabel player1Label;
    private JLabel player2Label;
    private JLabel moveCounterLabel;
    private JLabel currCardImg1;
    private JLabel currCardImg2;

    private Game game;
    private final EndGameDialog endGameDialog;

    private static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    public MainFrame() {
        this.setTitle("War");
        this.setContentPane(mainPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setJMenuBar(createMenuBar());
        this.pack();
        this.setLocation((screenSize.width - mainPanel.getWidth()) / 2, (screenSize.height - mainPanel.getHeight()) / 2);

        try {
            game = new Game();
        } catch (Deck.DeckException e) {
            SwingUtils.showErrorMessageBox(e);
        }
        endGameDialog = new EndGameDialog(this::newGame, this);

        JTableUtils.initJTableForArray(deck1Table, 60, false, false, false, false);
        JTableUtils.initJTableForArray(deck2Table, 60, false, false, false, false);
        JTableUtils.initJTableForArray(shownCards1Table, 80, false, false, false, false);
        JTableUtils.initJTableForArray(shownCards2Table, 80, false, false, false, false);

        updateView();

        playCardButton.addActionListener(actionEvent -> {
            try {
                game.makeMove();
                updateView();
            } catch (Exception e) {
                SwingUtils.showErrorMessageBox(e);
            }
        });
    }

    private void updateView() {
        moveCounterLabel.setText("Move: " + game.getCurrentMove());

        try {
            String img1Path = game.getShownCards1().isEmpty() ? "/cards/card-frame-1.png" : "/cards/"+ game.getShownCards1().checkTopCard().toString() + ".png";
            currCardImg1.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource(img1Path))));

            String img2Path = game.getShownCards2().isEmpty() ? "/cards/card-frame-2.png" : "/cards/"+ game.getShownCards2().checkTopCard().toString() + ".png";
            currCardImg2.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource(img2Path))));
        } catch (Deck.DeckException e) {
            SwingUtils.showErrorMessageBox(e);
        }

        writeStringCardsArrToJTable(deck1Table, game.getDeck1().toStringArray());
        writeStringCardsArrToJTable(deck2Table, game.getDeck2().toStringArray());
        writeStringCardsArrToJTable(shownCards1Table, game.getShownCards1().toStringArray());
        writeStringCardsArrToJTable(shownCards2Table, game.getShownCards2().toStringArray());

        int moveWinner = game.getMoveWinner();
        if (moveWinner == 1) {
            player1Label.setText("<html>⠀⠀⠀✔<br>PLAYER 1</html>");
            player2Label.setText("<html>⠀⠀⠀✖<br>PLAYER 2</html>");
        } else if (moveWinner == 2) {
            player1Label.setText("<html>⠀⠀⠀✖<br>PLAYER 1</html>");
            player2Label.setText("<html>⠀⠀⠀✔<br>PLAYER 2</html>");
        } else if (game.isCardDispute()) {
            player1Label.setText("<html>⠀⠀⠀⚔️<br>PLAYER 1</html>");
            player2Label.setText("<html>⠀⠀⠀⚔️<br>PLAYER 2</html>");
        } else {
            player1Label.setText("<html>⠀⠀⠀—<br>PLAYER 1</html>");
            player2Label.setText("<html>⠀⠀⠀—<br>PLAYER 2</html>");
        }

        if (game.isGameDraw() || (game.getGameWinner() == 1) || (game.getGameWinner() == 2)) {
            String dialogText = "";
            if (game.isGameDraw()) {
                dialogText = "DRAW";
            } else if (game.getGameWinner() == 1) {
                dialogText = "Winner: PLAYER 1";
            } else if (game.getGameWinner() == 2) {
                dialogText = "Winner: PLAYER 2";
            }
            endGameDialog.showDialog(dialogText, this);
            this.setEnabled(false);
        }
    }

    private JMenuItem createMenuItem(String text, String shortcut, ActionListener listener) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.addActionListener(listener);
        if (shortcut != null) {
            menuItem.setAccelerator(KeyStroke.getKeyStroke(shortcut.replace('+', ' ')));
        }
        return menuItem;
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBarMain = new JMenuBar();
        JMenu menuGame = new JMenu("Game options");
        menuBarMain.add(menuGame);
        menuGame.add(createMenuItem("New game", "ctrl+N", e -> newGame()));
        menuGame.addSeparator();
        menuGame.add(createMenuItem("Exit", "ctrl+X", e -> System.exit(0)));
        JMenu menuHelp = new JMenu("Info");
        menuBarMain.add(menuHelp);
        menuHelp.add(createMenuItem("Rules", "ctrl+R", e -> JOptionPane.showMessageDialog(this, """
                Колода раздаётся поровну всем игрокам. Каждый ход игроки снимают верхнюю карту
                из своей стопки и кладут её в центр стола в открытом виде. Тот игрок, чья карта оказалась
                старше всех остальных, снимает свою и «битые» карты и кладёт их в низ своей стопки.
                Игрок, потерявший все свои карты, выбывает из игры. Победителем считается игрок,
                в стопке у которого окажется вся колода.

                Если у двух и более игроков окажутся одинаковые карты, то каждый из этих игроков
                кладет сверху ещё по одной карте, и тот, чья карта оказалась старше всех остальных,
                снимает карты.
                Дополнительное правило: шестёрка бьёт туза.""", "Rules", JOptionPane.QUESTION_MESSAGE)));
        menuHelp.add(createMenuItem("About program", "ctrl+A", e -> JOptionPane.showMessageDialog(this, """
                Название игры: Пьяница (War)
                Описание: карточная игра на Java Swing
                Автор: Гуров А.О. (ВГУ, 2 курс ФКН ИБ, группа 10.1 )
                E-mail: gurov@cs.vsu.ru
                """, "О программе", JOptionPane.INFORMATION_MESSAGE)));

        return menuBarMain;
    }

    private void newGame() {
        try {
            game = new Game();
        } catch (Deck.DeckException e) {
            SwingUtils.showErrorMessageBox(e);
        }
        updateView();
    }

    private void writeStringCardsArrToJTable(JTable table, String[] arr) {
        String[][] tempArr = new String[arr.length][1];
        int i = 0;
        for (String string : arr) {
            tempArr[i++][0] = string;
        }
        JTableUtils.writeArrayToJTable(table, tempArr);
    }
}

