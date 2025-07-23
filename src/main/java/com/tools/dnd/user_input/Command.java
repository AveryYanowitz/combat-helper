package com.tools.dnd.user_input;

@FunctionalInterface
public interface Command {
    void runCommand() throws Exception;
}
