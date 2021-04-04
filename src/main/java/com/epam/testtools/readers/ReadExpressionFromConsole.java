package com.epam.testtools.readers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ReadExpressionFromConsole {

    public final String getFilteredInputFromConsole() {
        System.out.println("Enter required expression (enter empty line to quit):");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = null;
        try {
            line = reader.readLine();
            if (line == null || line.isEmpty()) {
                throw new RuntimeException("Expression is empty");
            }
        } catch (RuntimeException e) {
            System.out.println(e.getMessage() + ". Exit from calculator.");
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }

}


