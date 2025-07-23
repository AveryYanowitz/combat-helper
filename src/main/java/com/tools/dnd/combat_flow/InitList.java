package com.tools.dnd.combat_flow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.tools.dnd.creatures.Creature;
import com.tools.dnd.creatures.Monster;
import com.tools.dnd.creatures.Player;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(prefix = "_")
public class InitList {
    private List<Creature> _initList;
    private int _currentIndex, _roundsCompleted;
    private String _outcome;

    @SafeVarargs
    public InitList(List<? extends Creature>... creatureLists) {
        _initList = new ArrayList<>();
        for (List<? extends Creature> lc : creatureLists) {
            for (Creature c : lc) {
                _initList.add(c);
            }
        }
        _initList.sort((c1, c2) -> c1.compareTo(c2)); // sorts by initiative, breaking ties with dex
        _currentIndex = _roundsCompleted = 0;
        System.out.println(this); // copy-pastable init list
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Copy-Pastable Initiative:\n\n");
        for (Creature creature : _initList) {
            sb.append("**");
            sb.append(creature.getNAME());
            sb.append("**: ");
            sb.append(creature.getInitiative());
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Calls <code>takeTurn()</code> on the next element in the initiative order,
     * then returns that element (primarily for debugging purposes)
     * @return The Creature whose turn was just taken
     */
    public Creature nextTurn() {
        Creature nextInLine = _initList.get(_currentIndex);
        Map<String, String> targetsToDamage = nextInLine.takeTurn();
        _logDamage(targetsToDamage);

        _currentIndex++;
        if (_currentIndex == _initList.size()) {
            _currentIndex = 0;
            _roundsCompleted++;
        }
        return nextInLine;
    }

    public int addCreature(Creature newCreature) {
        for (int i = 0; i < _initList.size(); i++) {
            Creature current = _initList.get(i);
            if (current.compareTo(newCreature) > 0) {
                _initList.add(i, newCreature);
                return i;
            }
        }
        _initList.add(newCreature);
        return _initList.size() - 1;
    }

    public boolean combatDone() {
        boolean allPlayersDead = true;
        boolean allMonstersDead = true;

        for (Creature creature : _initList) {
            if (!creature.isDead()) {
                if (creature instanceof Player) {
                    allPlayersDead = false;
                } else if (creature instanceof Monster) {
                    allMonstersDead = false;
                }
            }
        }
        if (allPlayersDead) {
            _outcome = "The party lost!";
            return true;
        } else if (allMonstersDead) {
            _outcome = "The party won!";
            return true;
        }
        return false;
    }

    private void _logDamage(Map<String, String> targetsToDamage) {
        for (Creature creature : _initList) {
            String name = creature.getNAME();
            if (creature instanceof Monster && targetsToDamage.containsKey(name)) {
                String damageStr = targetsToDamage.get(name);
                ((Monster) creature).changeHp(damageStr);
            }
        }
    }
    
}
