package com.tools.dnd.user_input;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.tools.dnd.core.InitList;
import com.tools.dnd.creatures.Monster;
import com.tools.dnd.creatures.SpawnPoint;
import com.tools.dnd.util.Enums;

public class InputHandler {
    private final boolean _COMMANDS_ENABLED;
    private final InitList _initList;
    private final CommandBundle _cmdBundle;

    public InputHandler() {
        _COMMANDS_ENABLED = false;
        _initList = null;
        _cmdBundle = null;
    }

    public InputHandler(InitList initList) {
        _COMMANDS_ENABLED = true;
        _initList = initList;
        _cmdBundle = new CommandBundle();

        _cmdBundle.register("!add", "Add more monsters to combat", () -> {
            Map<String, Integer> monsterMap = getMap("Monster Name:", "Number:", "Add another?");
            _initList.addCreatures(SpawnPoint.monstersFromName(monsterMap));
        });

        _cmdBundle.register("!end", "End combat early", () -> {
            _initList.endEarly(false);
            System.out.println(_initList.getOutcome());
        });

        _cmdBundle.register("!cond-add", "Add a condition to a monster", () -> {
            Monster mon = _initList.getMonster(getString("Monster Name:"));
            mon.addConditions(getArray("Conditions to add:"));
        });

        _cmdBundle.register("!cond-del", "Remove a condition from a monster", () -> {
            Monster mon = _initList.getMonster(getString("Monster Name:"));
            mon.removeConditions(getArray("Conditions to add:"));
        });

        _cmdBundle.register("!help", "Print out this message", () -> {
            System.out.println(_cmdBundle);
        });

        _cmdBundle.register("!hp", "Set a monster's HP", () -> {
            Monster mon = _initList.getMonster(getString("Monster Name:"));
            int currentHp = mon.getCurrentHp();
            int newHp = getInt("New HP:");
            mon.changeHp(newHp - currentHp);
        });

       _cmdBundle.register("!init", "Reorder the initiative list", () -> {
            _initList.reOrder(getArray("Enter new initiative order, using monsters' EXACT names:"));
        });

        _cmdBundle.register("!la", "Change a monster's remaining legendary actions", () -> {
            Monster mon = _initList.getMonster(getString("Monster Name:"));
            mon.setCurrentLegendaryActions(getInt("New Legendary Actions:"));
        });

        _cmdBundle.register("!lr", "Change a monster's remaining legendary resistances", () -> {
            Monster mon = _initList.getMonster(getString("Monster Name:"));
            mon.setLegendaryResistances(getInt("New Legendary Resistances:"));            
        });

        _cmdBundle.register("!print", "Print out a monster's current stats", () -> {
            Monster mon = _initList.getMonster(getString("Monster Name:"));
            System.out.println();
            System.out.println(mon);
            System.out.println();
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
        if (answer.length() > 0 && answer.charAt(0) == '!') {
            if (_COMMANDS_ENABLED) {                
                _cmdBundle.runCommand(answer);
                return getString(prompt);
            } else {
                System.out.println("Sorry, you can't run a command right now, and only commands can start with '!'.");
                return getString(prompt);
            }
        }
        return answer;
    }

    public String[] getArray(String prompt) {
        String answer = getString(prompt+" (Enter comma-separated list.)");
        String[] answerSplit;
        answerSplit = answer.split(",");
        for (int i = 0; i < answerSplit.length; i++) {
            answerSplit[i] = answerSplit[i].strip();
        }
        return answerSplit;
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

    /**
     * Get an integer, unless a particular value is given, in which case return the sentinelValue
     * @param prompt The prompt to present to the user
     * @param exception The string to check for before parsing as int; does not need to be an integer
     * @param sentinelValue The value to return when "exception" is answered
     * @return The user's answer, or "sentinelValue" if their answer exactly matches exception
     */
    public Integer getInt(String prompt, String exception, int sentinelValue) {
        String answer = getString(prompt);
        if (answer.equals(exception)) {
            return sentinelValue;
        }
        try {
            return Integer.valueOf(answer);
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
