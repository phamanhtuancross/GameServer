package Model;

import Define.SpriteType;

import java.io.Serializable;

public class Dot implements Serializable {
    public int x;
    public int y;
    public SpriteType spriteType;

    public Dot(){
        this.x = this.y = 0;
        this.spriteType = SpriteType.FOOD_BLACK;
    }

    public Dot(int x, int y, SpriteType spriteType) {
        this.x = x;
        this.y = y;
        this.spriteType = spriteType;
    }
}
