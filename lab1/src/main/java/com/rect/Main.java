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
        private List<Square> bestSolution = new ArrayList<>();
        private int minSquares = Integer.MAX_VALUE;

        public Field(int length, int width) {
            this.length = length;
            this.width = width;
            this.occupied = new boolean[length][width];
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
                minSquares = count;
                bestSolution = new ArrayList<>(placed);
                return;
            }

            int x = pos[0], y = pos[1];
            int minSide = Math.min(length, width);
            for (int size = minSide - 1; size >= 1; size--) {
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
            for (int i = x; i < x + size; i++) {
                for (int j = y; j < y + size; j++) {
                    if (occupied[i][j]) return false;
                }
            }
            return true;
        }

        private void place(int x, int y, int size, boolean state) {
            for (int i = x; i < x + size; i++) {
                for (int j = y; j < y + size; j++) {
                    occupied[i][j] = state;
                }
            }
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
