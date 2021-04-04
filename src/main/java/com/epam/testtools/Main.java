package com.epam.testtools;

import com.epam.testtools.calculator.Calculator;
import com.epam.testtools.readers.ReadExpressionFromConsole;
import com.epam.testtools.readers.ReadWriteExpressionFromFile;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static final String CONSOLE = "console";
    public static final String FILES = "files";
    public static final String FILE_IN = "file.input";
    public static final String FILE_OUT = "file.output";


    public static void main(String[] args) {

        String mode = checkTheMode();
        if (FILES.equals(mode)) {
            System.out.println(System.getProperty(FILE_IN));
            System.out.println(System.getProperty(FILE_OUT));
            ReadWriteExpressionFromFile readWriteExpressionFromFile = new ReadWriteExpressionFromFile();
            Calculator calculator = new Calculator(mode, readWriteExpressionFromFile);
            List<String> expressions = readWriteExpressionFromFile.getListOfExpressionsFromFile();
            for (String expression: expressions
                 ) {
                calculator.calculate(expression);
            }
        } else {
            ReadExpressionFromConsole readExpressionFromConsole = new ReadExpressionFromConsole();
            while (true) {
                String expression = readExpressionFromConsole.getFilteredInputFromConsole();
                Calculator calculator = new Calculator(mode, null);
                calculator.calculate(expression);
            }
        }

    }

    private static String checkTheMode() {
        System.out.println("Enter required input and output file path (e.g. \"C:\\Users\\Anatolii_Sichkar\\IdeaProjects\\java-calculator\\src\\main\\resources\\input.txt  C:\\Users\\Anatolii_Sichkar\\IdeaProjects\\java-calculator\\src\\main\\resources\\output.txt\"):");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = null;
        try {
            line = reader.readLine();
            if (line == null || line.isEmpty()) {
                return CONSOLE;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            String[] strs = line.trim().split("\\s+");
            if (validateFile(new File(strs[0])) && validateFile(new File(strs[1]))) {
                System.out.println(Arrays.asList(strs));
                System.setProperty(FILE_IN, strs[0]);
                System.setProperty(FILE_OUT, strs[1]);
            } else {
                throw new IndexOutOfBoundsException();
            }
            return FILES;
        } catch (IndexOutOfBoundsException e) {
            System.out.printf("%s incorrect input. There should be 2 file path.%n", line);
        }
        return CONSOLE;
    }

    private static boolean validateFile(File file) {
        if (file.exists()) {
            return canWrite(file);
        } else {
            try {
                file.createNewFile();
                file.delete();
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    private static boolean canWrite(File file) {
        try {
            new FileOutputStream(file, true).close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}


