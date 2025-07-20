package com.tools.dnd.creatures;

import static com.tools.dnd.util.AskUtils.getArray;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.opencsv.bean.CsvToBeanBuilder;
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

    public static List<Player> createParty(String campaignName) throws IllegalStateException, FileNotFoundException {
        String[] absentPeople = getArray("Who's missing?");
        for (int i = 0; i < absentPeople.length; i++) {
            absentPeople[i] = absentPeople[i].strip();
        }
        return _playersFromCampaign(campaignName, absentPeople);
    }

    private static List<Player> _playersFromCampaign(String campaignName, String[] absentPeople) 
                                    throws IllegalStateException, FileNotFoundException {
        Stream<Player> beans = new CsvToBeanBuilder<Player>(new FileReader(new File("src/resources/party_list.csv")))
                            .withType(Player.class).build().stream();
        Stream<Player> noAbsent = beans.filter((player) -> {
            for (String absent : absentPeople) {
                String name = player.getNAME();
                if (absent.equals(name)) {
                    return false;
                }
            }
            return true;
        });
        return noAbsent.toList();
    }
    public static void main(String[] args) throws IllegalStateException, FileNotFoundException {
        createParty("Adeo");
    }
    
}
