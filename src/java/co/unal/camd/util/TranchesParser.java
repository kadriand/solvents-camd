package co.unal.camd.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static co.unal.camd.util.CdkUtils.smilesToUnique;

public class TranchesParser {

    public static void main(String[] args) {
        String fileName = "D:/GoogleDrive/UNAL/Maestría/TRABAJO FINAL/AI.csv";
        File file = new File(fileName);

        // this gives you a 2-dimensional array of strings
        List<String> lines = new ArrayList<>();
        Scanner inputStream;

        try {
            inputStream = new Scanner(file);

            if (inputStream.hasNext())
                inputStream.next();
            while (inputStream.hasNext()) {
                String line = inputStream.next();
                String[] values = line.split("\\s*,\\s*");
                // this adds the currently parsed line to the 2-dimensional string array
                lines.add(values[1]);
            }

            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // the following code lets you iterate through the 2-dimensional array
        int lineNo = 1;
        for (String line : lines) {
            int columnNo = 1;
            String uniqueSmiles = smilesToUnique(line);
            System.out.println(String.format("%d. Smiles %s, absolute %s, match? %s", lineNo, line, uniqueSmiles, line.equals(uniqueSmiles)));
            //            System.out.println("Line " + lineNo + " Column " + columnNo + ": ." + line + ".");
            columnNo++;
            lineNo++;
        }
    }

}