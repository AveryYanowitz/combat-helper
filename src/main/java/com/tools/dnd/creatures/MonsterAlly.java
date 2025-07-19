package com.tools.dnd.creatures;

import java.util.Map;

public class MonsterAlly extends Monster {

    public MonsterAlly(String _statBlockName, String _displayName, int initBonus, int dex, int hp, int autoheal,
            Map<DamageResponse, String[]> damages, boolean[] recharge, int[] spellSlots,
            Map<String, Integer> perDayActions, String passives, int legendaryActions, int legendaryResistances) {
        super(_statBlockName, _displayName, initBonus, dex, hp, autoheal, damages, recharge, spellSlots, perDayActions,
                passives, legendaryActions, legendaryResistances);
    }

    @Override
    public Map<String, String> takeTurn() {
        super.takeTurn();
        return _getDamage();
    }
    
}
