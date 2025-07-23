package com.tools.dnd.creatures;

import static com.tools.dnd.util.AskUser.getInt;

import java.util.HashMap;
import java.util.Map;

import com.tools.dnd.util.AskUser;

import lombok.Getter;
import lombok.Setter;

public class Player extends Creature {
    @Getter
    @Setter
    private boolean _dying;

    public Player(String name, int dex, int initiative) {
        super(name, dex, initiative);
        _dying = false;
    }

    public Player(String name, int dex) {
        this(name, dex, getInt("What is "+name+"'s initiative?"));
    }
    

    @Override
    public Map<String, String> takeTurn() {
        System.out.println(getConditionsAsString());
        if (!_survived() || _dying) {
            return new HashMap<>();
        }
        return super._getDamage();
    }

    private boolean _survived() {
        if (_dead && AskUser.getYesNo("Is "+_NAME+" still _dead?")) {
            return false;
        } else if (!_dead && AskUser.getYesNo("Is "+_NAME+" _dead?")) {
            _dying = false;
            _dead = true;
            return false;
        }
        
        if (_dying && AskUser.getYesNo("Has "+_NAME+" been healed?")) {            
            _dying = false;
        } else if (!_dying && AskUser.getYesNo("Is "+_NAME+" at 0 HP?")) {
            _dying = true;
        }

        // We have not returned false yet, so we're alive
        _dead = false;
        return true;
    }

}
