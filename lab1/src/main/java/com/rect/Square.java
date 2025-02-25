package com.rect;

public class Square {
    private final int x;
    private final int y;
    private final int length;

    public Square(int x, int y, int length) {
        this.x = x;
        this.y = y;
        this.length = length;
    }

    @Override
    public String toString() {
        return "Квадрат: (" + x + ", " + y + "), Размер: " + length + "x" + length;
    }
}