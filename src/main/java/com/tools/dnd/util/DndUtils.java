package com.tools.dnd.util;

public class DndUtils {
    public static int scoreToModifier(int score) {
        return Math.floorDiv((score - 10), 2);
    }

}
