package com.tools.dnd.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.tools.dnd.creatures.Creature;
import com.tools.dnd.creatures.Monster;
import com.tools.dnd.creatures.MonsterAlly;
import com.tools.dnd.creatures.Player;
import com.tools.dnd.user_input.InputHandler;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(prefix = "_")
public class InitList {
    private List<Creature> _initList;
    private int _currentIndex, _roundsCompleted;
    private String _outcome;
    private boolean _combatDone;
    private InputHandler _input;

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
        _combatDone = false;
        _input = new InputHandler(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Copy-Pastable Initiative:\n\n");
        for (Creature creature : _initList) {
            sb.append("**");
            sb.append(creature.getName());
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
        Map<String, String> targetsToDamage = nextInLine.takeTurn(_input);
        _logDamage(targetsToDamage);

        _currentIndex++;
        if (_currentIndex == _initList.size()) {
            _currentIndex = 0;
            _roundsCompleted++;
        }
        return nextInLine;
    }

    public Creature getCreature(String name) {
        for (Creature creature : _initList) {
            if (creature.getName().equals(name)) {
                return creature;
            }
        }
        return null;
    }

    public Monster getMonster(String name) {
        Creature cr = getCreature(name);
        if (cr instanceof Monster) {
            return (Monster) cr;
        }
        return null;
    }

    public Player getPlayer(String name) {
        Creature cr = getCreature(name);
        if (cr instanceof Player) {
            return (Player) cr;
        }
        return null;
    }

    public String getNameAtIndex(int i) {
        return _initList.get(i).getName();
    }

    public void addCreature(Creature newCreature) {
        for (int i = 0; i < _initList.size(); i++) {
            Creature current = _initList.get(i);
            if (current.compareTo(newCreature) > 0) { // if current goes after newCreature in initiative
                _initList.add(i, newCreature);
                return;
            }
        }
        _initList.add(newCreature);
    }

    public void addCreatures(Collection<? extends Creature> newCreatures) {
        for (Creature creature : newCreatures) {
            addCreature(creature);
        }
    }

    public int size() {
        return _initList.size();
    }

    public boolean isCombatDone() {
        if (_combatDone) {
            return true;
        }
        boolean allPlayersDead = true;
        boolean allMonstersDead = true;

        for (Creature creature : _initList) {
            if (!creature.isDead()) {
                if (creature instanceof Player || creature instanceof MonsterAlly) {
                    allPlayersDead = false;
                } else if (creature instanceof Monster) {
                    allMonstersDead = false;
                }
            }
        }
        if (allPlayersDead) {
            _outcome = "Combat is over—the party lost!";
            _combatDone = true;
            return true;
        } else if (allMonstersDead) {
            _outcome = "Combat is over—the party won!";
            _combatDone = true;
            return true;
        }
        return false;
    }

    public void endEarly(boolean isError) {
        _combatDone = true;
        if (isError) {
            _outcome = "Combat ended because of error";
            System.out.println();
            System.out.println("-- ERROR ENCOUNTERED: MONSTERS LISTED BELOW -- ");
            for (Creature creature : _initList) {
                if (creature instanceof Monster) {
                    System.out.println(creature.getName());
                    System.out.println(creature);
                    System.out.println();
                }
            }
        } else {
            _outcome = "User ended combat early";
        }
    }

    public void reOrder(String[] newOrder) {
        int oldSize = _initList.size();
        int newSize = newOrder.length;
        if (oldSize != newSize) {
            throw new IllegalArgumentException(newSize+" does not equal expected size "+oldSize);
        }
        List<Creature> newList = new ArrayList<>(newSize);
        for (int i = 0; i < newSize; i++) {
            Creature creature = getCreature(newOrder[i]);
            if (creature == null) {
                throw new NoSuchElementException("Could not find creature "+newOrder[i]);
            }
            newList.add(creature);
        }
        _initList = newList;
    }

    private void _logDamage(Map<String, String> targetsToDamage) {
        List<Creature> deadMons = new ArrayList<>();
        for (Creature creature : _initList) {
            String name = creature.getName();
            if (creature instanceof Monster mon && targetsToDamage.containsKey(name)) {
                String damageStr = targetsToDamage.get(name);
                mon.changeHp(damageStr);
                if (mon.isDead()) {
                    deadMons.add(mon);
                }
            }
        }
        _initList.removeAll(deadMons);
    }
    
}
