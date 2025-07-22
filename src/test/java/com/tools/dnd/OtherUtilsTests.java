package com.tools.dnd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.opencsv.exceptions.CsvException;
import com.tools.dnd.creatures.Monster;
import com.tools.dnd.util.CsvUtils;
import com.tools.dnd.util.DndUtils;
import com.tools.dnd.util.Enums.DamageResponse;
import com.tools.dnd.util.Enums.DamageType;

public class OtherUtilsTests {

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

    @Test
    public void filterLinesByColTest() throws IOException, CsvException {
        String[] names = {"Akamu", "Helios", "Riley", "Xena"};

        List<List<String>> results = CsvUtils.readLinesMatchingCol("party_list.csv", 0, "Adeo");
        assertEquals(4, results.size());
        for (int i = 0; i < names.length; i++) {
            List<String> csvRow = results.get(i);
            assertEquals(3, csvRow.size());
            assertEquals(names[i],csvRow.get(1));
        }
    }

    @Test
    public void excludeLinesByColTest() throws IOException, CsvException {
        String[] row1 = {"foo","bar","baz"};
        String[] row2 = {"goo","bar","baz"};
        String[] row3 = {"hoo","bar","baz"};
        List<List<String>> allRows = new ArrayList<>();
        allRows.add(Arrays.asList(row1));
        allRows.add(Arrays.asList(row2));
        allRows.add(Arrays.asList(row3));



        List<List<String>> filtered = CsvUtils.excludeLinesMatchingCol(allRows, 0, new String[] {"foo"});
        for (List<String> list : filtered) {
            assertFalse(list.contains("foo"));
            assertTrue(list.contains("bar"));
            assertTrue(list.contains("baz"));
            assertTrue(list.contains("goo") || list.contains("hoo"));
            
        }
    }

    @Test
    public void damageParser() {
        Map<DamageResponse, DamageType[]> damageResponses = new HashMap<>();
        damageResponses.put(DamageResponse.VULNERABLE, new DamageType[] {DamageType.FIRE});
        damageResponses.put(DamageResponse.RESISTANT, new DamageType[] {DamageType.COLD, DamageType.PSYCHIC});
        damageResponses.put(DamageResponse.IMMUNE, new DamageType[] {DamageType.BLUEBERRY});

        Monster monster = new Monster("Dire Test", "Dire Test", 0, 10, 100, 
            0, damageResponses, null, null, null, null, 0, 0);
        
        int expectedHp = 100;

        monster.changeHp("5");
        expectedHp -= 5;
        assertEquals(expectedHp, monster.getCurrentHp());

        monster.changeHp("5, 10");
        expectedHp -= 15;
        assertEquals(expectedHp, monster.getCurrentHp());
        
        monster.changeHp("5 acid");
        expectedHp -= 5;
        assertEquals(expectedHp, monster.getCurrentHp());

        monster.changeHp("5 acid, 5 poison");
        expectedHp -= 10;
        assertEquals(expectedHp, monster.getCurrentHp());

        monster.changeHp("5 fire");
        expectedHp -= 10;
        assertEquals(expectedHp, monster.getCurrentHp());

        monster.changeHp("10 cold");
        expectedHp -= 5;
        assertEquals(expectedHp, monster.getCurrentHp());

        monster.changeHp("6 cold, 7 psychic");
        expectedHp -= 6;
        assertEquals(expectedHp, monster.getCurrentHp());

        monster.changeHp("6 blueberry");
        assertEquals(expectedHp, monster.getCurrentHp());

        monster.changeHp("10 fire, 3 cold, 2 psychic, 30 blueberry, 3 acid");
        expectedHp -= (20 + 1 + 1 + 0 + 3);
        assertEquals(expectedHp, monster.getCurrentHp());
    }

    

}
