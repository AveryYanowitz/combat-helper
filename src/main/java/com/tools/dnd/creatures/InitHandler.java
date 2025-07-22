package com.tools.dnd.creatures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InitHandler {
    private List<Creature> _initList;

    public InitHandler(Creature... creatures) {
        _initList = Arrays.asList(creatures);
        _initList.sort(null);
    }


    
}
