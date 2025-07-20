package com.tools.dnd.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
        File csv = new File("src/resources/"+filename);
        try (CSVReader reader = new CSVReader(new FileReader(csv))) {
            List<String[]> rows = reader.readAll();
            List<List<String>> filtered = new ArrayList<>();

            for (String[] row : rows) {
                if (row[col].equals(includeIfMatches)) {
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
    public static List<List<String>> excludeLinesByCol(List<List<String>> csvList, int col, String[] excludeIfIn) {
        List<String> excluList = Arrays.asList(excludeIfIn);
        return csvList.stream()
                    .filter((row) -> !excluList.contains(row.get(col)))
                    .toList();
    }



}
