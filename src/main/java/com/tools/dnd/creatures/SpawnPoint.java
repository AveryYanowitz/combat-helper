package com.tools.dnd.creatures;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opencsv.exceptions.CsvException;
import com.tools.dnd.user_input.InputHandler;
import com.tools.dnd.util.CsvParser;
import com.tools.dnd.util.Enums;
import com.tools.dnd.util.Enums.DamageResponse;
import com.tools.dnd.util.Enums.DamageType;

/** Tools for creating Monsters, Players, etc. */
public class SpawnPoint {
    private static InputHandler input = new InputHandler();

    /**
     * Create a List of Players in the given campaign, excluding absent players
     * @param campaignName The name of the campaign
     * @return A List of Players
     * @throws IllegalStateException When no party members are found
     * @throws IOException If reading in the CSV fails
     * @throws CsvException If reading in the CSV fails
     */
    public static List<Player> createParty(String campaignName) throws IllegalStateException, IOException, CsvException {
        String[] absentPeople = input.getArray("Who's missing?");
        for (int i = 0; i < absentPeople.length; i++) {
            absentPeople[i] = absentPeople[i].strip();
        }
        return _playersFromCampaign(campaignName, absentPeople);
    }

    private static List<Player> _playersFromCampaign(String campaignName, String[] absentPeople) 
                                        throws IllegalStateException, IOException, CsvException {
        List<List<String>> playersInCampaign = CsvParser.readLinesMatchingCol("party_list.csv", 0, campaignName);
        List<List<String>> presentPlayersOnly = CsvParser.excludeLinesMatchingCol(playersInCampaign, 1, absentPeople);

        List<Player> asPlayers = new ArrayList<>();
        for (List<String> list : presentPlayersOnly) {
            String name = list.get(1);
            int dex = Integer.parseInt(list.get(2));
            asPlayers.add(new Player(name, dex, input));
        }
        if (asPlayers.size() == 0) {
            throw new IllegalStateException("No players found");
        }
        return asPlayers;
    }

    /**
     * Instantiate the monsters specified in the map and return them in a list
     * @param namesAndNumbers A map from the monsters' names (as they appear in monster_list.csv)
     *                         to the number of each monster that's present
     * @return A list of the monsters created
     * @throws IllegalStateException If no monsters are found
     * @throws IOException If reading in the CSV fails
     * @throws CsvException If reading in the CSV fails
     */
    public static List<Monster> monstersFromName(Map<String, Integer> namesAndNumbers) 
                                            throws IllegalStateException, IOException, CsvException {
        List<List<String>> rows = CsvParser.readLinesMatchingCol("monster_list.csv", 
                                                    0, namesAndNumbers.keySet());
        List<Monster> monsters = new ArrayList<>();                                            
        for (List<String> row : rows) {
            String statBlockName = row.get(0).strip();
            int initBonus = Integer.parseInt(row.get(1).strip());
            int dex = Integer.parseInt(row.get(2).strip());
            int hp = Integer.parseInt(row.get(3).strip());
            Map<DamageResponse,DamageType[]> damageResponses = _getDamageResponses(row);            
            int autoheal = Integer.parseInt(row.get(7).strip());
            int[] spellSlots = _getSlots(row.get(8).strip());
            boolean[] recharge = _getRecharge(row.get(9).strip());
            Map<String, Integer> perDayActions = _getActions(row.get(10).strip());
            String passives = row.get(11).strip();
            int[] legendaries = _getLegendaries(row.get(12).strip());
            int legendaryActions = legendaries[0];
            int legendaryResistances = legendaries[1];

            String[] aliases = new String[namesAndNumbers.get(statBlockName)];
            if (input.getYesNo("Give nickname(s) to "+statBlockName+"?")) {
                for (int i = 0; i < aliases.length; i++) {
                    aliases[i] = input.getString("Alias for "+statBlockName+" "+(i+1)+":");
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
        if (monsters.size() == 0) {
            throw new IllegalStateException("No monsters found");
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
            if (val == DamageResponse.DEFAULT) {
                continue;
            }
            String[] splitStr = row.get(col).strip().split(";");
            DamageType[] damageTypes;
            if (splitStr[0].equals("")) {
                damageTypes = new DamageType[0];
            } else {
                damageTypes = new DamageType[splitStr.length];
                for (int i = 0; i < splitStr.length; i++) {
                    damageTypes[i] = Enums.evaluateType(DamageType.class, splitStr[i]);
                }
            }

            damageMap.put(val, damageTypes);
            col++;
        }
        return damageMap;
    }

    private static Map<String, Integer> _getActions(String actionStr) {
        Map<String, Integer> actions = new HashMap<>();
        if (actionStr.equals("") || actionStr.equals("\"\"")) {
            return actions;
        }
        int i = 0;
        int max = actionStr.length();
        StringBuilder sb = new StringBuilder();
        boolean inString = false;
        while (i < max) {
            char ch = actionStr.charAt(i++);
            if (ch == '\'') {
                inString = !inString; // either we've entered or exited a name
            } else if (inString) {
                sb.append(ch);
            } else if (ch == ':') {
                String name = sb.toString();
                sb = new StringBuilder();

                i++; // skip the space
                while (i < max && actionStr.charAt(i) != ',') {
                    sb.append(actionStr.charAt(i));
                    i++;
                }
                int num = Integer.parseInt(sb.toString());
                actions.put(name, num);

                i += 2; // advance past comma and space
                sb = new StringBuilder();
            } else {
                // we incremented i after reading ch so we have to do i-1
                throw new IllegalArgumentException("Failed to parse string "+actionStr+" at char "+(i-1));
            }
        }
        return actions;
    }

    private static int[] _getSlots(String slotStr) {
        int[] spellSlots = new int[9];
        String[] slotStringSplit = slotStr.split(";");
        for (int i = 0; i < slotStringSplit.length; i++) {
            String slotNum = slotStringSplit[i];
            try {
                spellSlots[i] = Integer.parseInt(slotNum);
            } catch (NumberFormatException e) { // if slotStr was empty string
                break;
            }
        }
        return spellSlots;
    }

    private static boolean[] _getRecharge(String rechargeStr) {
        boolean[] recharge = new boolean[6];
        String[] recharges = rechargeStr.split(";");
        for (String num : recharges) {
            try {
                int index = Integer.parseInt(num) - 1;
                recharge[index] = true;
            } catch (NumberFormatException e) { // if rechargeStr was empty string
                break;
            }
        }
        return recharge;
    }

    private static int[] _getLegendaries(String legendaryStr) {
        int[] legendaries = new int[2];
        String[] legends = legendaryStr.split(";");
        legendaries[0] = Integer.parseInt(legends[0]);
        legendaries[1] = Integer.parseInt(legends[1]);
        return legendaries;
    }

}
