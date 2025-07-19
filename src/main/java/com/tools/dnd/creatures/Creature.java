package com.tools.dnd.creatures;

import java.util.List;

import com.tools.dnd.util.DndUtils;

public abstract class Creature {
    protected String name, statBlockName;
    protected List<String> conditions;
    protected int dex, initiative;
    protected boolean dead;

    protected Creature(String name, int dex, int initiative) {
        this.name = name;
        this.dex = dex;
        this.initiative = initiative;
        this.dead = false;
    }

    protected Creature(String name, int dex) {
        this.name = name;
        this.dex = dex;
        this.initiative = DndUtils.scoreToModifier(dex);
        this.dead = false;
    }

    


}
