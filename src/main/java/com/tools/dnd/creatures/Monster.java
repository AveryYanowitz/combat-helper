package com.tools.dnd.creatures;

import java.util.HashMap;
import java.util.Map;

import com.tools.dnd.util.DndUtils;
import com.tools.dnd.util.Enums.DamageResponse;
import com.tools.dnd.util.Enums.DamageType;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(prefix = "_")
@EqualsAndHashCode(callSuper = true)
public class Monster extends Creature {
    private String _statBlockName;
    private final int _INIT_BONUS, _MAX_HP, _AUTO_HEAL, _MAX_LEGENDARY_ACTIONS;
    private int _currentHp, _tempHp, _legendaryActions, _legendaryResistances;
    private boolean[] _recharge;
    private int[] _spellSlots;
    private Map<String, Integer> _perDayActions;
    private Map<DamageResponse, DamageType[]> _damageResponses;
    private boolean _bloodied;
    private final String _PASSIVES;

    public Monster(String statBlockName, String _displayName, int initBonus, int dex,
                    int hp, int autoheal, Map<DamageResponse, DamageType[]> damages,
                    boolean[] recharge, int[] spellSlots, Map<String, Integer> perDayActions,
                    String passives, int legendaryActions, int legendaryResistances) {
        super(_displayName, dex, _rollInit(initBonus, _displayName));
        _statBlockName = statBlockName;
        _INIT_BONUS = initBonus;
        _MAX_HP = _currentHp = hp;
        _AUTO_HEAL = autoheal;
        _damageResponses = (damages != null) ? damages    : new HashMap<>();
        _recharge = (recharge != null)       ? recharge   : new boolean[] {false, false, false, false, false, false};
        _spellSlots = (spellSlots != null)   ? spellSlots : new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0};
        _perDayActions = (perDayActions != null) ? perDayActions : new HashMap<>();
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

    public void addConditions(String[] conditionsToAdd) {
        for (String condition : conditionsToAdd) {
            _conditions.add(condition);
        }
    }

    public void removeConditions(String[] conditionsToRemove) {
        for (String condition : conditionsToRemove) {
            if (!_conditions.remove(condition)) {
                System.out.println("Condition "+condition+" not found on monster "+_NAME);
            }
        }
    }

    /**
     * Changes HP by the given amount, taking temp HP into account
     * @param change Amount to change by; positive if healing, negative if damage
     */
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

    public void changeHp(String damageStr) {
        Map<DamageType, Integer> damageMap = DndUtils.parseDamage(damageStr);
        int totalDamage = 0;
        for (var entry : damageMap.entrySet()) {
            DamageType dt = entry.getKey();
            int dmg = entry.getValue();
            switch (getResponseTo(dt)) {
                case VULNERABLE:
                    dmg *= 2;
                    break;
                case RESISTANT:
                    dmg /= 2;
                    break;
                case IMMUNE:
                    dmg = 0;
                    break;
                case DEFAULT:
                    break;
            }
            totalDamage += dmg;
        }
        changeHp(-totalDamage);
    }

    public void setTempHp(int tempHp) {
        _tempHp = tempHp;
    }

    public void setLegendaryResistances(int n) {
        if (n < 0) {
            _legendaryResistances = 0;
        } else {
            _legendaryResistances = n;
        }
    }

    public void setCurrentLegendaryActions(int n) {
        if (n < 0) {
            _legendaryActions = 0;
        } else {
            _legendaryActions = n;
        }
    }

    /** Asks user if monster autoheals */
    private void _autoheal() {
        if (_AUTO_HEAL > 0 && input.getYesNo("Does "+_NAME+" autoheal this round?")) {
            _currentHp += _AUTO_HEAL;
        }
    }

    /** Checks if dead and updates _dead accordingly
     * @return _dead after being updated
     */
    private boolean _checkDeath() {
        if (_currentHp <= 0) {
            _currentHp = 0;
            _dead = true;
            System.out.println(_NAME+ " died!");
        }
        return _dead;
    }

