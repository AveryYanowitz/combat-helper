package com.tools.dnd.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class AskUser {
    
    public static boolean getYesNo(String prompt) {
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
    public static String getString(String prompt) {
        Scanner in = new Scanner(System.in);
        System.out.print(prompt+" ");
        return in.nextLine();
    }

    public static String[] getArray(String prompt) {
        String answer = getString(prompt+" (Enter comma-separated list.)");
        if (answer.contains(",")) {
            return answer.split(",");
        }
        return new String[] {answer};
    }

    public static int getInt(String prompt) {
        try {
            String answer = getString(prompt);
            return Integer.parseInt(answer);
        } catch (NumberFormatException e) {
            System.out.println("Sorry, couldn't understand that!");
            return getInt(prompt);
        }
    }

    public static Integer getInt(String prompt, String exception, int sentinelValue) {
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
    
    public static String getIntString(String prompt) {
        int answer = getInt(prompt);
        return Integer.toString(answer);
    }

    public static <E extends Enum<E>> E getEnum(Class<E> clazz, String prompt) {
        String answer = getString(prompt);
        return Enums.evaluateType(clazz, answer);
    }
    
    public static Map<String, Integer> getMap(String keyPrompt, String valPrompt, String addAnotherPrompt) {
        Map<String, Integer> returnMap = new HashMap<>();
        do {
            _putMapEntry(returnMap, keyPrompt, valPrompt);
        } while (getYesNo(addAnotherPrompt));
        return returnMap;
    }

    private static void _putMapEntry(Map<String, Integer> map, String keyPrompt, String valPrompt) {
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
