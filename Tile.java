package com.javarush.task.task35.task3513;

import java.awt.*;

public class Tile {
    int value;

    public Tile() {
        value = 0;
    }

    public Tile(int value) {
        this.value = value;
    }

    public boolean isEmpty(){
        return value == 0;
    }

    public Color getFontColor(){
        if(value < 16){
            return new Color(119, 110, 101);
        }else {
            return new Color(249, 246, 242);
        }
    }

    public Color getTileColor(){
        Color color;
        switch (value){
            case 0: color = new Color(205, 193, 180);
            break;
            case 2: color = new Color(238, 228, 218);
            break;
            case 4: color = new Color(237, 224, 200);
            break;
            case 8: color = new Color(242, 177, 121);
            break;
            case 16: color = new Color(245, 149, 99);
            break;
            case 32: color = new Color(246, 124, 95);
            break;
            case 64: color = new Color(246, 94, 59);
            break;
            case 128: color = new Color(237, 207, 114);
            break;
            case 256: color = new Color(237, 204, 97);
            break;
            case 512: color = new Color(237, 200, 80);
            break;
            case 1024: color = new Color(237, 197, 63);
            break;
            case 2048: color = new Color(237, 194, 46);
            break;
            default: color = new Color(255, 0, 0);
            break;
        }
        return color;
    }
}
