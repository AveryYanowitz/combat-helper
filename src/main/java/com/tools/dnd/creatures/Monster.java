package com.tools.dnd.creatures;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tools.dnd.util.AskUtils.getInt;
import static com.tools.dnd.util.AskUtils.getString;
import static com.tools.dnd.util.AskUtils.getYesNo;

import com.opencsv.exceptions.CsvException;
import com.tools.dnd.util.CsvUtils;
import com.tools.dnd.util.DndUtils;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(prefix = "_")
public class Monster extends Creature {
    static enum DamageResponse {
        VULNERABILITIES,
        RESISTANCES,
        IMMUNITIES
    }

    private String _statBlockName;
    private final int _INIT_BONUS, _MAX_HP, _AUTO_HEAL, _MAX_LEGENDARY_ACTIONS;
    private int _currentHp, _tempHp, _legendaryActions, _legendaryResistances;
    private boolean[] _recharge;
    private int[] _spellSlots;
    private Map<String, Integer> _perDayActions;
    private Map<DamageResponse, String[]> _damageResponses;
    private boolean _bloodied;
    private final String _PASSIVES;

    public Monster(String statBlockName, String _displayName, int initBonus, int dex,
                    int hp, int autoheal, Map<DamageResponse, String[]> damages,
                    boolean[] recharge, int[] spellSlots, Map<String, Integer> perDayActions,
                    String passives, int legendaryActions, int legendaryResistances) {
        super(_displayName, dex, _rollInit(initBonus, _displayName));
        _statBlockName = statBlockName;
        _INIT_BONUS = initBonus;
        _MAX_HP = _currentHp = hp;
        _AUTO_HEAL = autoheal;
        _damageResponses = damages;
        _recharge = recharge;
        _spellSlots = spellSlots;
        _perDayActions = perDayActions;
        _PASSIVES = passives;
        _MAX_LEGENDARY_ACTIONS = _legendaryActions = legendaryActions;
        _legendaryResistances = legendaryResistances;
        _bloodied = false;
    }

    private static int _rollInit(int modifier, String _name) {
        int initiative = DndUtils.rollDice(1, 20) + modifier;
        System.out.println("Created "+_name+" with initiative "+initiative);
        return initiative;
    }

    public void changeHp(int change) {
        if (change >= 0) {
            _currentHp += change;
            if (_currentHp > _MAX_HP) {
                _currentHp = _MAX_HP;
            }
            if (_bloodied && _currentHp >= (_MAX_HP / 2)) {
                _bloodied = false;
                System.out.println(_NAME+" is no longer bloodied!");
            }
            return;
        }

        // Otherwise, change is negative. Check temp HP.
        if (_tempHp > 0) {
            _tempHp += change;
            if (_tempHp < 0) {
                _currentHp += _tempHp; // excess damage rolls over
                _tempHp = 0;
            }
        } else {
            _currentHp += change;
        }

        if (_currentHp < (_MAX_HP / 2)) {
            if (!_bloodied) {
                _bloodied = true;
                System.out.println(_NAME+" is bloodied!");
            }
            _checkDeath(); // no need to check death if conditional isn't true
        }

    }

    private void _autoheal() {
        if (_AUTO_HEAL > 0 && getYesNo("Does "+_NAME+" autoheal this round?")) {
            _currentHp += _AUTO_HEAL;
        }
    }

    private boolean _checkDeath() {
        if (_currentHp <= 0) {
            _currentHp = 0;
            _dead = true;
            System.out.println(_NAME+ " died!");
        }
        return _dead;
    }

    public void checkLegendaries() {
        int before = _legendaryActions;
        if (_legendaryActions > 0) {
            // Use up legendary actions, up to the max remaining.
            // Repeat until you get a valid number.
            while (true) {
                _legendaryActions -= getInt("How many legendary actions does "+_NAME+" use?");
                if (_legendaryActions >= 0) {
                    break;
                }
                _legendaryActions = before;
                System.out.println("Not enough legendary actions remaining!");
            }
        }
        if (_legendaryResistances > 0 && getYesNo("Does "+_NAME+" use a legendary resistance?")) {
            _legendaryActions -= 1;
        }
    }

    private void _checkActions() {
        for (var actionEntry : _perDayActions.entrySet()) {
            if (actionEntry.getValue() > 0) {
                String actionName = actionEntry.getKey();
                int before = actionEntry.getValue();
                while (true) {
                    int numUsed = getInt("How many uses of "+actionName+" this turn?");
                    if (numUsed <= before) {
                        actionEntry.setValue(before - numUsed);
                        break;
                    }
                    System.out.println("Not enough left!");
                }
            }
        }
    }

