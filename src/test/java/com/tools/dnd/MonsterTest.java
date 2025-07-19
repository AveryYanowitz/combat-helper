package com.tools.dnd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.tools.dnd.creatures.Monster;

public class MonsterTest {
    private Monster monster;

    @BeforeEach
    public void setup() {
        monster = new Monster("name", "name", 0, 10, 25, 
        5, null, null, null, null, null, 0, 0);
    }

    @Test
    public void monsterHpLogic() {
        // Test that temp HP works
        assertEquals(25, monster.getCurrentHp());
        monster.setTempHp(5);
        monster.changeHp(5);
        assertEquals(25, monster.getCurrentHp());
        assertEquals(0, monster.getTempHp());

        // Test that normal damage works
        monster.changeHp(-5);
        assertEquals(20, monster.getCurrentHp());
        assertEquals(25, monster.getMAX_HP());
        
        // Healing
        monster.changeHp(5);
        assertEquals(25, monster.getCurrentHp());
        assertEquals(25, monster.getMAX_HP());

        // Over-healing
        monster.changeHp(15);
        assertEquals(25, monster.getCurrentHp());
        assertEquals(25, monster.getMAX_HP());
    }
}
