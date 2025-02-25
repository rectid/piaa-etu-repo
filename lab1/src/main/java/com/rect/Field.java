package com.rect;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class Field {
    private static final Logger logger = LogManager.getLogger(Field.class);

    private final int length;
    private int filledArea;
    private final int width;
    private final boolean[][] occupied;
    private List<Square> bestSolution = new ArrayList<>();
    private int minSquares = Integer.MAX_VALUE;

    public Field(int length, int width) {
        this.length = length;
        this.width = width;
        this.occupied = new boolean[length][width];
        this.filledArea = 0;
    }

    public void solve() {
        logger.info("Начинаем поиск минимального количества квадратов...");
        backtrack(new ArrayList<>(), 0);
        logger.info("Минимальное количество квадратов: {}", minSquares);
        logger.info("Лучшее решение:");
        for (Square s : bestSolution) {
            logger.info(s.toString());
        }
    }

    private void backtrack(List<Square> placed, int count) {
        if (count >= minSquares) {
            logger.debug("Текущий путь не оптимален, возвращаемся.");
            return;
        }

        int[] pos = findFirstEmpty();
        if (pos == null) {
            if (count < minSquares) {
                logger.info("Найдено новое лучшее решение с {} квадратами.", count);
                minSquares = count;
                bestSolution = new ArrayList<>(placed);
            }
            return;
        }

        int x = pos[0], y = pos[1];
        int maxSize = Math.min(length - x, width - y);
        maxSize = Math.min(maxSize, Math.min(length, width) - 1);
        int remainingArea = length * width - filledArea;

        int maxPossibleSize = maxSize;
        int minRemaining = (int) Math.ceil((double) remainingArea / (maxPossibleSize * maxPossibleSize));
        if (count + minRemaining >= minSquares) {
            return;
        }

        logger.debug("Попытка разместить квадраты в позиции ({}, {})...", x + 1, y + 1);

        for (int size = maxSize; size >= 1; size--) {
            if (canPlace(x, y, size)) {
                logger.debug("Размещаем квадрат размером {}x{} в позиции ({}, {})", size, size, x + 1, y + 1);
                place(x, y, size, true);
                placed.add(new Square(x + 1, y + 1, size));
                backtrack(placed, count + 1);
                placed.remove(placed.size() - 1);
                place(x, y, size, false);
                logger.debug("Убираем квадрат размером {}x{} из позиции ({}, {})", size, size, x + 1, y + 1);
            } else {
                logger.debug("Квадрат размером {}x{} нельзя разместить в позиции ({}, {})", size, size, x + 1, y + 1);
            }
        }
    }

    private int[] findFirstEmpty() {
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < width; j++) {
                if (!occupied[i][j]) return new int[]{i, j};
            }
        }
        return null;
    }

    private boolean canPlace(int x, int y, int size) {
        if (x + size > length || y + size > width) return false;
        for (int dx = 0; dx < size; dx++) {
            for (int dy = 0; dy < size; dy++) {
                if (occupied[x + dx][y + dy]) return false;
            }
        }
        return true;
    }

    private void place(int x, int y, int size, boolean state) {
        for (int dx = 0; dx < size; dx++) {
            for (int dy = 0; dy < size; dy++) {
                occupied[x + dx][y + dy] = state;
            }
        }
        filledArea += (state ? size * size : -size * size);
    }
}