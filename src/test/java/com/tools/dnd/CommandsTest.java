package com.tools.dnd;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.stefanbirkner.systemlambda.SystemLambda;
import com.tools.dnd.core.InitList;
import com.tools.dnd.creatures.SpawnPoint;
import com.tools.dnd.user_input.InputHandler;

public class CommandsTest {
    
    private InitList initList;
    private InputHandler inputHandler;

    @BeforeEach
    void setup() throws Exception {
        SystemLambda.withTextFromSystemIn("n","n").execute(() -> {
            Map<String, Integer> monsterMap = Map.of("Commoner", 1, "Kraken", 1);
            initList = new InitList(SpawnPoint.monstersFromName(monsterMap));
        });
        inputHandler = new InputHandler(initList);
    }

    @Test
    void addCommand() {

    }

    @Test
    void endCommand() {

    }

    @Test
    void condCommand() {

    }

    @Test
    void hpCommand() {

    }

    @Test
    void initCommand() {

    }

    @Test
    void lrCommand() {

    }

    private void _runCommand(String cmd, String response) throws Exception {
        SystemLambda.withTextFromSystemIn(cmd).execute(() -> {
            initList.nextTurn();
        });
    }

}
