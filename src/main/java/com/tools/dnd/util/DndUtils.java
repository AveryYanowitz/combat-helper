package com.tools.dnd.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.tools.dnd.util.Enums.DamageType;

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

    public static Map<DamageType, Integer> parseDamage(String damageStr) throws NumberFormatException {
        try {
            return Map.of(DamageType.DEFAULT, Integer.parseInt(damageStr));
        } catch (NumberFormatException e) {
            Map<DamageType, Integer> map = new HashMap<>();
            for (String str : damageStr.split(",")) {
                String[] dmg = str.strip().split(" ");
                if (dmg.length > 1) { // if damage is typed
                    int amount = Integer.parseInt(dmg[0].strip());
                    DamageType type = Enums.evaluateType(DamageType.class, dmg[1].strip());
                    _putOrAddTo(map, type, amount);
                } else {
                    _putOrAddTo(map, DamageType.DEFAULT, Integer.parseInt(str.strip()));
                }
            }
            return map;
        }
    }

    private static <K> void _putOrAddTo(Map<K, Integer> mapToAdd, K key, int value) {
        if (mapToAdd.containsKey(key)) {
            int oldVal = mapToAdd.get(key);
            mapToAdd.put(key, oldVal + value);
        } else {
            mapToAdd.put(key, value);
        }
    }

}
 