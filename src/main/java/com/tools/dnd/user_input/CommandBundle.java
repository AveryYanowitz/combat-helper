package com.tools.dnd.user_input;

import java.util.HashMap;
import java.util.Map;

class CommandBundle {
    private record CommandInfo(String description, Command cmd) {}

    private Map<String, CommandInfo> _cmdMap;

    CommandBundle() {
        _cmdMap = new HashMap<>();
    }

    public void register(String name, String desc, Command cmd) {
        _cmdMap.put(name, new CommandInfo(desc, cmd));
    }

    public void runCommand(String name) {
        try {
            _cmdMap.get(name).cmd().runCommand();
        } catch (NullPointerException e) {
            System.out.println("Command not found. Type `!help` to print all commands.");
        } catch (Exception e) {
            System.out.println("Oops, something went wrong...");
            System.out.println(e.getMessage());
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (var entry : _cmdMap.entrySet()) {
            sb.append(entry.getKey());
            sb.append(" --- ");
            sb.append(entry.getValue().description());
            sb.append("\n");
        }
        return sb.toString();
    }

}
