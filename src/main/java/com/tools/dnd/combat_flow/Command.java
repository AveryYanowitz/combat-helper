package com.tools.dnd.combat_flow;

@FunctionalInterface
public interface Command {
    void runCommand() throws Exception;
}
