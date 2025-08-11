package com.tools.dnd.core;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.opencsv.exceptions.CsvException;
import com.tools.dnd.creatures.BattleEvent;
import com.tools.dnd.creatures.Creature;
import com.tools.dnd.creatures.Monster;
import com.tools.dnd.creatures.Player;
import com.tools.dnd.creatures.SpawnPoint;
import com.tools.dnd.user_input.InputHandler;

public class CombatRunner {
    private static final InputHandler input = new InputHandler();
    public static void main(String[] args) {
        List<Player> party = getParty();
        List<Monster> monsters = getMonsters();
        List<BattleEvent> events = getEvents();
        InitList initList = new InitList(party, monsters, events);
        while (!initList.isCombatDone()) {
            try {
                initList.nextTurn();
                input.getString("Enter any key to continue.");
            } catch (Exception e) {
                initList.endEarly(true);
                e.printStackTrace();
            }
        }
        System.out.println(initList.getOutcome());
    }

    public static List<Player> getParty() {
        try {
            String partyName = input.getString("Party Name:");
            return SpawnPoint.createParty(partyName);
        } catch (IllegalStateException | IOException | CsvException e) {
            System.out.println("Couldn't find that, please try again!");
            return getParty();
        }
    }

    public static List<Monster> getMonsters() {
        try {
            Map<String, Integer> monsterMap = input.getMap("Monster Name:", "Number:",
                                    "Add another monster?");
            return SpawnPoint.monstersFromName(monsterMap);
        } catch (IllegalStateException | IOException | CsvException e) {
            System.out.println(e.getMessage());
            return getMonsters();
        }
    }

    public static List<BattleEvent> getEvents() {
        if (input.getYesNo("Are there any battlefield events with static initiative, like lair actions or traps?")) {
            return SpawnPoint.getBattleEvents();
        }
        return List.of(); // else return empty list     
    }

}
