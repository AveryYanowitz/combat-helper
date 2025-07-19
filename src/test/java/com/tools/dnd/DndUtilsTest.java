package com.tools.dnd;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

}
