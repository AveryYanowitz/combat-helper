package com.tools.dnd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import com.tools.dnd.core.InitList;
import com.tools.dnd.creatures.Creature;
import com.tools.dnd.creatures.Monster;
import com.tools.dnd.creatures.Player;
import com.tools.dnd.user_input.InputHandler;

public class InitListTest {
    static int numTurnsTaken;

    static class TestCreature extends Creature {
        TestCreature(String name, int dex, int init) {
            super(name, dex, init);
        }

        @Override
        public Map<String, String> takeTurn(InputHandler input) {
            numTurnsTaken++;
            return null;
        }
    }

    @Test
    public void toStringTest() {
        List<Creature> plyrs = List.of(new Player("c1", 10, 8));
        Creature c2 = new TestCreature("c2", 10, 9);
        Creature c3 = new TestCreature("c3", 10, 10);

        String expectedString = """
                Copy-Pastable Initiative:

                **c3**: 10
                **c2**: 9
                **c1**: 8
                """;
        
        InitList initList = new InitList(plyrs, List.of(c2, c3));
        assertEquals(expectedString, initList.toString());
    }

    @Test
    public void rightOrderingTest() {
        Creature c1 = new TestCreature("c1", 0, 30);
        Creature c2 = new TestCreature("c2", 20, 20);
        Creature c3 = new TestCreature("c3", 10, 10);
        Creature c4 = new TestCreature("c4", 10, 9);
        Creature c5 = new TestCreature("c5", 30, 1);
        Creature c6 = new TestCreature("c6", 1, 1);

        InitList initList = new InitList(List.of(c1, c2, c3, c4, c5, c6));
        for (int i = 1; i <= 6; i++) {
            String name = "c" + i;
            assertEquals(name, initList.nextTurn().getNAME());
        }
    }

    @Test
    public void partyWinsTest() throws Exception {
        String[] monsterNames = {"Commoner", "Mage"};
        List<Monster> monList = TestingTools.getMonstersNoAliases(monsterNames);
        List<Player> playerList = List.of(new Player("p1", 3, 3));
        InitList init = new InitList(monList, playerList);
        assertFalse(init.isCombatDone());

        monList.get(0).changeHp(-1000);
        assertFalse(init.isCombatDone());

        monList.get(1).changeHp(-1000);
        assertTrue(init.isCombatDone());
        assertEquals("The party won!", init.getOutcome());
    }

    @Test
    public void partyLosesTest() throws Exception {
        String[] monsterNames = {"Commoner", "Mage"};
        List<Monster> monList = TestingTools.getMonstersNoAliases(monsterNames);
        List<Player> playerList = List.of(new Player("p1", 3, 3), new Player("p2", 3, 3));
        InitList init = new InitList(monList, playerList);
        assertFalse(init.isCombatDone());

        playerList.get(0).setDead(true);
        assertFalse(init.isCombatDone());

        playerList.get(1).setDead(true);
        assertTrue(init.isCombatDone());
        assertEquals("The party lost!", init.getOutcome());
    }

}
