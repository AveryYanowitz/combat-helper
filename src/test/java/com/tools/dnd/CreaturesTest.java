package com.tools.dnd;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.github.stefanbirkner.systemlambda.SystemLambda;
import com.opencsv.exceptions.CsvException;
import com.tools.dnd.creatures.CreatureFactory;
import com.tools.dnd.creatures.Monster;
import com.tools.dnd.creatures.Player;
import com.tools.dnd.util.Enums.DamageType;

public class CreaturesTest {
    private static final InputStream stdIn = System.in;

    @BeforeEach
    public void setup() {
        System.setIn(stdIn);
    }

    @Test
    public void monsterHpLogic() {
        Monster monster = new Monster("Dire Test", "Dire Test", 0, 10, 25, 
            5, null, null, null, null, null, 0, 0);
        
        // Test that temp HP works
        assertEquals(25, monster.getCurrentHp());
        monster.setTempHp(5);
        monster.changeHp(-5);
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

    @Test
    @Disabled
    public void campaignTest() throws IllegalStateException, IOException, CsvException {
        List<Player> party = CreatureFactory.createParty("Adeo");
        assertTrue(_nameInParty(party, "Akamu"));
        assertTrue(_nameInParty(party, "Helios"));
        assertTrue(_nameInParty(party, "Riley"));
        assertTrue(_nameInParty(party, "Xena"));
        assertFalse(_nameInParty(party, "Foobar"));
    }

    // Tests if list `party` has a field `fieldName` with value `contents`
    private static boolean _nameInParty(List<Player> party, String name) {
        for (Player player : party) {
            if (player.getNAME().equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Test
    public void createMonster() throws Exception {       
        Map<String, Integer> monsterMap = new HashMap<>();
        monsterMap.put("Test Monster", 1);
        monsterMap.put("Commoner", 1);

        List<Monster> monsters = new ArrayList<>();
        SystemLambda.withTextFromSystemIn("N", "N").execute(() -> {
            List<Monster> monstersTemp = CreatureFactory.monstersFromName(monsterMap);
            for (Monster monster : monstersTemp) {
                monsters.add(monster);
            }
        });

        Monster commoner = monsters.get(0);
        Monster testMon = monsters.get(1);
        
        assertEquals("Test Monster 1", testMon.getNAME());
        assertEquals("Commoner 1", commoner.getNAME());
        assertEquals(8, testMon.getDEX());
        assertEquals(10, commoner.getDEX());
        

        assertEquals(5, testMon.getMAX_HP());
        assertEquals(4, commoner.getMAX_HP());
        assertEquals(5, testMon.getCurrentHp());
        assertEquals(4, commoner.getMAX_HP());
        assertEquals(5, testMon.getAUTO_HEAL());
        assertEquals(0, commoner.getAUTO_HEAL());
        
        assertNotNull(testMon.getDamageResponses());
        assertNotNull(commoner.getDamageResponses());
        assertArrayEquals(new DamageType[] {DamageType.COLD}, testMon.getVulnerabilities());
        assertArrayEquals(new DamageType[] {}, commoner.getVulnerabilities());
        assertArrayEquals(new DamageType[] {DamageType.FIRE, DamageType.ACID}, testMon.getResistances());
        assertArrayEquals(new DamageType[] {}, commoner.getResistances());
        assertArrayEquals(new DamageType[] {DamageType.LIGHTNING}, testMon.getImmunities());
        assertArrayEquals(new DamageType[] {}, commoner.getImmunities());

        assertArrayEquals(new int[] {4,3,3,1,0,0,0,0,0}, testMon.getSpellSlots());
        assertArrayEquals(new int[] {0,0,0,0,0,0,0,0,0}, commoner.getSpellSlots());
        assertArrayEquals(new boolean[] {false,false,false,false,true,true}, testMon.getRecharge());
        assertArrayEquals(new boolean[] {false,false,false,false,false,false}, commoner.getRecharge());
        
        assertEquals(2, testMon.getPerDayActions().get("Foo"));
        assertEquals(1, testMon.getPerDayActions().get("Bar"));

        assertEquals("Passive abilities go here", testMon.getPASSIVES());
        assertEquals("", commoner.getPASSIVES());
        assertEquals(3, testMon.getLegendaryActions());
        assertEquals(0, commoner.getLegendaryActions());
        assertEquals(2, testMon.getLegendaryResistances());
        assertEquals(0, commoner.getLegendaryResistances());
        
    }

    @Test
    public void monsterAlias() throws Exception {
        Map<String, Integer> monsterMap = new HashMap<>();
        monsterMap.put("Test Monster", 1);

        SystemLambda.withTextFromSystemIn("Y", "alias").execute(() -> {
            List<Monster> monstersTemp = CreatureFactory.monstersFromName(monsterMap);
            for (Monster monster : monstersTemp) {
                assertEquals("Test Monster", monster.getStatBlockName());
                assertEquals("alias", monster.getNAME());
            }
        });        
    }

}
