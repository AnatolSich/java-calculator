package com.epam.testtools.readers;


import lombok.Getter;
import lombok.Setter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.epam.testtools.Main.FILE_IN;
import static com.epam.testtools.Main.FILE_OUT;


@Getter
@Setter
public class ReadWriteExpressionFromFile {

    private String inputFileName;
    private String outputFileName;

    public ReadWriteExpressionFromFile() {
        this.inputFileName = System.getProperty(FILE_IN);
        this.outputFileName = System.getProperty(FILE_OUT);
    }

    public void writeResultToFile(String result) {
        BufferedWriter outputFileWriter = null;
        try {
            File file = new File(outputFileName);
            System.out.println("Output file " + outputFileName + " exists = " + file.exists());
            if (!file.exists()) {
                System.out.println("Created output file " + outputFileName + " = " + file.createNewFile());
            }

            outputFileWriter = new BufferedWriter(new FileWriter(file, true));
            outputFileWriter.append(result);

        } catch (IOException e) {
            System.out.println(String.format("An error occurred while writing to file: %s", e));
            e.printStackTrace();
        } finally {
            try {
                if (outputFileWriter != null)
                    outputFileWriter.close();
            } catch (Exception ex) {
                System.out.println("Error in closing the BufferedWriter" + ex);
            }
        }
    }

    public List<String> getListOfExpressionsFromFile() {

        List<String> listExpressionsFromFile = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(inputFileName))) {
            listExpressionsFromFile = stream
                    .filter(line -> line != null && line.length() > 0)
                    .map(String::trim)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listExpressionsFromFile;
    }


}

