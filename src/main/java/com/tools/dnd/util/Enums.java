package com.tools.dnd.util;

public class Enums {
    public static enum DamageResponse {
        VULNERABLE,
        RESISTANT,
        IMMUNE,
        NORMAL
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
        UNTYPED
    }

    public static DamageType evaluateType(String damageStr) {
        try {
            return DamageType.valueOf(damageStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return DamageType.UNTYPED;
        }
    }
    
}
