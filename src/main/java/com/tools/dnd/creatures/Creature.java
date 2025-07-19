package com.tools.dnd.creatures;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.tools.dnd.util.AskUtils;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(prefix = "_")
public abstract class Creature {
    protected final String _NAME;
    protected Set<String> _conditions;
    protected final int _DEX, _INITIATIVE;
    protected boolean _dead;

    protected Creature(String name, int dex, int initiative) {
        this._NAME = name;
        this._DEX = dex;
        this._INITIATIVE = initiative;
        this._dead = false;
        this._conditions = new TreeSet<>();
    }

    @Override
    public String toString() {
        return _NAME;
    }

    /**
     * Add the given condition to the creature's current conditions
     * @param condition Condition to add
     * @return true if added, false if not added
     */
    public final boolean addCondition(String condition) {
        return _conditions.add(condition);
    }

    /**
     * Remove the given condition from the creature's current conditions
     * @param condition Condition to remove
     * @return true if removed, false if not removed
     */
    public final boolean removeCondition(String condition) {
        boolean wasPresent = _conditions.remove(condition);
        if (!wasPresent) {
            System.out.println("Condition "+condition+" not found.");
        }
        return wasPresent;
    }

    public final String getConditionsAsString() {
        if (_conditions.size() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder("Conditions: ");
        for (String condition : _conditions) {
            sb.append(condition);
            sb.append("; ");
        }
        String trimmedFinalSemicolon = sb.substring(0, sb.length() - 2);
        return trimmedFinalSemicolon;
    }

    /**
     * Take the creature's turn and return a Map that represents which creatures the creature damaged
     * @return A mapping of target names to the damage dealt to them
     */
    public abstract Map<String, String> takeTurn();

    /**
     * Ask the user which targets this creature damages, and how much damage each one takes
     * @return A mapping of target names to the damage dealt to them
     */
    protected final Map<String, String> _getDamage() {
        Map<String, String> targetsAndDamage = new TreeMap<>();
        String[] targets = AskUtils.getArray("Input target (or 'Enter' if none)");
        if (!targets[0].equals("")) {
            for (String target : targets) {
                target = target.strip();
                String damage = AskUtils.getString("How much damage to "+target+"?");
                targetsAndDamage.put(target, damage);
            }
        }
        return targetsAndDamage;
    }

}
