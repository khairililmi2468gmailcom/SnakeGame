
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Random;
import javax.imageio.ImageIO;
import java.net.URL;

public class Food {

    private int x;
    private int y;
    private Random random;
    private BufferedImage foodImage;
    private int tileSize; // Use this instead of Settings.TILE_SIZE

    public Food(int tileSize) {
        this.tileSize = tileSize;
        random = new Random();
        try {
            foodImage = loadImage("apple.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
        newFood(0, 0, Settings.GAME_WIDTH, Settings.GAME_HEIGHT);
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

    public void newFood(int gameX, int gameY, int gameWidth, int gameHeight) {
        x = random.nextInt(gameWidth / tileSize) * tileSize;
        y = random.nextInt(gameHeight / tileSize) * tileSize;
    }

    public void draw(Graphics g, int offsetX, int offsetY, BufferedImage image) {
        if (image != null) {
            g.drawImage(image, x + offsetX, y + offsetY, tileSize, tileSize, null);
        } else {
            g.setColor(Settings.FOOD_COLOR);
            g.fillOval(x + offsetX, y + offsetY, tileSize, tileSize);
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
