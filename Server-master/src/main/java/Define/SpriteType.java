package Define;

import java.util.Random;

public enum SpriteType {
    BRICK_WALL,
    FOOD_BLACK,
    FOOD_BLUE,
    FOOD_GREEN,
    FOOD_PINK,
    FOOD_RED,
    FOOD_YELLOW,

    SNAKE_HEAD,
    SNAKE_DOT_BLACK,
    SNAKE_DOT_RED,
    SNAKE_DOT_YELLOW,
    SNAKE_DOT_GREEN,
    SNAKE_DOT_PINK,
    SNAKE_DOT_BLUE;

    public static SpriteType getFoodSprite(){
        Random random = new Random();
        return SpriteType.values()[random.nextInt(6) + 1];
    }
}
