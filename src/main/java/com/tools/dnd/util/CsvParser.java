package com.tools.dnd.util;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;


public class CsvParser {

    private static Set<Character> bracketChars;
    private static Map<Character, Character> bracketCharMap;
    static {
        bracketCharMap = new HashMap<>();
        bracketChars = new HashSet<>();

        bracketCharMap.put('(',')');
        bracketCharMap.put('[',']');
        bracketCharMap.put('{','}');
        bracketCharMap.put('"','"');
        bracketCharMap.put('\'','\'');

        for (var entry : bracketCharMap.entrySet()) {
            bracketChars.add(entry.getKey());
            bracketChars.add(entry.getValue());
        }
    }

    /**
     * Searches the given CSV for the given String in the given column
     * @param filetoSearch The CSV file to search
     * @param columnNumber The column to search (zero-indexed)
     * @param includeIfMatches The required column value to be returned
     * @return A list containing all the matching rows, each of which is a list of the column values
     */
    public static List<List<String>> getObjectsByCol(File fileToSearch, int columnNumber, String includeIfMatches) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileToSearch))) {
            List<List<String>> allMatches = new ArrayList<>();
            String nextLine;
            while ((nextLine = br.readLine()) != null) {
                List<String> row = _splitByCommasIgnoringQuoted(nextLine);
                if (row.get(columnNumber).equals(includeIfMatches)) {
                    allMatches.add(row);
                }
            }
            return allMatches;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Split into commas, but ignore brackets, quotes, etc.—assumes all come in pairs that open and clsoe.
     * @param line The line to split
     * @return A list of Strings representing the split String
     */
    private static List<String> _splitByCommasIgnoringQuoted(String line) {
        List<String> splitLine = new ArrayList<>();
        Stack<Character> charStack = new Stack<>();
        StringBuilder current = new StringBuilder();
        for (char ch : line.toCharArray()) {
            if (ch == ',') {
                if (charStack.size() == 0) { // if we're outside of {} rn
                    splitLine.add(current.toString().trim());
                    current = new StringBuilder();
                    continue;
                }
            } else if (bracketChars.contains(ch)) {
                if (charStack.size() == 0) {
                    charStack.push(ch);
                } else {
                    char lastChar = charStack.peek();
                    if (lastChar == '(') {
                        
                    }
                }
            }
            // CSVReaderBuilder
            // current.append(ch);     
        }
        splitLine.add(current.toString().trim());
        return splitLine;
    }

    public static void main(String[] args) {
        List<String> test = _splitByCommasIgnoringQuoted("This is one piece, this {is,another}, and 'this,makes' three");
        for (String str : test) {
            System.out.println(str);
        }
    }

}
