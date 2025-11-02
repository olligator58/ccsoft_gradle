package ru.bis.cc;

/*
Размножаем FT14
 */

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Ft14Multiplier {
    private static final List<String> stringsList = new ArrayList<>();
    private static final int BEGIN_COUNTER = 100;
    private static final int NUM_OF_REPEATS = 32;
    private static final String SOURCE_CODEPAGE = "ISO8859_5"; //Cp866 для DOS, Cp1251 для Windows-1251, KOI8_R для КОИ-8
    private static final String DEST_CODEPAGE = "ISO8859_5";

    public static void main(String[] args) {
        String sourcePath = getSourceFile();
        if (sourcePath != null) {
            String destPath = getDestinationFile(sourcePath);
            Charset charsetSource = Charset.forName(SOURCE_CODEPAGE);
            Charset charsetDest = Charset.forName(DEST_CODEPAGE);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(sourcePath), charsetSource));
                 PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(destPath), charsetDest))) {
                while (reader.ready()) {
                    String line = reader.readLine();
                    stringsList.add(line);
                }

                int j = BEGIN_COUNTER;
                for (int i = 0; i < NUM_OF_REPEATS; i++) {
                    for (String line : stringsList) {
                        writer.println(modifyNumber(line, j));
                        j++;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String getSourceFile() {
        String result;
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Введите путь к файлу FT14:");
        result = keyboard.nextLine();
        try (FileInputStream fis = new FileInputStream(result)) {
        } catch (IOException e) {
            result = null;
            System.out.println("Путь к файлу введен неверно");
            ;
        } finally {
            keyboard.close();
            return result;
        }
    }

    private static String getDestinationFile(String sourceFile) {
        String sourceFileName = Path.of(sourceFile).getFileName().toString();
        int pointIndex = sourceFileName.indexOf(".");
        String destFileName = sourceFileName.substring(0, pointIndex) + "_copy." +
                sourceFileName.substring(pointIndex + 1);
        return Path.of(sourceFile).getParent().resolve(Path.of(destFileName)).toString();
    }

    private static String modifyNumber(String line, Integer i) {
        StringBuilder result = new StringBuilder(line.trim());
        StringBuilder num = new StringBuilder(i.toString());
        while (num.length() < 6) {
            num.insert(0, " ");
        }
        result.replace(28, 34, num.toString());
        while (num.length() < 10) {
            num.insert(0, " ");
        }
        result.replace(370, 380, num.toString());
        return result.toString();
    }
}

