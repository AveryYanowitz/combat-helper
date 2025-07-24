package com.tools.dnd.creatures;

import java.util.HashMap;
import java.util.Map;

import com.tools.dnd.user_input.InputHandler;

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

    public Player(String name, int dex, InputHandler input) {
        this(name, dex, input.getInt("What is "+name+"'s initiative?"));
    }
    

    @Override
    public Map<String, String> takeTurn(InputHandler input) {
        System.out.println(getConditionsAsString());
        if (!_survived(input) || _dying) {
            return new HashMap<>();
        }
        return super._getDamage(input);
    }

    private boolean _survived(InputHandler input) {
        if (_dead && input.getYesNo("Is "+_NAME+" still _dead?")) {
            return false;
        } else if (!_dead && input.getYesNo("Is "+_NAME+" _dead?")) {
            _dying = false;
            _dead = true;
            return false;
        }
        
        if (_dying && input.getYesNo("Has "+_NAME+" been healed?")) {            
            _dying = false;
        } else if (!_dying && input.getYesNo("Is "+_NAME+" at 0 HP?")) {
            _dying = true;
        }

        // We have not returned false yet, so we're alive
        _dead = false;
        return true;
    }

}
