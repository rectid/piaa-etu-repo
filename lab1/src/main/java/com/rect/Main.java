package com.rect;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        logger.info("Введите длину и ширину поля через пробел:");
        String[] input = sc.nextLine().split(" ");
        int length = Integer.parseInt(input[0]);
        int width = (input.length > 1) ? Integer.parseInt(input[1]) : length;
        logger.info("Создано поле размером {}x{}", length, width);

        Field field = new Field(length, width);
        long startTime = System.currentTimeMillis();
        field.solve();
        long endTime = System.currentTimeMillis();
        logger.info("Время выполнения: {} ms", endTime - startTime);
    }
}