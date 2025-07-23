package com.tools.dnd.core;

import static com.tools.dnd.util.AskUser.getMap;
import static com.tools.dnd.util.AskUser.getString;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.opencsv.exceptions.CsvException;
import com.tools.dnd.creatures.Monster;
import com.tools.dnd.creatures.Player;
import com.tools.dnd.creatures.SpawnPoint;

public class Main {
    public static void main(String[] args) {
        List<Player> party = getParty();
        List<Monster> monsters = getMonsters();
        InitList initList = new InitList(party, monsters);
        while (!initList.combatDone()) {
            initList.nextTurn();
        }
        System.out.println("Combat is done! "+initList.getOutcome());
    }

    public static List<Player> getParty() {
        try {
            String partyName = getString("Party name: ");
            return SpawnPoint.createParty(partyName);
        } catch (IllegalStateException | IOException | CsvException e) {
            System.out.println("Couldn't find that, please try again!");
            return getParty();
        }
    }

    public static List<Monster> getMonsters() {
        try {
            Map<String, Integer> monsterMap = getMap("Monster Name:", "Number:",
                                    "Add another monster?");
            return SpawnPoint.monstersFromName(monsterMap);
        } catch (IllegalStateException | IOException | CsvException e) {
            return getMonsters();
        }
    }

}
