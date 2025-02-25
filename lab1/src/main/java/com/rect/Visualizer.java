package com.rect;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Random;

public class Visualizer extends JFrame {
    private final JTextField inputField;
    private final JPanel drawPanel;
    private final JLabel resultLabel;
    private Field field;

    public Visualizer() {
        setTitle("Backtracking Visualization");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel();
        inputField = new JTextField(5);
        JButton startButton = new JButton("Запустить");
        resultLabel = new JLabel("Результат: ");

        controlPanel.add(new JLabel("Размер поля: "));
        controlPanel.add(inputField);
        controlPanel.add(startButton);
        controlPanel.add(resultLabel);

        drawPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawSolution(g);
            }
        };
        drawPanel.setPreferredSize(new Dimension(600, 600));

        add(controlPanel, BorderLayout.NORTH);
        add(drawPanel, BorderLayout.CENTER);

        startButton.addActionListener(e -> startAlgorithm());
    }

    private void startAlgorithm() {
        try {
            String[] input = inputField.getText().split(" ");
            int length = Integer.parseInt(input[0]);
            int width = (input.length > 1) ? Integer.parseInt(input[1]) : length;
            field = new Field(length, width);
            field.solve();
            resultLabel.setText("Минимальное число квадратов: " + field.getMinSquares());
            drawPanel.repaint();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Введите корректное число", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void drawSolution(Graphics g) {
        if (field == null) return;
        List<Square> solution = field.getBestSolution();
        int gridSize = field.getLength();
        int cellSize = 500 / gridSize;
        Random rand = new Random();

        for (Square square : solution) {
            int x = (square.getX() - 1) * cellSize;
            int y = (square.getY() - 1) * cellSize;
            int size = square.getLength() * cellSize;
            Color color;

            do {
                color = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
            } while (!isValidColor(color, g, size));

            g.setColor(color);
            g.fillRect(x+50, y+10, size, size);
            g.setColor(Color.BLACK);
            g.drawRect(x+50, y+10, size, size);
        }
    }

    private boolean isValidColor(Color color, Graphics g, int size) {
        for (int dx = -1; dx <= size; dx++) {
            for (int dy = -1; dy <= size; dy++) {
                if (g instanceof Graphics2D) {
                    Graphics2D g2d = (Graphics2D) g;
                    if (g2d.getPaint() == color) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Visualizer().setVisible(true));
    }
}
