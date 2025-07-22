package com.tools.dnd.util;

public class Enums {
    public static enum DamageResponse {
        VULNERABLE,
        RESISTANT,
        IMMUNE,
        DEFAULT
    }

    public static enum DamageType {
        ACID,
        BLUDGEONING,
        BLUEBERRY, // SOURCE - Extra Life Misplaced Monsters: Volume One
        COLD,
        CUSTARD, // SOURCE - The Wild Beyond the Witchlight
        FIRE,
        FORCE,
        NECROTIC,
        PIERCING,
        POISON,
        PSYCHIC,
        LIGHTNING,
        RADIANT,
        SLASHING,
        THUNDER,
        DEFAULT
    }

    /**
     * A version of Enum.valueOf() that can handle whitespace and inconsistent capitalization, 
     * and will return the DEFAULT keyword if it exists and the given string isn't found
     * @param <E> The Enum to evaluate
     * @param enumClass A class object representing the Enum
     * @param enumValue The value to evaluate
     * @return The enumValue if it exists, or DEFAULT if not, or null if neither can be found
     */
    public static <E extends Enum<E>> E evaluateType(Class<E> enumClass, String enumValue) {
        // Nested try-catch is ugly, but I don't see a good alternative
        // for this particular method, so it stays for now
        try {
            return Enum.valueOf(enumClass, enumValue.strip().toUpperCase());
        } catch (IllegalArgumentException e1) {
            try {
                return Enum.valueOf(enumClass, "DEFAULT");
            } catch (IllegalArgumentException e2) {
                return null;
            }
        }
    }
    
}
