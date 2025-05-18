import javax.swing.*;

public class GameFrame extends JFrame {

    public GameFrame() {
        add(new GamePanel());
        setTitle(Settings.GAME_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        new GameFrame();
    }
}