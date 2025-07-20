package com.tools.dnd.util;

import java.util.Random;

import com.tools.dnd.creatures.Monster;
import com.tools.dnd.creatures.Enums;
import com.tools.dnd.creatures.Enums.DamageType;

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

    public static int parseDamage(String damageStr, Monster target) throws NumberFormatException {
        try {
            return Integer.parseInt(damageStr);
        } catch (NumberFormatException e) {
            int total = 0;
            for (String str : damageStr.split(",")) {
                String[] dmg = str.trim().split(" ");
                if (dmg.length > 1) { // if damage is typed
                    int amount = Integer.parseInt(dmg[0].trim());
                    DamageType type = Enums.evaluateType(dmg[1].trim());
                    switch (target.getResponseTo(type)) {
                        case VULNERABLE:
                            amount *= 2;
                            break;
                        case RESISTANT:
                            amount /= 2;
                            break;                        
                        case IMMUNE:
                            amount = 0;
                            break;
                        default: // no special reaction
                            break;
                    }
                    total += amount;
                } else {
                    total += Integer.parseInt(str.trim());
                }
            }
            return total;
        }
    }

}
 