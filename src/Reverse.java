import allreverse.*;
import allreverse.Box;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class Reverse extends JFrame {
    private final AtomicBoolean isMouseCanBePressed = new AtomicBoolean(true);
    private Game game;
    private JPanel panel;
    public static void main(String[] args) {
        new Reverse();
    }

    private Reverse () {
        initPanelMain();
        initFrame();
    }


    private void initPanelMain () {
        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(getImage("qeasy"), 0, 0, this);
                g.drawImage(getImage("users"), Constants.IMAGE_SIZE_FIRST, 0, this);
            }
        };

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX() / Constants.IMAGE_SIZE_FIRST;
                remove (panel);
                if (x == 0) {
                    ReverseComputer();
                } else {
                    ReverseHuman();
                }
            }
        });
        panel.setPreferredSize(new Dimension(
                2 * Constants.IMAGE_SIZE_FIRST,
                100));
        add(panel);
    }

    public void ReverseHuman () {
        game = new Game (Constants.COLS, Constants.ROWS);
        game.start("Human");
        setImages();
        initLabel();
        initPanelHuman();
        initFrame();
    }

    public void ReverseComputer () {
        game = new Game (Constants.COLS, Constants.ROWS);
        game.start("Computer");
        setImages();
        initLabel();
        initPanelComputer();
        initFrame();
    }

    private void initLabel () {
        JLabel label = new JLabel("Welcome!");
        add(label, BorderLayout.SOUTH);
    }
    private void initPanelHuman() {
        panel = createPanel();
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX() / Constants.IMAGE_SIZE;
                int y = e.getY() / Constants.IMAGE_SIZE;
                Coord coord = new Coord(x, y);
                game.pressLeftButtonHuman(coord);
                panel.repaint();
                if (game.getState() != GameState.PLAYING) {
                    if (game.getState() != GameState.NOONE) {
                        if (game.getState() == GameState.WINNER) {
                            JOptionPane.showMessageDialog(null, "Purple wins! The count is: " + game.getCount());
                        } else {
                            JOptionPane.showMessageDialog(null, "Blue wins! The count is: " + game.getCount());
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "No one has won!");
                    }
                }
            }
        });
        panel.setPreferredSize(new Dimension(
                    Ranges.getSize().x * Constants.IMAGE_SIZE,
                    Ranges.getSize().y * Constants.IMAGE_SIZE));
        add(panel);
    }
    private void initPanelComputer() {
        panel = createPanel();
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (isMouseCanBePressed.getAndSet(false)) {
                    int x = e.getX() / Constants.IMAGE_SIZE;
                    int y = e.getY() / Constants.IMAGE_SIZE;
                    Coord coord = new Coord(x, y);
                    game.pressLeftButton(coord);
                    panel.repaint();
                    if (game.buttonPressed) {
                        game.createBlue(() -> {
                            panel.repaint();
                            isMouseCanBePressed.set(true);
                        });
                    } else {
                        isMouseCanBePressed.set(true);
                    }
                    if (game.getState() != GameState.PLAYING) {
                        if (game.getState() != GameState.NOONE) {
                            JOptionPane.showMessageDialog(null, "You are a " + game.getState() + "! The count of a winner is: " + game.getCount());
                        } else {
                            JOptionPane.showMessageDialog(null, "No one has won!");
                        }
                    }
                }
            }
        });
        panel.setPreferredSize(new Dimension(
                Ranges.getSize().x * Constants.IMAGE_SIZE,
                Ranges.getSize().y * Constants.IMAGE_SIZE));
        add(panel);
    }

    private void initFrame() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // автоматическое прекращение работы проги
        setTitle("Reversy");
        pack();
        setLocationRelativeTo(null); // игра по центру
        setResizable(false); // неизменяемое окно
        setVisible(true); // видимая игра
        setIconImage(getImage("blue"));
    }

    private void setImages() {
        for (allreverse.Box box : Box.values()) {
            box.image = getImage(box.name().toLowerCase());
        }
    }

    private Image getImage(String name) {
        ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("img/" + name.toLowerCase() + ".jpg"));
        return icon.getImage();
    }

    private JPanel createPanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                for (Coord coord : Ranges.getAllCoords()) {
                    g.drawImage(game.getBox(coord).image,
                            coord.x * Constants.IMAGE_SIZE, coord.y * Constants.IMAGE_SIZE, this);
                }
            }
        };
    }
}
