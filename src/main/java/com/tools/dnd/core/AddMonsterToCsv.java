package com.tools.dnd.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opencsv.exceptions.CsvException;
import com.tools.dnd.user_input.InputHandler;
import com.tools.dnd.util.CsvParser;
import com.tools.dnd.util.Enums;
import com.tools.dnd.util.Enums.DamageType;

public class AddMonsterToCsv {

    private final static InputHandler _INPUT;

    static {
        _INPUT = new InputHandler();
    }

    // Too complex to write simple unit tests, but I've verified this works
    public static void main(String[] args) throws IOException, CsvException {
        List<String> existingMonsters =  CsvParser.getColFromAllRows("monster_list.csv", 0);
        while (true) {
            String[] newRow = _getCsvEntry(existingMonsters);
            if (newRow != null) {
                CsvParser.writeRow("monster_list.csv", newRow);
            }
            if (!_INPUT.getYesNo("Add another?")) {
                break;
            }
        }
        System.out.println("Thanks for stopping by!");
    }

    private static String[] _getCsvEntry(List<String> existingMonsters) {
        String[] csvString = new String[13];
        String name;
        csvString[0] = name = _INPUT.getString("Monster Name:");
        if (existingMonsters.contains(name)) {
            System.out.println("That monster has already been added.");
            return null;
        }

        csvString[1] = _INPUT.getIntString("Initiative Bonus:");
        csvString[2] = _INPUT.getIntString("Dexterity Score:");
        csvString[3] = _INPUT.getIntString("Maximum Hit Points:");
        csvString[4] = _getDamageResponse("vulnerable", name);
        csvString[5] = _getDamageResponse("resistant", name);
        csvString[6] = _getDamageResponse("immune", name);
        csvString[7] = _getAutoheal(name);
        csvString[8] = _getSpellSlots(name);
        csvString[9] = _getRecharges(name);
        csvString[10] = _getOtherActions(name);
        csvString[11] = _INPUT.getString("Description of other passives:");
        csvString[12] = _getLegendaries(name);

        return csvString;
    }

    private static String _getDamageResponse(String damageResponseName, String monName) {
        String[] fullArr = _INPUT.getArray("What damage types is " + monName + " " + damageResponseName + " to?");
        List<String> validTypesOnly = new ArrayList<>();
        for (String str : fullArr) {
            if (Enums.evaluateType(DamageType.class, str) != DamageType.DEFAULT) {
                validTypesOnly.add(str);
            }
        }
        return String.join(";", validTypesOnly);
    }
    
    private static String _getAutoheal(String monName) {
        if (!_INPUT.getYesNo("Does "+monName+" autoheal?")) {
            return "0";
        }
        return _INPUT.getString("How much per round?");
    }
    
    private static String _getSpellSlots(String monName) {
        if (!_INPUT.getYesNo("Does "+monName+" expend spell slots?")) {
            return "";
        }

        System.out.println("Input the number slots of each level that "+monName+"has.");
        StringBuilder slotsAsString = new StringBuilder();        
        for (int lvl = 1; lvl < 10; lvl++) {
            String number = _INPUT.getIntString("Level "+lvl+":");
            slotsAsString.append(number);
            if (lvl < 9) { // don't put semicolon on the end
                slotsAsString.append(";");
            }
        }
        return slotsAsString.toString();
    }

    private static String _getRecharges(String name) {
        if (!_INPUT.getYesNo("Does "+name+" have Recharge abilities?")) {
            return "";
        }
        int minRecharge = _INPUT.getInt("What is the minimum roll to recharge?");
        StringBuilder recharges = new StringBuilder();
        for (int roll = minRecharge; roll < 7; roll++) {
            recharges.append(roll);
            if (roll < 6) { // don't put semicolon on the end
                recharges.append(";");
            }
        }
        return recharges.toString();
    }

    private static String _getOtherActions(String name) {
        Map<String, Integer> actionMap = new HashMap<>();
        // Even though innate spells are the same under the hood,
        // I find this to be a nicer way to enter them for the user
        if (_INPUT.getYesNo("Does "+name+" have innate spellcasting?")) {
            int dailyUses = 1;
            while (true) {
                String[] spells = _INPUT.getArray("Which spells can be used "+dailyUses
                                        +" times per day? Type 'CANCEL' to finish"
                                        +" adding innate spells)");
                if (spells[0].equals("cancel")) {
                    break;
                }
                for (String spell : spells) {
                    actionMap.put(spell, dailyUses);
                }
                dailyUses++;
            }
        }

        do {
            actionMap.put(_INPUT.getString("Action name:"), 
                            _INPUT.getInt("Uses per day (0 to cancel):"));
        } while (_INPUT.getYesNo("Add another per-day action?"));
        return mapToString(actionMap, 0);

    }

    private static String _getLegendaries(String name) {
        StringBuilder legendary = new StringBuilder();
        legendary.append(_INPUT.getIntString("How many legendary actions does "+name+" have?"));
        legendary.append(";");
        legendary.append(_INPUT.getIntString("How many legendary resistances does "+name+" have?"));
        return legendary.toString();
    }


    private static <K, V> String mapToString(Map<K,V> map, V cancelValue) {
        StringBuilder mapString = new StringBuilder();
        mapString.append("\"");
        for (var entry : map.entrySet()) {
            if (entry.getValue().equals(cancelValue)) {
                continue;
            }
            mapString.append("'");
            mapString.append(entry.getKey());
            mapString.append("': ");
            mapString.append(entry.getValue());
            mapString.append(", ");
        }
        if (mapString.length() > 1) {                       // if the map wasn't empty, then
            mapString.deleteCharAt(mapString.length() - 2); // remove the final comma & space
        }
        mapString.append("\"");
        return mapString.toString();
    }

}
