package com.tools.dnd.creatures;

import java.util.Map;

import com.tools.dnd.util.AskUtils;

import lombok.Getter;
import lombok.Setter;

public class Player extends Creature {
    @Getter
    @Setter
    private boolean _dying;

    protected Player(String _NAME, int dex, int initiative) {
        super(_NAME, dex, initiative);
        _dying = false;
    }
    

    @Override
    public Map<String, String> takeTurn() {
        System.out.println(getConditionsAsString());
        if (!_survived() || _dying) {
            return null;
        }
        return super._getDamage();
    }

    private boolean _survived() {
        if (_dead && AskUtils.getYesNo("Is "+_NAME+" still _dead?")) {
            return false;
        } else if (!_dead && AskUtils.getYesNo("Is "+_NAME+" _dead?")) {
            _dying = false;
            _dead = true;
            return false;
        }
        
        if (_dying && AskUtils.getYesNo("Has "+_NAME+" been healed?")) {            
            _dying = false;
        } else if (!_dying && AskUtils.getYesNo("Is "+_NAME+" at 0 HP?")) {
            _dying = true;
        }

        // We have not returned false yet, so we're alive
        _dead = false;
        return true;
    }
    
}
