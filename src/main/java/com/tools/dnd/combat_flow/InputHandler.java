package com.tools.dnd.combat_flow;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.tools.dnd.util.Enums;

public class InputHandler {
    public boolean getYesNo(String prompt) {
        String answer = getString(prompt + " (Y/N)").toLowerCase();
        if (answer.toLowerCase().equals("y")) {
            return true;
        }
        if (answer.toLowerCase().equals("n")) {
            return false;
        }
        System.out.println("Sorry, couldn't understand that!");
        return getYesNo(prompt);
    }

    @SuppressWarnings("resource")
    public String getString(String prompt) {
        Scanner in = new Scanner(System.in);
        System.out.print(prompt+" ");
        return in.nextLine();
    }

    public String[] getArray(String prompt) {
        String answer = getString(prompt+" (Enter comma-separated list.)");
        if (answer.contains(",")) {
            return answer.split(",");
        }
        return new String[] {answer};
    }

    public int getInt(String prompt) {
        try {
            String answer = getString(prompt);
            return Integer.parseInt(answer);
        } catch (NumberFormatException e) {
            System.out.println("Sorry, couldn't understand that!");
            return getInt(prompt);
        }
    }

    public Integer getInt(String prompt, String exception, int sentinelValue) {
        String answer = getString(prompt);
        if (answer.equals(exception)) {
            return sentinelValue;
        }
        try {
            return Integer.parseInt(answer);
        } catch (NumberFormatException e) {
            System.out.println("Sorry, couldn't understand that!");
            return getInt(prompt, exception, sentinelValue);
        }
    }
    
    public String getIntString(String prompt) {
        int answer = getInt(prompt);
        return Integer.toString(answer);
    }

    public <E extends Enum<E>> E getEnum(Class<E> clazz, String prompt) {
        String answer = getString(prompt);
        return Enums.evaluateType(clazz, answer);
    }
    
    public Map<String, Integer> getMap(String keyPrompt, String valPrompt, String addAnotherPrompt) {
        Map<String, Integer> returnMap = new HashMap<>();
        do {
            _putMapEntry(returnMap, keyPrompt, valPrompt);
        } while (getYesNo(addAnotherPrompt));
        return returnMap;
    }

    private void _putMapEntry(Map<String, Integer> map, String keyPrompt, String valPrompt) {
        try {
            String key = getString(keyPrompt);
            int val = getInt(valPrompt);
            map.put(key, val);
        } catch (Exception e) {
            System.out.println("Sorry, couldn't understand that!");
            _putMapEntry(map, keyPrompt, valPrompt);
        }
    }



}
