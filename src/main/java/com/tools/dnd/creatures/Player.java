package com.tools.dnd.creatures;

import static com.tools.dnd.util.AskUtils.getArray;
import static com.tools.dnd.util.AskUtils.getInt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.opencsv.exceptions.CsvException;
import com.tools.dnd.util.AskUtils;
import com.tools.dnd.util.CsvUtils;

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

    public static List<Player> createParty(String campaignName) throws IllegalStateException, IOException, CsvException {
        String[] absentPeople = getArray("Who's missing?");
        for (int i = 0; i < absentPeople.length; i++) {
            absentPeople[i] = absentPeople[i].strip();
        }
        return _playersFromCampaign(campaignName, absentPeople);
    }

    private static List<Player> _playersFromCampaign(String campaignName, String[] absentPeople) throws IOException, CsvException {
        List<List<String>> playersInCampaign = CsvUtils.readLinesMatchingCol("party_list.csv", 0, campaignName);
        List<List<String>> presentPlayersOnly = CsvUtils.excludeLinesMatchingCol(playersInCampaign, 1, absentPeople);

        List<Player> asPlayers = new ArrayList<>();
        for (List<String> list : presentPlayersOnly) {
            String name = list.get(1);
            int dex = Integer.parseInt(list.get(2));
            asPlayers.add(new Player(name, dex));
        }
        return asPlayers;
    }

}
