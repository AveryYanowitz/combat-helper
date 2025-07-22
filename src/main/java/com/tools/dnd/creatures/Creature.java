package com.tools.dnd.creatures;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.opencsv.bean.CsvBindByName;
import com.tools.dnd.util.AskUser;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(prefix = "_")
@EqualsAndHashCode
public abstract class Creature implements Comparable<Creature> {
    @CsvBindByName(column = "Name", required = true)
    protected final String _NAME;
    @CsvBindByName(column = "Dex", required = true)
    protected final int _DEX;
    protected final int _initiative;
    protected Set<String> _conditions;
    protected boolean _dead;

    protected Creature(String name, int dex, int initiative) {
        this._NAME = name;
        this._DEX = dex;
        this._initiative = initiative;
        this._dead = false;
        this._conditions = new HashSet<>();
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
        Map<String, String> targetsAndDamage = new HashMap<>();
        String[] targets = AskUser.getArray("Input target (or 'Enter' if none)");
        if (!targets[0].equals("")) {
            for (String target : targets) {
                target = target.strip();
                String damage = AskUser.getString("How much damage to "+target+"?");
                targetsAndDamage.put(target, damage);
            }
        }
        return targetsAndDamage;
    }

    @Override
    // InitList uses a TreeSet, which sorts from low to high,
    // so we have to reverse the default Integer.compare
    public final int compareTo(Creature other) {
        if (_initiative == other._initiative) {
            return Integer.compare(other._DEX, _DEX);
        } else {
            return Integer.compare(other._initiative, _initiative);
        }
    }

}
