package com.tools.dnd;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.github.stefanbirkner.systemlambda.SystemLambda;
import com.tools.dnd.core.InitList;
import com.tools.dnd.creatures.Monster;
import com.tools.dnd.creatures.SpawnPoint;

public class CommandsTest {
    
    private InitList initList;

    @BeforeEach
    void setup() {
        try {
            SystemLambda.withTextFromSystemIn("n","n").execute(() -> {
                Map<String, Integer> monsterMap = Map.of("Commoner", 1, "Kraken", 1);
                initList = new InitList(SpawnPoint.monstersFromName(monsterMap));
            });
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void addCommand() throws Exception {
        _runCommand("!add", "Ghoul","1","N","N");
        assertNotNull(initList.getMonster("Ghoul 1"));
    }

    @Test
    void endCommand() throws Exception {
        _runCommand("!end");
        assertEquals("Combat was ended early", initList.getOutcome());
    }

    @Test
    void condAddCommand() throws Exception {
        _runCommand("!cond-add", "Commoner 1", "frightened, petrified");
        Monster mon = initList.getMonster("Commoner 1");
        assertTrue(mon.hasCondition("frightened"));
        assertTrue(mon.hasCondition("petrified"));
        assertFalse(mon.hasCondition("paralyzed"));
    }

    @Test
    void condDelCommand() throws Exception {
        Monster mon = initList.getMonster("Commoner 1");
        mon.addConditions(new String[] {"frightened, petrified"});
        _runCommand("!cond-del", "Commoner 1", "frightened, petrified");
        assertDoesNotThrow(() -> {
            _runCommand("!cond-del", "Commoner 1", "paralyzed");
        });
        assertFalse(mon.hasCondition("frightened"));
        assertFalse(mon.hasCondition("petrified"));
    }

    @Test
    void hpCommand() throws Exception {
        _runCommand("!hp","Commoner 1", "3");
        Monster mon = initList.getMonster("Commoner 1");
        assertEquals(3, mon.getCurrentHp());
    }

    @Test
    void initCommand() throws Exception {
        if (initList.getNameAtIndex(0).equals("Commoner 1")) { // make sure we're changing it
            _runCommand("!init", "Kraken 1, Commoner 1");
            assertEquals("Kraken 1", initList.getNameAtIndex(0));
        } else {
            _runCommand("!init", "Commoner 1, Kraken 1");
            assertEquals("Commoner 1", initList.getNameAtIndex(0));
        }
    }

    @Test
    @Disabled
    // This test works, but fails when creature's turn finishes
    // because current legendary actions gets reset (as it should)
    void laCommand() throws Exception {
        _runCommand("!la", "Commoner 1", "100");
        _runCommand("!la", "Kraken 1", "-1");
        assertEquals(100, initList.getMonster("Commoner 1").getLegendaryActions());
        assertEquals(0, initList.getMonster("Kraken 1").getLegendaryActions());
    }

    @Test
    void lrCommand() throws Exception {
        _runCommand("!lr", "Commoner 1", "100");
        _runCommand("!lr", "Kraken 1", "-1");
        assertEquals(100, initList.getMonster("Commoner 1").getLegendaryResistances());
        assertEquals(0, initList.getMonster("Kraken 1").getLegendaryResistances());        
    }

    private void _runCommand(String cmd, String... responses) throws Exception {
        String[] allResponses = new String[responses.length + 4];
        allResponses[0] = cmd;
        int i = 0; // we want to use i after this loop
        for (; i < responses.length; i++) {
            allResponses[i + 1] = responses[i];
        }
        allResponses[++i] = "N"; // no temp HP (monster) / not dead (player)
        allResponses[++i] = "N"; // no healing (monster) / not at 0 HP (player)
        allResponses[++i] = "";  // no damage dealt (both)
        SystemLambda.withTextFromSystemIn(allResponses).execute(() -> {
            initList.nextTurn();
        });
    }

}
