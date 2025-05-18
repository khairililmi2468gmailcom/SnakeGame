import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GamePanel extends JPanel implements ActionListener {

    private Snake snake;
    private Food food;
    private boolean running = false;
    private Timer timer;
    private int score;
    private int level;
    private JButton[] levelButtons;
    private JButton restartButton;
    private JButton menuButton;
    private JPanel controlPanel;
    private boolean gameStarted = false;
    private boolean gameOver = false; // State untuk menandakan game over

    public GamePanel() {
        setPreferredSize(new Dimension(Settings.GAME_WIDTH, Settings.GAME_HEIGHT + 80));
        setBackground(Settings.BACKGROUND_COLOR);
        setFocusable(true);
        addKeyListener(new MyKeyAdapter());
        setLayout(null);
        setupControlPanel();
        showLevelSelection(); // Tampilkan menu level di awal
    }

    private void setupControlPanel() {
        controlPanel = new JPanel();
        controlPanel.setBounds(0, Settings.GAME_HEIGHT, Settings.GAME_WIDTH, 80);
        controlPanel.setBackground(Settings.BACKGROUND_COLOR.darker());
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        add(controlPanel);
    }

    private void showLevelSelection() {
        controlPanel.removeAll();
        JLabel levelLabel = new JLabel("Pilih Level:");
        levelLabel.setForeground(Settings.SCORE_COLOR);
        levelLabel.setFont(Settings.SCORE_FONT.deriveFont(Font.PLAIN, 18));
        controlPanel.add(levelLabel);

        levelButtons = new JButton[3];
        int[] delays = {200, 150, 100};
        String[] levelLabels = {"Lambat", "Sedang", "Cepat"};

        for (int i = 0; i < levelButtons.length; i++) {
            levelButtons[i] = new JButton(levelLabels[i]);
            levelButtons[i].setFocusable(false);
            final int selectedLevel = i + 1;
            final int delay = delays[i];
            levelButtons[i].addActionListener(e -> {
                level = selectedLevel;
                startGame(delay);
                gameStarted = true;
                gameOver = false; // Reset state game over
                controlPanel.removeAll(); // Bersihkan tombol level
                controlPanel.revalidate();
                controlPanel.repaint();
                requestFocusInWindow();
            });
            controlPanel.add(levelButtons[i]);
        }
        controlPanel.revalidate();
        controlPanel.repaint();
        repaint(); // Pastikan panel ter-render ulang
    }

    public void startGame(int initialDelay) {
        snake = new Snake();
        food = new Food();
        running = true;
        score = 0;
        timer = new Timer(initialDelay, this);
        timer.start();
        gameOver = false;
        requestFocusInWindow();
    }

    public void restartGame() {
        startGame(getLevelDelay(level));
        running = true;
        gameOver = false;
        controlPanel.removeAll();
        controlPanel.revalidate();
        controlPanel.repaint();
        requestFocusInWindow();
    }

    private int getLevelDelay(int level) {
        switch (level) {
            case 1: return 200;
            case 2: return 150;
            case 3: return 100;
            default: return 150;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        draw(g2d);
    }

    public void draw(Graphics2D g2d) {
        // Gambar latar belakang area game
        g2d.setColor(Settings.BACKGROUND_COLOR);
        g2d.fillRect(0, 0, Settings.GAME_WIDTH, Settings.GAME_HEIGHT);

        // Gambar border area game (opsional)
        g2d.setColor(Color.GRAY);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRect(0, 0, Settings.GAME_WIDTH - 1, Settings.GAME_HEIGHT - 1);

        if (running) {
            food.draw(g2d, 0, 0);
            snake.draw(g2d, 0, 0);
        } else if (gameOver) {
            gameOverScreen(g2d);
        }
        drawScore(g2d);
    }

    public void drawScore(Graphics2D g2d) {
        g2d.setColor(Settings.SCORE_COLOR);
        g2d.setFont(Settings.SCORE_FONT);
        g2d.drawString("Score: " + score, 10, Settings.GAME_HEIGHT + 25);
        g2d.drawString("Level: " + level, Settings.GAME_WIDTH - 150, Settings.GAME_HEIGHT + 25);
    }

    public void newFood() {
        food.newFood(0, 0, Settings.GAME_WIDTH, Settings.GAME_HEIGHT);
    }

    public void checkCollision() {
        if (snake.getHeadX() == food.getX() && snake.getHeadY() == food.getY()) {
            snake.increaseLength();
            score++;
            int currentDelay = timer.getDelay();
            if (currentDelay > Settings.MIN_DELAY && score % Settings.LEVEL_UP_SCORE == 0) {
                level++;
                timer.stop();
                timer = new Timer(Math.max(Settings.MIN_DELAY, currentDelay - Settings.DELAY_DECREMENT), this);
                timer.start();
            }
            newFood();
        }

        for (int i = 1; i < snake.getBodyParts(); i++) {
            if (snake.getX()[0] == snake.getX()[i] && snake.getY()[0] == snake.getY()[i]) {
                running = false;
                gameOver = true; // Set state game over
                timer.stop();
                showGameOverButtons();
                break;
            }
        }
    }

    private void showGameOverButtons() {
        controlPanel.removeAll();
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        restartButton = new JButton("Restart");
        restartButton.setFocusable(false);
        restartButton.addActionListener(e -> restartGame());
        controlPanel.add(restartButton);

        menuButton = new JButton("Menu");
        menuButton.setFocusable(false);
        menuButton.addActionListener(e -> {
            gameStarted = false;
            gameOver = false;
            showLevelSelection();
        });
        controlPanel.add(menuButton);

        controlPanel.revalidate();
        controlPanel.repaint();
        requestFocusInWindow();
    }

    public void gameOverScreen(Graphics2D g2d) {
        g2d.setColor(Color.RED);
        g2d.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metrics1 = getFontMetrics(g2d.getFont());
        g2d.drawString("Final Score: " + score,
                (Settings.GAME_WIDTH - metrics1.stringWidth("Final Score: " + score)) / 2,
                Settings.GAME_HEIGHT / 2 - 50);

        g2d.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics2 = getFontMetrics(g2d.getFont());
        g2d.drawString("Game Over", (Settings.GAME_WIDTH - metrics2.stringWidth("Game Over")) / 2, Settings.GAME_HEIGHT / 2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            snake.move();
            checkCollision();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (running) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        if (snake.getDirection() != 'R') {
                            snake.setDirection('L');
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (snake.getDirection() != 'L') {
                            snake.setDirection('R');
                        }
                        break;
                    case KeyEvent.VK_UP:
                        if (snake.getDirection() != 'D') {
                            snake.setDirection('U');
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        if (snake.getDirection() != 'U') {
                            snake.setDirection('D');
                        }
                        break;
                }
            } else if (gameOver && e.getKeyCode() == KeyEvent.VK_SPACE) {
                restartGame(); // Opsi restart dengan spasi saat game over
            }
        }
    }
}