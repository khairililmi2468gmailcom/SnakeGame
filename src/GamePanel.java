import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Random;
import javax.imageio.ImageIO;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
    private boolean gameOver = false;
    private BufferedImage grassImage;
    private BufferedImage foodImage;
    // Snake body part images
    private BufferedImage headUpImage;
    private BufferedImage headDownImage;
    private BufferedImage headLeftImage;
    private BufferedImage headRightImage;
    private BufferedImage bodyHorizontalImage;
    private BufferedImage bodyVerticalImage;
    private BufferedImage bodyTopLeftImage;
    private BufferedImage bodyTopRightImage;
    private BufferedImage bodyBottomLeftImage;
    private BufferedImage bodyBottomRightImage;
    private BufferedImage tailUpImage;
    private BufferedImage tailDownImage;
    private BufferedImage tailLeftImage;
    private BufferedImage tailRightImage;
    private Random random = new Random();
    private int gameSpeed;
    private List<CollisionParticle> collisionParticles = new ArrayList<>();
    private boolean collisionOccurred = false;
    private int collisionDelay = 2000; // Changed to 2000 for 2-second delay
    private Timer collisionTimer;
    private int tileSize; // Use this instead of Settings.TILE_SIZE

    public GamePanel() {
        // Initialize TILE_SIZE here, so it can be used in the constructor.
        this.tileSize = Settings.TILE_SIZE;
        setPreferredSize(new Dimension(Settings.GAME_WIDTH, Settings.GAME_HEIGHT + 80));
        setBackground(Settings.BACKGROUND_COLOR);
        setFocusable(true);
        addKeyListener(new MyKeyAdapter());
        setLayout(null);
        loadImages();
        setupControlPanel();
        showLevelSelection();
        gameSpeed = Settings.INITIAL_DELAY;
    }

    private void loadImages() {
        try {
            grassImage = loadImage("grass.png");
            foodImage = loadImage("apple.png");
            headUpImage = loadImage("head_up.png");
            headDownImage = loadImage("head_down.png");
            headLeftImage = loadImage("head_left.png");
            headRightImage = loadImage("head_right.png");
            bodyHorizontalImage = loadImage("body_horizontal.png");
            bodyVerticalImage = loadImage("body_vertical.png");
            bodyTopLeftImage = loadImage("body_topleft.png");
            bodyTopRightImage = loadImage("body_topright.png");
            bodyBottomLeftImage = loadImage("body_bottomleft.png");
            bodyBottomRightImage = loadImage("body_bottomright.png");
            tailUpImage = loadImage("tail_up.png");
            tailDownImage = loadImage("tail_down.png");
            tailLeftImage = loadImage("tail_left.png");
            tailRightImage = loadImage("tail_right.png");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load images. Please ensure the 'Graphics' folder is in the correct location.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private BufferedImage loadImage(String path) throws IOException {
        URL imgUrl = getClass().getResource(path);
        if (imgUrl == null) {
            File file = new File("SnakeGame/" + path);
            if (file.exists()) {
                imgUrl = file.toURI().toURL();
            } else {
                throw new IOException("Could not load image: " + path);
            }
        }
        try {
            return ImageIO.read(imgUrl);
        } catch (IOException e) {
            throw new IOException("Error reading image from URL: " + path, e);
        }
    }

    private void setupControlPanel() {
        controlPanel = new JPanel();
        controlPanel.setBounds(0, Settings.GAME_HEIGHT, Settings.GAME_WIDTH, 80);
        controlPanel.setBackground(Settings.CONTROL_PANEL_COLOR);
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
        int[] delays = {200, 80, 40};
        String[] levelLabels = {"Lambat", "Sedang", "Cepat"};

        for (int i = 0; i < levelButtons.length; i++) {
            levelButtons[i] = new JButton(levelLabels[i]);
            levelButtons[i].setFocusable(false);
            final int selectedLevel = i + 1;
            final int delay = delays[i];
            levelButtons[i].addActionListener(e -> {
                level = selectedLevel;
                gameSpeed = delay;
                startGame(delay);
                gameStarted = true;
                gameOver = false;
                controlPanel.removeAll();
                controlPanel.revalidate();
                controlPanel.repaint();
                requestFocusInWindow();
            });
            controlPanel.add(levelButtons[i]);
        }
        controlPanel.revalidate();
        controlPanel.repaint();
        repaint();
    }

    public void startGame(int initialDelay) {
        // Pass tileSize to Snake and Food
        snake = new Snake(tileSize);
        food = new Food(tileSize);
        running = true;
        score = 0;
        timer = new Timer(initialDelay, this);
        timer.start();
        gameOver = false;
        collisionOccurred = false;
        collisionParticles.clear();
        if (collisionTimer != null) {
            collisionTimer.stop();
        }
        requestFocusInWindow();
    }

    public void restartGame() {
        startGame(gameSpeed);
        running = true;
        gameOver = false;
        controlPanel.removeAll();
        controlPanel.revalidate();
        controlPanel.repaint();
        requestFocusInWindow();
    }

    private int getLevelDelay(int level) {
        switch (level) {
            case 1:
                return 200;
            case 2:
                return 150;
            case 3:
                return 100;
            default:
                return 150;
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
        int tileWidth = tileSize;
        int tileHeight = tileSize;
        Color color1 = new Color(170, 215, 81);
        Color color2 = new Color(162, 209, 73);

        for (int row = 0; row < Settings.GAME_HEIGHT / tileHeight; row++) {
            for (int col = 0; col < Settings.GAME_WIDTH / tileWidth; col++) {
                g2d.setColor((row + col) % 2 == 0 ? color1 : color2);
                g2d.fillRect(col * tileWidth, row * tileHeight, tileWidth, tileHeight);
            }
        }

        if (running) {
            food.draw(g2d, 0, 0, foodImage);
            snake.draw(g2d, 0, 0, headUpImage, headDownImage, headLeftImage, headRightImage,
                    bodyHorizontalImage, bodyVerticalImage, bodyTopLeftImage, bodyTopRightImage,
                    bodyBottomLeftImage, bodyBottomRightImage, tailUpImage, tailDownImage,
                    tailLeftImage, tailRightImage);
            drawCollisionParticles(g2d);
        } else if (gameOver) {
            gameOverScreen(g2d);
        } else if (collisionOccurred) {
            drawCollisionParticles(g2d);
        }
        drawScore(g2d);
    }

    private void drawScore(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRoundRect(Settings.GAME_WIDTH - 160, 10, 150, 40, 20, 20);

        g2d.setColor(Color.RED);
        g2d.fillOval(Settings.GAME_WIDTH - 145, 20, 20, 20);

        g2d.setColor(Settings.SCORE_COLOR);
        g2d.setFont(Settings.SCORE_FONT);
        g2d.drawString("Score: " + score, Settings.GAME_WIDTH - 120, 35);
        g2d.drawString("Level: " + level, 10, Settings.GAME_HEIGHT + 25);
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
                gameSpeed = Math.max(Settings.MIN_DELAY, currentDelay - Settings.DELAY_DECREMENT);
                timer = new Timer(gameSpeed, this);
                timer.start();
            }
            newFood();
        }

        if (snake.checkSelfCollision()) {
            handleCollision();
        }
    }

    private void handleCollision() {
        if (!collisionOccurred) {
            collisionOccurred = true;
            running = false;
            timer.stop();
            createCollisionParticles(); // Generate particles
            collisionTimer = new Timer(collisionDelay, new ActionListener() { // Use the 2-second delay
                @Override
                public void actionPerformed(ActionEvent e) {
                    gameOver = true;
                    showGameOverButtons();
                    collisionTimer.stop();
                }
            });
            collisionTimer.setRepeats(false);
            collisionTimer.start();
        }
    }

    private void createCollisionParticles() {
        int headX = snake.getHeadX();
        int headY = snake.getHeadY();
        // Color particleColor = Settings.SNAKE_COLOR; // Use a base color, nanti dibuat variasinya
        for (int i = 0; i < 30; i++) { // Increased particle count for better effect
            collisionParticles.add(new CollisionParticle(headX, headY));
        }
    }

    private void drawCollisionParticles(Graphics2D g2d) {
        for (int i = 0; i < collisionParticles.size(); i++) {
            collisionParticles.get(i).draw(g2d);
        }
    }

    private void updateCollisionParticles() {
        for (int i = 0; i < collisionParticles.size(); i++) {
            collisionParticles.get(i).update();
            if (collisionParticles.get(i).isDead()) {
                collisionParticles.remove(i);
                i--;
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
        g2d.setColor(new Color(0, 0, 0, 200));
        g2d.fillRect(0, 0, Settings.GAME_WIDTH, Settings.GAME_HEIGHT);
        g2d.setColor(Color.RED);
        g2d.setFont(new Font("Arial", Font.BOLD, 40));
        FontMetrics metrics1 = getFontMetrics(g2d.getFont());
        g2d.drawString("Final Score: " + score,
                (Settings.GAME_WIDTH - metrics1.stringWidth("Final Score: " + score)) / 2,
                Settings.GAME_HEIGHT / 2 - 50);

        g2d.setFont(new Font("Arial", Font.BOLD, 75));
        FontMetrics metrics2 = getFontMetrics(g2d.getFont());
        g2d.drawString("Game Over", (Settings.GAME_WIDTH - metrics2.stringWidth("Game Over")) / 2, Settings.GAME_HEIGHT / 2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            snake.move();
            checkCollision();
        }
        if (collisionOccurred) {
            updateCollisionParticles();
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
                restartGame();
            }
        }
    }

    private class CollisionParticle {
        private int x;
        private int y;
        private int size;
        private Color color;
        private int speedX;
        private int speedY;
        private int alpha;
        private int lifespan;
        private int baseSize = 10;
        private int maxLifespan = 30;

        public CollisionParticle(int x, int y) {
            this.x = x;
            this.y = y;
            this.size = random.nextInt(10) + 5; // Variasi ukuran lebih besar
            this.speedX = random.nextInt(9) - 4; // Rentang kecepatan X: -4 hingga 4
            this.speedY = random.nextInt(9) - 4; // Rentang kecepatan Y: -4 hingga 4
            this.alpha = 255;
            this.lifespan = maxLifespan;
            this.baseSize = this.size; // Gunakan ukuran awal sebagai baseSize
        }
        public void update() {
            x += speedX;
            y += speedY;
            alpha -= 12; // Sedikit lebih cepat menghilang jika perlu
            lifespan--;
            size = (int) (baseSize * (lifespan / (float) maxLifespan)); // Ukuran mengecil
        }

        public void draw(Graphics2D g2d) {
            if (alpha > 0) {
                // Color gradient dari merah ke kuning
                float ratio = (float) lifespan / maxLifespan;
                int red = 255;
                int green = (int) (255 * ratio); // Hijau meningkat seiring lifespan
                int blue = 0;
                color = new Color(red, green, blue, alpha);
                g2d.setColor(color);
                g2d.fillOval(x, y, size, size);
            }
        }

        public boolean isDead() {
            return lifespan <= 0;
        }
    }
}
