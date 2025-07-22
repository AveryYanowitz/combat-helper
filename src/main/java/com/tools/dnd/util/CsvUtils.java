package com.tools.dnd.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

public class CsvUtils {
    
    /**
     * Searches the given CSV for the given String in the given column
     * @param filename The name of the file, which must be in src/resources
     * @param col The column to search (zero-indexed)
     * @param includeIfMatches The required column value to be returned
     * @return A list containing all the matching rows, each of which is a list of the column values
     * @throws CsvException 
     * @throws IOException 
     */
    public static List<List<String>> readLinesMatchingCol(String filename, int col, String includeIfMatches) throws IOException, CsvException {
        return readLinesMatchingCol(filename, col, List.of(includeIfMatches));
    }

    /**
     * Searches the given CSV for the given Strings in the given column
     * @param filename The name of the file, which must be in src/resources
     * @param col The column to search (zero-indexed)
     * @param includeIfMatches All accepted column values
     * @return A list containing all the matching rows, each of which is a list of the column values
     * @throws IOException - If bad things happen during the read
     * @throws CsvException - If there is a failed validator
     */

    public static List<List<String>> readLinesMatchingCol(String filename, int col, Collection<String> includeIfMatches) throws IOException, CsvException {
        File csv = new File("src/resources/"+filename);
        try (CSVReader reader = new CSVReader(new FileReader(csv))) {
            List<String[]> rows = reader.readAll();
            List<List<String>> filtered = new ArrayList<>();
            
            for (String[] row : rows) {
                if (includeIfMatches.contains(row[col])) {
                    filtered.add(Arrays.asList(row));
                }
            }
            return filtered;
        }
    }

    /**
     * Return a new list, omitting lines whose indicated column is inside excludeIfIn
     * @param listToFilter The list to filter
     * @param col The column to search (zero-indexed)
     * @param excludeIfIn The list of values to be excluded
     * @return A new list with only the rows whose <code>col</code>th String is not <code>excludeIfMatches</code> 
     *         (as defined by the <code>String.equals()</code> method)
     */
    public static List<List<String>> excludeLinesMatchingCol(List<List<String>> csvList, int col, String[] excludeIfIn) {
        List<String> excluList = Arrays.asList(excludeIfIn);
        return csvList.stream()
                    .filter((row) -> !excluList.contains(row.get(col)))
                    .toList();
    }

    /**
     * Go through the entire CSV and retrieve the given column from each row
     * @param filename The name of the CSV to search
     * @param col The column to retrieve, zero-indexed
     * @return A list matching the order of the CSV
     * @throws IOException - If bad things happen during the read
     * @throws CsvException - If there is a failed validator
     */
    public static List<String> getColFromAllRows(String filename, int col) throws IOException, CsvException {
        File csv = new File("src/resources/"+filename);
        try (CSVReader reader = new CSVReader(new FileReader(csv))) {
            List<String[]> all = reader.readAll();
            List<String> filtered = new ArrayList<>();;
            
            for (String[] row : all) {
                filtered.add(row[col]);
            }
            return filtered;
        }
    }

}
