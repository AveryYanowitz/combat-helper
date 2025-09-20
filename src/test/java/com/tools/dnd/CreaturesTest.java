package com.tools.dnd;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

import com.github.stefanbirkner.systemlambda.SystemLambda;
import com.tools.dnd.creatures.SpawnPoint;
import com.tools.dnd.creatures.Monster;
import com.tools.dnd.creatures.Player;
import com.tools.dnd.util.Enums.DamageType;

public class CreaturesTest {

    @Test
    public void monsterHpLogic() {
        Monster mon = new Monster("Dire Test", "Dire Test", 0, 10, 25, 
            5, null, null, null, null, null, 0, 0);
        
        // Test that temp HP works
        assertEquals(25, mon.getCurrentHp());
        mon.setTempHp(5);
        mon.changeHp(-5);
        assertEquals(25, mon.getCurrentHp());
        assertEquals(0, mon.getTempHp());

        // Test that normal damage works
        mon.changeHp(-5);
        assertEquals(20, mon.getCurrentHp());
        assertEquals(25, mon.getMAX_HP());
        
        // Healing
        mon.changeHp(5);
        assertEquals(25, mon.getCurrentHp());
        assertEquals(25, mon.getMAX_HP());

        // Over-healing
        mon.changeHp(15);
        assertEquals(25, mon.getCurrentHp());
        assertEquals(25, mon.getMAX_HP());
    }

    @Test
    public void campaignTest() throws Exception {
        List<Player> party = new ArrayList<>();
        SystemLambda.withTextFromSystemIn("Akamu","10","10","10")
            .execute(() -> {
                List<Player> partyTemp = SpawnPoint.createParty("Adeo");
                for (Player plr : partyTemp) {
                    party.add(plr);
                }
            });

        assertTrue(_nameInParty(party, "Helios"));
        assertTrue(_nameInParty(party, "Riley"));
        assertTrue(_nameInParty(party, "Xena"));
        assertFalse(_nameInParty(party, "Akamu"));
        assertFalse(_nameInParty(party, "Foobar"));

        for (Player player : party) {
            assertEquals(10, player.getInitiative());
        }
    }

    // Tests if list `party` has a field `fieldName` with value `contents`
    private static boolean _nameInParty(List<Player> party, String name) {
        for (Player player : party) {
            if (player.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Test
    public void createMonster() throws Exception {       
        String[] monNames = {"Test Monster", "Commoner"};
        List<Monster> monsters = TestingTools.getMonstersNoAliases(monNames);

        Monster commoner = monsters.get(0);
        Monster testMon = monsters.get(1);
        
        assertEquals("Test Monster 1", testMon.getName());
        assertEquals("Commoner 1", commoner.getName());
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
        assertEquals("Check stat block for passives!", commoner.getPASSIVES());
        assertEquals(3, testMon.getLegendaryActions());
        assertEquals(0, commoner.getLegendaryActions());
        assertEquals(2, testMon.getLegendaryResistances());
        assertEquals(0, commoner.getLegendaryResistances());
        
    }

    @Test
    public void monsterAlias() throws Exception {
        List<Monster> aliasedMons = TestingTools.getMonstersWithAliases(new String[] {"Test Monster"}, new String[] {"alias"});

        SystemLambda.withTextFromSystemIn("Y", "alias").execute(() -> {
            for (Monster mon : aliasedMons) {
                assertEquals("Test Monster", mon.getStatBlockName());
                assertEquals("alias", mon.getName());
            }
        });        
    }

}
