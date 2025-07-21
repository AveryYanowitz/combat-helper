package com.tools.dnd.creatures;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tools.dnd.util.AskUtils.getArray;
import static com.tools.dnd.util.AskUtils.getYesNo;
import static com.tools.dnd.util.AskUtils.getString;

import com.opencsv.exceptions.CsvException;
import com.tools.dnd.util.CsvUtils;
import com.tools.dnd.util.Enums;
import com.tools.dnd.util.Enums.DamageResponse;
import com.tools.dnd.util.Enums.DamageType;

public class CreatureFactory {
    public static List<Player> createParty(String campaignName) throws IllegalStateException, IOException, CsvException {
        String[] absentPeople = getArray("Who's missing?");
        for (int i = 0; i < absentPeople.length; i++) {
            absentPeople[i] = absentPeople[i].strip();
        }
        return _playersFromCampaign(campaignName, absentPeople);
    }

    private static List<Player> _playersFromCampaign(String campaignName, String[] absentPeople) throws IOException, CsvException {
        List<List<String>> playersInCampaign = CsvUtils.readLinesMatchingCol("party_list.csv", 0, campaignName);
        List<List<String>> presentPlayersOnly = CsvUtils.excludeLinesMatchingCol(playersInCampaign, 1, absentPeople);

        List<Player> asPlayers = new ArrayList<>();
        for (List<String> list : presentPlayersOnly) {
            String name = list.get(1);
            int dex = Integer.parseInt(list.get(2));
            asPlayers.add(new Player(name, dex));
        }
        return asPlayers;
    }

    /**
     * Instantiate the monsters specified in the map and return them in a list
     * @param namesAndNumbers A map from the monsters' names (as they appear in monster_list.csv)
     *                         to the number of each monster that's present
     * @return A list of the monsters created
     * @throws IOException If reading in the CSV fails
     * @throws CsvException If reading in the CSV fails
     */
    public static List<Monster> monstersFromName(Map<String, Integer> namesAndNumbers) 
                                                            throws IOException, CsvException {
        List<List<String>> rows = CsvUtils.readLinesMatchingCol("monster_list.csv", 
                                                    0, namesAndNumbers.keySet());
        List<Monster> monsters = new ArrayList<>();                                            
        for (List<String> row : rows) {
            String statBlockName = row.get(0).trim();
            int initBonus = Integer.parseInt(row.get(1).trim());
            int dex = Integer.parseInt(row.get(2).trim());
            int hp = Integer.parseInt(row.get(3).trim());
            Map<DamageResponse,DamageType[]> damageResponses = _getDamageResponses(row);            
            int autoheal = Integer.parseInt(row.get(7).trim());
            int[] spellSlots = _getSlots(row.get(8).trim());
            boolean[] recharge = _getRecharge(row.get(9).trim());
            Map<String, Integer> perDayActions = _getActions(row.get(10).trim());
            String passives = row.get(11).trim();
            int[] legendaries = _getLegendaries(row.get(12).trim());
            int legendaryActions = legendaries[0];
            int legendaryResistances = legendaries[1];

            String[] aliases = new String[namesAndNumbers.get(statBlockName)];
            if (getYesNo("Give nickname(s) to "+statBlockName+"?")) {
                for (int i = 0; i < aliases.length; i++) {
                    aliases[i] = getString("Alias for "+statBlockName+" "+(i+1)+":");
                }
            } else {
                for (int i = 0; i < aliases.length; i++) {
                    aliases[i] = statBlockName + " " + (i + 1);
                }
            }

            for (String alias : aliases) {
                monsters.add(new Monster(statBlockName, alias, initBonus, dex, 
                                        hp, autoheal, damageResponses, 
                                        recharge, spellSlots, perDayActions, 
                                        passives, legendaryActions, legendaryResistances));
            }
        }
        return monsters;

    }

    /**
     * Read in the given CSV row and return the monster's damage type response
     * @param row The row of the CSV to read
     * @return A map representing which damage types the monster is vulnerable, resistant, and immune to
     */
    private static Map<DamageResponse,DamageType[]> _getDamageResponses(List<String> row) {
        Map<DamageResponse,DamageType[]> damageMap = new HashMap<>();
        int col = 4;
        for (DamageResponse val : DamageResponse.values()) {
            if (val == DamageResponse.NORMAL) {
                continue;
            }
            String[] rawString = row.get(col).split(";");
            DamageType[] damageTypes = new DamageType[rawString.length];
            for (int i = 0; i < rawString.length; i++) {
                damageTypes[i] = Enums.evaluateType(rawString[i]);
            }

            damageMap.put(val, damageTypes);
            col++;
        }
        return damageMap;
    }

    private static Map<String, Integer> _getActions(String entryToParse) {
        Map<String, Integer> actions = new HashMap<>();
        if (entryToParse.equals("") || entryToParse.equals("\"\"")) {
            return actions;
        }
        int i = 0;
        int max = entryToParse.length();
        StringBuilder sb = new StringBuilder();
        boolean inString = false;
        while (i < max) {
            char ch = entryToParse.charAt(i++);
            if (ch == '\'') {
                inString = !inString; // either we've entered or exited a name
            } else if (inString) {
                sb.append(ch);
            } else if (ch == ':') {
                String name = sb.toString();
                sb = new StringBuilder();

                i++; // skip the space
                while (i < max && entryToParse.charAt(i) != ',') {
                    sb.append(entryToParse.charAt(i));
                    i++;
                }
                int num = Integer.parseInt(sb.toString());
                actions.put(name, num);

                i += 2; // advance past comma and space
                sb = new StringBuilder();
            } else {
                throw new IllegalArgumentException("Failed to parse string "+entryToParse+" at char "+i);
            }
        }
        return actions;
    }

    private static int[] _getSlots(String slotString) {
        int[] spellSlots = new int[9];
        String[] slotStringSplit = slotString.split(";");
        for (int i = 0; i < slotStringSplit.length; i++) {
            String slotNum = slotStringSplit[i];
            try {
                spellSlots[i] = Integer.parseInt(slotNum);
            } catch (NumberFormatException e) { // if empty string
                break;
            }
        }
        return spellSlots;
    }

    private static boolean[] _getRecharge(String rechargeString) {
        boolean[] recharge = new boolean[6];
        String[] recharges = rechargeString.split(";");
        for (String num : recharges) {
            try {
                int index = Integer.parseInt(num) - 1;
                recharge[index] = true;
            } catch (NumberFormatException e) { // if empty string
                break;
            }
        }
        return recharge;
    }

    private static int[] _getLegendaries(String legendaryString) {
        int[] legendaries = new int[2];
        String[] legends = legendaryString.split(";");
        legendaries[0] = Integer.parseInt(legends[0]);
        legendaries[1] = Integer.parseInt(legends[1]);
        return legendaries;
    }

}
