package com.tools.dnd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.tools.dnd.util.DndUtils;

public class DndUtilsTest {

    @Test
    public void scoresToModifiers() {
        int[] scores = new int[30];
        for (int i = 0; i < 30; i++) {
            scores[i] = i + 1;
        }

        int[] modifiers = {-5, -4, -4, -3, -3, -2, -2, -1, -1,  0,
                            0,  1,  1,  2,  2,  3,  3,  4,  4,  5,
                            5,  6,  6,  7,  7,  8,  8,  9,  9, 10};
        
        for (int i = 0; i < scores.length; i++) {
            int score = scores[i];
            int expectedModifier = modifiers[i];
            assertEquals(expectedModifier, DndUtils.scoreToModifier(score));
        }
    }

    @Test
    public void diceRolling() {
        List<Integer> d8results = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            d8results.add(DndUtils.rollDice(1, 8));
        }
        assertTrue(d8results.contains(1));
        assertTrue(d8results.contains(2));
        assertTrue(d8results.contains(3));
        assertTrue(d8results.contains(4));
        assertTrue(d8results.contains(5));
        assertTrue(d8results.contains(6));
        assertTrue(d8results.contains(7));
        assertTrue(d8results.contains(8));
    }

}
