package com.tools.dnd.combat_flow;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.tools.dnd.creatures.Creature;
import com.tools.dnd.creatures.Monster;
import com.tools.dnd.creatures.SpawnPoint;
import com.tools.dnd.util.Enums;

public class InputHandler {
    private final boolean _COMMANDS_ENABLED;
    private final InitList _initList;
    private final Map<String, Command> _commands;

    public InputHandler() {
        _COMMANDS_ENABLED = false;
        _initList = null;
        _commands = null;
    }

    public InputHandler(InitList initList) {
        _COMMANDS_ENABLED = true;
        _initList = initList;
        _commands = new HashMap<>();

        _commands.put("!add", () -> {
            Map<String, Integer> monsterMap = getMap("Monster Name:", "Number:", "Add another?");
            _initList.addCreatures(SpawnPoint.monstersFromName(monsterMap));
        });

        _commands.put("!end", () -> {
            _initList.endEarly();
        });

        _commands.put("!cond", () -> {
            Creature cr = _initList.getCreature(getString("Which creature to change?"));
            if (cr != null) {
                cr.addCondition(getString("What condition to add?"));
            } else {
                System.out.println("Sorry, couldn't find that creature.");
            }
        });

        _commands.put("!help", () -> {
            System.out.println("""
                    !add - add more monsters to combat
                    !end - end combat
                    !cond - modify a creature's conditions
                    !hp - set monster HP to new value
                    !init - reorder initiative list
                    !lr - change number of legendary resistances left
                    """);
        });

        _commands.put("!hp", () -> {
            Monster mon = _initList.getMonster(getString("Which monster's HP are you changing?"));
            System.out.println("Current HP: "+mon.getCurrentHp());
        });

        _commands.put("!init", () -> {
            String[] newOrder = getArray("Input new initiative order, using creatures' EXACT names:");
            _initList.reOrder(newOrder);
        });


        _commands.put("!lr", () -> {
            Monster mon = _initList.getMonster(getString("Which monster's legendary resistances are you changing?"));
            mon.setLegendaryRes(getInt("How many does it have now?"));
        });
    }
    public boolean getYesNo(String prompt) {
        String answer = getString(prompt + " (Y/N)").toLowerCase();
        if (answer.toLowerCase().equals("y")) {
            return true;
        }
        if (answer.toLowerCase().equals("n")) {
            return false;
        }
        System.out.println("Sorry, couldn't understand that!");
        return getYesNo(prompt);
    }

    @SuppressWarnings("resource")
    public String getString(String prompt) {
        Scanner in = new Scanner(System.in);
        System.out.print(prompt+" ");
        String answer = in.nextLine();
        if (_COMMANDS_ENABLED && answer.charAt(0) == '!') {
            
        }
        return in.nextLine();
    }

    public String[] getArray(String prompt) {
        String answer = getString(prompt+" (Enter comma-separated list.)");
        if (answer.contains(",")) {
            return answer.split(",");
        }
        return new String[] {answer};
    }

    public int getInt(String prompt) {
        try {
            String answer = getString(prompt);
            return Integer.parseInt(answer);
        } catch (NumberFormatException e) {
            System.out.println("Sorry, couldn't understand that!");
            return getInt(prompt);
        }
    }

    public Integer getInt(String prompt, String exception, int sentinelValue) {
        String answer = getString(prompt);
        if (answer.equals(exception)) {
            return sentinelValue;
        }
        try {
            return Integer.parseInt(answer);
        } catch (NumberFormatException e) {
            System.out.println("Sorry, couldn't understand that!");
            return getInt(prompt, exception, sentinelValue);
        }
    }
    
    public String getIntString(String prompt) {
        int answer = getInt(prompt);
        return Integer.toString(answer);
    }

    public <E extends Enum<E>> E getEnum(Class<E> clazz, String prompt) {
        String answer = getString(prompt);
        return Enums.evaluateType(clazz, answer);
    }
    
    public Map<String, Integer> getMap(String keyPrompt, String valPrompt, String addAnotherPrompt) {
        Map<String, Integer> returnMap = new HashMap<>();
        do {
            _putMapEntry(returnMap, keyPrompt, valPrompt);
        } while (getYesNo(addAnotherPrompt));
        return returnMap;
    }

    private void _putMapEntry(Map<String, Integer> map, String keyPrompt, String valPrompt) {
        try {
            String key = getString(keyPrompt);
            int val = getInt(valPrompt);
            map.put(key, val);
        } catch (Exception e) {
            System.out.println("Sorry, couldn't understand that!");
            _putMapEntry(map, keyPrompt, valPrompt);
        }
    }    

}
