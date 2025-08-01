package com.tools.dnd.creatures;

import java.util.Map;

import com.tools.dnd.user_input.InputHandler;

public class BattleEvent extends Creature {
    private final String _EVENT_TEXT;

    public BattleEvent(String name, int dex, int initiative, String eventText) {
        super(name, dex, initiative);
        _EVENT_TEXT = eventText;
    }

    @Override
    public String toString() {
        return _NAME + ": " + _EVENT_TEXT;
    }

    @Override
    public Map<String, String> takeTurn(InputHandler input) {
        System.out.println(this);
        return Map.of();
    }
    
}
