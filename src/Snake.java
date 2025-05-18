
import java.awt.*;

import java.awt.image.BufferedImage;

import java.io.File;

import java.io.IOException;

import java.io.InputStream;

import java.util.Objects;

import java.util.Random;

import javax.imageio.ImageIO;

import java.net.URL;



public class Snake {



    private int[] x;

    private int[] y;

    private int bodyParts;

    private char direction;

    private boolean moving;

    private int lastX;

    private int lastY;

    private int tileSize; // Use this instead of Settings.TILE_SIZE



    public Snake(int tileSize) {

        this.tileSize = tileSize;

        this.x = new int[Settings.GAME_UNITS];

        this.y = new int[Settings.GAME_UNITS];

        this.bodyParts = Settings.INITIAL_SNAKE_LENGTH;

        this.direction = 'R';

        this.moving = true;

        this.lastX = 0;

        this.lastY = 0;

        initSnake();

    }



    private void initSnake() {

        for (int i = 0; i < bodyParts; i++) {

            x[i] = Settings.GAME_WIDTH / 2 - i * tileSize;

            y[i] = Settings.GAME_HEIGHT / 2;

        }

    }



    public void move() {

        if (moving) {

            lastX = x[0];

            lastY = y[0];

            for (int i = bodyParts; i > 0; i--) {

                x[i] = x[i - 1];

                y[i] = y[i - 1];

            }



            switch (direction) {

                case 'U':

                    y[0] -= tileSize;

                    if (y[0] < 0) {

                        y[0] = Settings.GAME_HEIGHT - tileSize;

                    }

                    break;

                case 'D':

                    y[0] += tileSize;

                    if (y[0] >= Settings.GAME_HEIGHT) {

                        y[0] = 0;

                    }

                    break;

                case 'L':

                    x[0] -= tileSize;

                    if (x[0] < 0) {

                        x[0] = Settings.GAME_WIDTH - tileSize;

                    }

                    break;

                case 'R':

                    x[0] += tileSize;

                    if (x[0] >= Settings.GAME_WIDTH) {

                        x[0] = 0;

                    }

                    break;

            }

        }

    }



    public void draw(Graphics2D g2d, int offsetX, int offsetY,

                     BufferedImage headUp, BufferedImage headDown, BufferedImage headLeft, BufferedImage headRight,

                     BufferedImage bodyHorizontal, BufferedImage bodyVertical, BufferedImage bodyTopLeft,

                     BufferedImage bodyTopRight, BufferedImage bodyBottomLeft, BufferedImage bodyBottomRight,

                     BufferedImage tailUp, BufferedImage tailDown, BufferedImage tailLeft, BufferedImage tailRight) {

        for (int i = 0; i < bodyParts; i++) {

            BufferedImage imageToDraw = null;

            if (i == 0) {

                switch (direction) {

                    case 'U':

                        imageToDraw = headUp;

                        break;

                    case 'D':

                        imageToDraw = headDown;

                        break;

                    case 'L':

                        imageToDraw = headLeft;

                        break;

                    case 'R':

                        imageToDraw = headRight;

                        break;

                }

            } else if (i == bodyParts - 1) {

                int prevX = x[i - 1];

                int prevY = y[i - 1];

                if (x[i] == prevX) {

                    if (y[i] < prevY) {

                        imageToDraw = tailUp;

                    } else {

                        imageToDraw = tailDown;

                    }

                } else if (y[i] == prevY) {

                    if (x[i] < prevX) {

                        imageToDraw = tailLeft;

                    } else {

                        imageToDraw = tailRight;

                    }

                }

            } else {

                int prevX = x[i - 1];

                int prevY = y[i - 1];

                int nextX = x[i + 1];

                int nextY = y[i + 1];



                if (x[i] == prevX && x[i] == nextX) {

                    imageToDraw = bodyVertical;

                } else if (y[i] == prevY && y[i] == nextY) {

                    imageToDraw = bodyHorizontal;

                } else if ((x[i] == prevX && y[i] == nextY) || (x[i] == nextX && y[i] == prevY)) {

                    if ((prevX < x[i] && nextY < y[i]) || (nextX < x[i] && prevY < y[i])) {

                        imageToDraw = bodyTopLeft;

                    } else if ((prevX < x[i] && nextY > y[i]) || (nextX < x[i] && prevY > y[i])) {

                        imageToDraw = bodyBottomLeft;

                    } else if ((prevX > x[i] && nextY < y[i]) || (nextX > x[i] && prevY < y[i])) {

                        imageToDraw = bodyTopRight;

                    } else {

                        imageToDraw = bodyBottomRight;

                    }

                } else if ((y[i] == prevY && x[i] == nextX) || (y[i] == nextY && x[i] == prevX)) {

                    if ((prevY < y[i] && nextX > x[i]) || (nextY < y[i] && prevX > x[i])) {

                        imageToDraw = bodyTopLeft;

                    } else if ((prevY < y[i] && nextX < x[i]) || (nextY < y[i] && prevX < x[i])) {

                        imageToDraw = bodyTopRight;

                    } else if ((prevY > y[i] && nextX > x[i]) || (nextY > y[i] && prevX > x[i])) {

                        imageToDraw = bodyBottomLeft;

                    } else {

                        imageToDraw = bodyBottomRight;

                    }

                } else {

                    imageToDraw = bodyHorizontal;

                }

            }



            if (imageToDraw != null) {

                g2d.drawImage(imageToDraw, x[i] + offsetX, y[i] + offsetY, tileSize, tileSize, null);

            }

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



    public int getLastX() {

        return lastX;

    }



    public int getLastY() {

        return lastY;

    }

    public boolean checkSelfCollision() {
        for (int i = 1; i < bodyParts; i++) {
            if (x[0] == x[i] && y[0] == y[i]) {
                return true;
            }
        }
        return false;
    }

}
