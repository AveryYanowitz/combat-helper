package com.tools.dnd.core;

import static com.tools.dnd.util.AskUtils.getArray;
import static com.tools.dnd.util.AskUtils.getInt;
import static com.tools.dnd.util.AskUtils.getIntString;
import static com.tools.dnd.util.AskUtils.getString;
import static com.tools.dnd.util.AskUtils.getYesNo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.exceptions.CsvException;
import com.tools.dnd.util.CsvUtils;
import com.tools.dnd.util.Enums.DamageResponse;

public class AddMonsterToCsv {
    public static void main(String[] args) throws IOException, CsvException {
        List<String> existingMonsters =  CsvUtils.getColFromAllRows("monster_list.csv", 0);
        while (true) {
            _csvWrite(existingMonsters);
            if (!getYesNo("Add another?")) {
                break;
            }
        }
        System.out.println("Thanks for stopping by!");
    }

    private static void _csvWrite(List<String> existingMonsters) {
        String[] csvString = new String[13];
        csvString[0] = getString("Monster Name:");
        if (existingMonsters.contains(csvString[0])) {
            System.out.println("That monster has already been added.");
            return;
        }

        // Integer.toString() to verify that 
        csvString[1] = getIntString("Initiative Bonus:");
        csvString[2] = getIntString("Maximum Hit Points:");
        csvString[3] = _getDamageResponse("vulnerable");
        csvString[4] = _getDamageResponse("resistant");
        csvString[5] = _getDamageResponse("immune");

    }

    private static String _getDamageResponse(String damageResponseName) {
        String[] a = getArray("What damage types is the monster "+damageResponseName+" to?");
        return String.join(";", a);
    }

    private static String _autoheal(String monName) {
        if (!getYesNo("Does the monster autoheal?")) {
            return "0";
        }
        return getString("How much per round?");
    }

}
