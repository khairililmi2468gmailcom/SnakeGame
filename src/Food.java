import java.awt.*;
import java.util.Random;

public class Food {

    private int x;
    private int y;
    private Random random;

    public Food() {
        random = new Random();
        newFood(0, 0, Settings.GAME_WIDTH, Settings.GAME_HEIGHT);
    }

    public void newFood(int gameX, int gameY, int gameWidth, int gameHeight) {
        x = random.nextInt(gameWidth / Settings.TILE_SIZE) * Settings.TILE_SIZE;
        y = random.nextInt(gameHeight / Settings.TILE_SIZE) * Settings.TILE_SIZE;
    }

    public void draw(Graphics g, int offsetX, int offsetY) {
        g.setColor(Settings.FOOD_COLOR);
        g.fillOval(x + offsetX, y + offsetY, Settings.TILE_SIZE, Settings.TILE_SIZE);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}