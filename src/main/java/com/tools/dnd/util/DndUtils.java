package com.tools.dnd.util;

import java.util.Random;

public class DndUtils {
    public static int scoreToModifier(int score) {
        return Math.floorDiv((score - 10), 2);
    }

    public static int rollDice(int numberOfDice, int numberOfFaces) {
        int total = 0;
        Random random = new Random();
        for (int i = 0; i < numberOfDice; i++) {
            total += random.nextInt(1, numberOfFaces + 1);
        }
        return total;
    }

}
 