    /** Asks user if monster uses legendary actions */
    public void checkLegendaries() {
        int before = _legendaryActions;
        if (_legendaryActions > 0) {
            // Use up legendary actions, up to the max remaining.
            // Repeat until you get a valid number.
            while (true) {
                _legendaryActions -= input.getInt("How many legendary actions does "+_NAME+" use?");
                if (_legendaryActions >= 0) {
                    break;
                }
                _legendaryActions = before;
                System.out.println("Not enough legendary actions remaining!");
            }
        }
        if (_legendaryResistances > 0 && input.getYesNo("Does "+_NAME+" use a legendary resistance?")) {
            _legendaryActions -= 1;
        }
    }

    /** Asks user if monster uses any per-day actions */
    private void _checkActions() {
        for (var actionEntry : _perDayActions.entrySet()) {
            if (actionEntry.getValue() > 0) {
                String actionName = actionEntry.getKey();
                int before = actionEntry.getValue();
                while (true) {
                    int numUsed = input.getInt("How many uses of "+actionName+" this turn?");
                    if (numUsed <= before) {
                        actionEntry.setValue(before - numUsed);
                        break;
                    }
                    System.out.println("Not enough left!");
                }
            }
        }
    }

    /** Checks if ability recharges and notifies user */
    private void _checkRecharge() {
        int d6 = DndUtils.rollDice(1, 6); // 1, 2, 3, 4, 5, or 6
        if (_recharge[d6]) {
            System.out.println("> "+_NAME+"'s ability DOES recharge!");
        } else {
            System.out.println("> "+_NAME+"'s ability does NOT recharge!");
        }

    }

    /**
     * Consumes a spell slot of the given level if available
     * @param lvl The level of slot to consume
     */
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
    /** Takes the monster's turn
     * @return A mapping from the names of the creature(s) the monster attacks 
     *         to the string representing the damage they take
     */
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
                int slotLevel = input.getInt("Input slot level used, or 'Enter' to stop:","",-1);
                if (slotLevel == -1) {
                    break;
                }
                _useSlot(slotLevel);
            }
        }
        if (input.getYesNo("Gained temp HP?")) {
            _tempHp = input.getInt("How much?");
        }
        if (input.getYesNo("Healed?")) {
            changeHp(input.getInt("How much?"));
        }
        return _getDamage();
    }

    /**
     * Finds the max entry in the array
     * @param a The array to check
     * @return The max value (not its index)
     */
    private int _maxEntry(int[] a) {
        int max = Integer.MIN_VALUE;
        for (int num : a) {
            if (num > max) {
                max = num;
            }
        }
        return max;
    }

    /**
     * Finds whether the monster is vulnerable, resistant, or immune to the given damage type
     * @param type The damage type to check
     * @return A DamageResponse enum indicating the monster's "response" that damage
     */
    public DamageResponse getResponseTo(DamageType type) {
        for (DamageType v : getVulnerabilities()) {
            if (type == v) {
                return DamageResponse.VULNERABLE;
            }
        }
        for (DamageType r : getResistances()) {
            if (type == r) {
                return DamageResponse.RESISTANT;
            }            
        }
        for (DamageType i : getImmunities()) {
            if (type == i) {
                return DamageResponse.IMMUNE;
            }            
        }
        return DamageResponse.DEFAULT;
    }

    /**
     * Get the damage types the monster is vulnerable to
     * @return An array of DamageType objects
     */
    public DamageType[] getVulnerabilities() {
        return _damageResponses.get(DamageResponse.VULNERABLE);
    }

    /**
     * Get the damage types the monster is resistant to
     * @return An array of DamageType objects
     */

    public DamageType[] getResistances() {
        return _damageResponses.get(DamageResponse.RESISTANT);
    }

    /**
     * Get the damage types the monster is immune to
     * @return An array of DamageType objects
     */

    public DamageType[] getImmunities() {
        return _damageResponses.get(DamageResponse.IMMUNE);
    }
}
