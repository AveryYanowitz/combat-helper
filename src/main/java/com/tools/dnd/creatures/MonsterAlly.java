package com.tools.dnd.creatures;

import java.util.Map;

import com.tools.dnd.user_input.InputHandler;
import com.tools.dnd.util.Enums.DamageResponse;
import com.tools.dnd.util.Enums.DamageType;

public class MonsterAlly extends Monster {

    public MonsterAlly(String _statBlockName, String _displayName, int initBonus, int dex, int hp, int autoheal,
            Map<DamageResponse, DamageType[]> damages, boolean[] recharge, int[] spellSlots,
            Map<String, Integer> perDayActions, String passives, int legendaryActions, int legendaryResistances) {
        super(_statBlockName, _displayName, initBonus, dex, hp, autoheal, damages, recharge, spellSlots, perDayActions,
                passives, legendaryActions, legendaryResistances);
    }

    @Override
    public Map<String, String> takeTurn(InputHandler input) {
        super.takeTurn(input);
        return _getDamage(input);
    }
    
}
