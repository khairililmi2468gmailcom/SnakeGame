import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class Snake {
    private int[] x = new int[Settings.GAME_UNITS];
    private int[] y = new int[Settings.GAME_UNITS];
    private int bodyParts = Settings.INITIAL_SNAKE_LENGTH;
    private char direction = 'R';
    private boolean moving = false;

    public Snake() {
        for (int i = 0; i < bodyParts; i++) {
            x[i] = Settings.GAME_WIDTH / 2 - i * Settings.TILE_SIZE;
            y[i] = Settings.GAME_HEIGHT / 2;
        }
        moving = true;
    }

    public void move() {
        if (moving) {
            for (int i = bodyParts; i > 0; i--) {
                x[i] = x[i - 1];
                y[i] = y[i - 1];
            }

            switch (direction) {
                case 'U':
                    y[0] = y[0] - Settings.TILE_SIZE;
                    if (y[0] < 0) {
                        y[0] = Settings.GAME_HEIGHT - Settings.TILE_SIZE;
                    }
                    break;
                case 'D':
                    y[0] = y[0] + Settings.TILE_SIZE;
                    if (y[0] >= Settings.GAME_HEIGHT) {
                        y[0] = 0;
                    }
                    break;
                case 'L':
                    x[0] = x[0] - Settings.TILE_SIZE;
                    if (x[0] < 0) {
                        x[0] = Settings.GAME_WIDTH - Settings.TILE_SIZE;
                    }
                    break;
                case 'R':
                    x[0] = x[0] + Settings.TILE_SIZE;
                    if (x[0] >= Settings.GAME_WIDTH) {
                        x[0] = 0;
                    }
                    break;
            }
        }
    }

    public void draw(Graphics2D g2d, int offsetX, int offsetY) {
        for (int i = 0; i < bodyParts; i++) {
            g2d.setColor(Settings.SNAKE_COLOR);
            if (i == 0) { // Kepala
                g2d.fill(new RoundRectangle2D.Double(x[i] + offsetX, y[i] + offsetY,
                        Settings.TILE_SIZE, Settings.TILE_SIZE, 8, 8));
            } else { // Badan
                g2d.fillRect(x[i] + offsetX, y[i] + offsetY, Settings.TILE_SIZE, Settings.TILE_SIZE);
            }
            // Border tipis
            g2d.setColor(Settings.BACKGROUND_COLOR.darker());
            g2d.drawRect(x[i] + offsetX, y[i] + offsetY, Settings.TILE_SIZE, Settings.TILE_SIZE);
        }
    }

    public void setDirection(char direction) {
        this.direction = direction;
    }

    public char getDirection() {
        return direction;
    }

    public int getHeadX() {
        return x[0];
    }

    public int getHeadY() {
        return y[0];
    }

    public int getBodyParts() {
        return bodyParts;
    }

    public int[] getX() {
        return x;
    }

    public int[] getY() {
        return y;
    }

    public void increaseLength() {
        bodyParts++;
    }

    public boolean isMoving() {
        return moving;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }
}