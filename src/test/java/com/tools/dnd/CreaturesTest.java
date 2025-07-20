package com.tools.dnd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.opencsv.exceptions.CsvException;
import com.tools.dnd.creatures.Monster;
import com.tools.dnd.creatures.Player;

public class CreaturesTest {
    private Monster monster;

    @BeforeEach
    public void setup() {
        monster = new Monster("Dire Test", "Dire Test", 0, 10, 25, 
        5, null, null, null, null, null, 0, 0);
    }

    @Test
    public void monsterHpLogic() {
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
        List<Player> party = Player.createParty("Adeo");
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

}
