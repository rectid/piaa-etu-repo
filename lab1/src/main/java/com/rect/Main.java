package com.rect;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String[] input = sc.nextLine().split(" ");
        int length = Integer.parseInt(input[0]);
        int width = (input.length > 1) ? Integer.parseInt(input[1]) : length;
        Field field = new Field(length, width);
        field.solve();
    }


    static class Field {
        private final int length;
        private final int width;
        private final boolean[][] occupied;
        private int filledArea;
        private List<Square> bestSolution = new ArrayList<>();
        private int minSquares = Integer.MAX_VALUE;

        public Field(int length, int width) {
            this.length = length;
            this.width = width;
            this.occupied = new boolean[length][width];
            this.filledArea = 0;
        }

        public void solve() {
            backtrack(new ArrayList<>(), 0);
            System.out.println(minSquares);
            for (Square s : bestSolution) {
                System.out.println(s.x + " " + s.y + " " + s.length);
            }
        }

        private void backtrack(List<Square> placed, int count) {
            if (count >= minSquares) return;

            int[] pos = findFirstEmpty();
            if (pos == null) {
                if (count < minSquares) {
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

            for (int size = maxSize; size >= 1; size--) {
                if (canPlace(x, y, size)) {
                    place(x, y, size, true);
                    placed.add(new Square(x + 1, y + 1, size));
                    backtrack(placed, count + 1);
                    placed.remove(placed.size() - 1);
                    place(x, y, size, false);
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

    static class Square {
        int x, y, length;

        public Square(int x, int y, int length) {
            this.x = x;
            this.y = y;
            this.length = length;
        }
    }
}