import allreverse.Box;
import allreverse.Coord;
import allreverse.Game;
import allreverse.GameState;
import allreverse.Ranges;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Reverse extends JFrame {

    private Game game;
    private JPanel panel;
    private JLabel label;
    private final int COLS = 8;
    private final int ROWS = 8;

    private final int ROWSFIRST = 1;
    private final int COLSFIRST = 2;
    private final int IMAGE_SIZE_FIRST = 250;
    private final int IMAGE_SIZE = 75;

    public static void main(String[] args) {
        new Reverse();
    }

    private Reverse ()
    {
        initPanelMain();
        initFrame();
    }


    private void initPanelMain ()
    {
        panel = new JPanel()
        {
            @Override
            protected void paintComponent(Graphics g)
            {
                super.paintComponent(g);
                g.drawImage(getImage("qeasy"), 0, 0, this);
                g.drawImage(getImage("users"), IMAGE_SIZE_FIRST, 0, this);
            }
        };

        panel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent e)
            {
                int x = e.getX() / IMAGE_SIZE_FIRST;
                if (x == 0)
                {
                    remove (panel);
                    ReverseComputer ();
                } else {
                    ReverseHuman ();
                }
            }
        });
        panel.setPreferredSize(new Dimension(
                COLSFIRST * IMAGE_SIZE_FIRST,
                ROWSFIRST * 100));
        add (panel);
    }

    public void ReverseHuman ()
    {
        game = new Game (COLS, ROWS);
        game.start();
        setImages();
        initLabel();
        initPanelHuman();
        initFrame();
    }

    public void ReverseComputer ()
    {
        game = new Game (COLS, ROWS);
        game.start();
        setImages();
        initLabel();
        initPanelComputer();
        initFrame();
    }

    private void initLabel ()
    {
        label = new JLabel("Welcome!");
        add (label, BorderLayout.SOUTH);
    }
    private void initPanelHuman ()
    {
        panel = new JPanel()
        {
            @Override
            protected void paintComponent(Graphics g)
            {
                super.paintComponent(g);
                for (Coord coord : Ranges.getAllCoords())
                {
                    g.drawImage((Image) game.getBox(coord).image,
                            coord.x * IMAGE_SIZE, coord.y * IMAGE_SIZE, this);
                }
            }
        };

        panel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent e)
            {
                int x = e.getX() / IMAGE_SIZE;
                int y = e.getY() / IMAGE_SIZE;
                Coord coord = new Coord (x, y);
                game.pressLeftButtonHuman (coord);
                label.setText(getMessage ());
                panel.repaint();
                if (game.getState() != GameState.PLAYING)
                {
                    if (game.getState() != GameState.WINNER)
                        JOptionPane.showMessageDialog(null, "You won with the count: " + game.getCount ());
                    else
                        JOptionPane.showMessageDialog(null, "Opponent wins with the count: " + game.getCount ());
                }
            }
        });
        panel.setPreferredSize(new Dimension(
                Ranges.getSize().x * IMAGE_SIZE,
                Ranges.getSize().y * IMAGE_SIZE));
        add (panel);
    }
    private void initPanelComputer ()
    {
        panel = new JPanel()
        {
            @Override
            protected void paintComponent(Graphics g)
            {
                super.paintComponent(g);
                for (Coord coord : Ranges.getAllCoords())
                {
                    g.drawImage((Image) game.getBox(coord).image,
                            coord.x * IMAGE_SIZE, coord.y * IMAGE_SIZE, this);
                }
            }
        };

        panel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent e)
            {
                int x = e.getX() / IMAGE_SIZE;
                int y = e.getY() / IMAGE_SIZE;
                Coord coord = new Coord (x, y);
                game.pressLeftButton (coord);
                label.setText(getMessage ());
                panel.repaint();
                if (game.getState() != GameState.PLAYING)
                {
                    if (game.getState() != GameState.WINNER)
                        JOptionPane.showMessageDialog(null, "You won with the count: " + game.getCount ());
                    else
                        JOptionPane.showMessageDialog(null, "Opponent wins with the count: " + game.getCount ());
                }
            }
        });
        panel.setPreferredSize(new Dimension(
                Ranges.getSize().x * IMAGE_SIZE,
                Ranges.getSize().y * IMAGE_SIZE));
        add (panel);
    }

    private String getMessage()
    {
        switch(game.getState())
        {
            case LOST  : return "YOU LOSE!";
            case WINNER: return "CONGRATULATIONS!";
            default    : return "WELCOME!";
        }
    }

    private void initFrame () {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // автоматическое прекращение работы проги
        setTitle("Reversy");
        pack();
        setLocationRelativeTo(null); // игра по центру
        setResizable(false); // неизменяемое окно
        setVisible(true); // видимая игра
        setIconImage(getImage("blue"));
    }

    private void setImages () {
        for (allreverse.Box box : Box.values()) {
            box.image = getImage(box.name().toLowerCase());
        }
    }

    private Image getImage (String name) {
        String filename = "img/" + name.toLowerCase() + ".jpg";
        ImageIcon icon = new ImageIcon(getClass().getResource(filename));
        return icon.getImage();
    }
}