    private void _checkRecharge() {
        int d6 = DndUtils.rollDice(1, 6); // 1, 2, 3, 4, 5, or 6
        if (_recharge[d6]) {
            System.out.println("> "+_NAME+"'s ability DOES recharge!");
        } else {
            System.out.println("> "+_NAME+"'s ability does NOT recharge!");
        }

    }

    private void _useSlot(int lvl) {
        if (_spellSlots[lvl - 1] < 1) {
            System.out.println("No slots of that level left!");
            return;
        }

        _spellSlots[lvl - 1] -= 1;
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 9; i++) {
            int numOfLevel = _spellSlots[i - 1];
            if (numOfLevel > 0) {
                sb.append("Level ");
                sb.append(i);
                sb.append(" Slots: ");
                sb.append(numOfLevel);
                sb.append("\n");
            }
        }
        System.out.println(sb.toString());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // Add HP
        sb.append(_currentHp);
        sb.append("/");
        sb.append(_MAX_HP);
        sb.append(" HP left, plus ");
        sb.append(_tempHp);
        sb.append(" temp HP\n");

        // Add conditions
        sb.append(getConditionsAsString());
        sb.append("\n");

        // Add actions left
        for (var entry : _perDayActions.entrySet()) {
            String actionName = entry.getKey();
            int usesLeft = entry.getValue();
            sb.append(actionName);
            sb.append(" uses left: ");
            sb.append(usesLeft);
            sb.append("\n");
        }

        // Add spell slots left
        for (int lvl = 1; lvl <= 9; lvl++) {
            int numOfLevel = _spellSlots[lvl - 1];
            if (numOfLevel > 0) {
                sb.append("Level ");
                sb.append(lvl);
                sb.append(" Slots: ");
                sb.append(numOfLevel);
                sb.append("\n");
            }
        }

        // Add legendary resistances left
        if (_legendaryResistances > 0) {
            sb.append(_legendaryResistances);
            sb.append(" use(s) of Legendary Resistance left");
        }

        return sb.toString();
    }

    @Override
    public Map<String, String> takeTurn() {
        System.out.println(this);
        if (!_PASSIVES.equals("")) {
            System.out.println(_PASSIVES);
        }
        _legendaryActions = _MAX_LEGENDARY_ACTIONS;
        _checkRecharge();
        _checkActions();
        _autoheal();
        if (_maxEntry(_spellSlots) > 0) {
            while (true) {
                int slotLevel = getInt("Input slot level used, or 'Enter' to stop:","",-1);
                if (slotLevel == -1) {
                    break;
                }
                _useSlot(slotLevel);
            }
        }
        if (getYesNo("Gained temp HP?")) {
            _tempHp = getInt("How much?");
        }
        if (getYesNo("Healed?")) {
            changeHp(getInt("How much?"));
        }
        return _getDamage();
    }

    private int _maxEntry(int[] a) {
        int max = Integer.MIN_VALUE;
        for (int num : a) {
            if (num > max) {
                max = num;
            }
        }
        return max;
    }

    // Legendary
    public static List<Monster> monstersFromName(Map<String, Integer> namesAndNumbers) 
                                                            throws IOException, CsvException {
        List<List<String>> rows = CsvUtils.readLinesMatchingCol("monster_list.csv", 
                                                    0, namesAndNumbers.keySet());
        List<Monster> monsters = new ArrayList<>();                                            
        for (List<String> row : rows) {
            String statBlockName = row.get(0);
            int initBonus = Integer.parseInt(row.get(1));
            int dex = Integer.parseInt(row.get(2));
            int hp = Integer.parseInt(row.get(3));
            Map<DamageResponse,String[]> damageResponses = _getDamageResponses(row);            
            int autoheal = Integer.parseInt(row.get(7));
            int[] spellSlots = _getSlots(row.get(8));
            boolean[] recharge = _getRecharge(row.get(9));
            Map<String, Integer> perDayActions = _getActions(row.get(10));
            String passives = row.get(11);
            int[] legendaries = _getLegendaries(row.get(12));
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

    private static Map<DamageResponse,String[]> _getDamageResponses(List<String> row) {
        Map<DamageResponse,String[]> damageMap = new HashMap<>();
        damageMap.put(DamageResponse.VULNERABILITIES, row.get(4).split(";"));
        damageMap.put(DamageResponse.RESISTANCES, row.get(5).split(";"));
        damageMap.put(DamageResponse.IMMUNITIES, row.get(6).split(";"));
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
                int index = Integer.parseInt(num);
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
