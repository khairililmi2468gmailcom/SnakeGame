import java.awt.*;

public class Settings {
    public static final int TILE_SIZE = 25;
    public static final int GAME_WIDTH = 600;
    public static final int GAME_HEIGHT = 600;
    public static final int UNIT_SIZE = TILE_SIZE * TILE_SIZE;
    public static final int GAME_UNITS = (GAME_WIDTH * GAME_HEIGHT) / UNIT_SIZE;
    public static final int INITIAL_DELAY = 150;
    public static final int DELAY_DECREMENT = 10; // Pengurangan delay saat level naik
    public static final int MIN_DELAY = 50; // Delay minimum
    public static final String GAME_TITLE = "Snake Game";
    public static final int INITIAL_SNAKE_LENGTH = 3;
    public static final int LEVEL_UP_SCORE = 5; // Setiap berapa skor level naik

    // Warna yang lebih menarik
    public static final Color BACKGROUND_COLOR = new Color(30, 30, 30); // Abu-abu gelap
    public static final Color SNAKE_COLOR = new Color(100, 255, 100); // Hijau cerah
    public static final Color FOOD_COLOR = new Color(255, 100, 100); // Merah cerah
    public static final Color SCORE_COLOR = Color.WHITE;
    public static final Font SCORE_FONT = new Font("SansSerif", Font.BOLD, 24);
}