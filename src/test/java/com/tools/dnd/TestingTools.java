package com.tools.dnd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.stefanbirkner.systemlambda.SystemLambda;
import com.tools.dnd.creatures.Monster;
import com.tools.dnd.creatures.SpawnPoint;

public class TestingTools {
    static List<Monster> getMonstersNoAliases(String[] monsterNames) throws Exception {
        Map<String, Integer> monsterMap = new HashMap<>();
        String[] no = new String[monsterNames.length * 2];
        for (int i = 0; i < monsterNames.length; i++) {
            String mon = monsterNames[i];
            monsterMap.put(mon, 1);
            no[2*i] = "N";
            no[2*i + 1] = "N";
        }

        List<Monster> monsters = new ArrayList<>();
        SystemLambda.withTextFromSystemIn(no).execute(() -> {
            List<Monster> monstersTemp = SpawnPoint.monstersFromName(monsterMap);
            for (Monster mon : monstersTemp) {
                monsters.add(mon);
            }
        });
        return monsters;
    }

    static List<Monster> getMonstersWithAliases(String[] monsterNames, String[] aliases) throws Exception {
        Map<String, Integer> monsterMap = new HashMap<>();
        String[] inputs = new String[3 * monsterNames.length];
        for (int i = 0; i < monsterNames.length; i++) {
            String mon = monsterNames[i];
            monsterMap.put(mon, 1);
            inputs[3*i] = "y";
            inputs[3*i + 1] = aliases[i];
            inputs[3*i + 2] = "n";
        }

        List<Monster> monsters = new ArrayList<>();
        SystemLambda.withTextFromSystemIn(inputs).execute(() -> {
            List<Monster> monstersTemp = SpawnPoint.monstersFromName(monsterMap);
            for (Monster mon : monstersTemp) {
                monsters.add(mon);
            }
        });
        return monsters;
    }

}
