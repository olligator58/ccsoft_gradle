package ru.bis.cc;

/*
Размножаем FT14
 */

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Ft14MultAddRef {
    private enum RefType {
        MT103,
        MT202
    }

    private static final List<String> stringsList = new ArrayList<>();
    private static final int BEGIN_COUNTER = 100;
    private static final int NUM_OF_REPEATS = 8;
    private static final RefType REF_TYPE = RefType.MT103;
    private static final boolean IS_MIS01s = false;
    private static final String SOURCE_CODEPAGE = "ISO8859_5"; //Cp866 для DOS, Cp1251 для Windows-1251, KOI8_R для КОИ-8
    private static final String DEST_CODEPAGE = "ISO8859_5";
    private static final boolean ADD_REF = true;
    private static final int REF_OFFSET = 2575;
    private static final String REF_BEGIN_103 = "58334455";
    private static final String REF_BEGIN_202 = "DEUTDEFF4XX1134013";

    public static void main(String[] args) {
        String sourcePath = FileHelper.chooseSourceFile("Выберите файл FT14");
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
                        writer.println(modifyLine(line, j));
                        j++;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String getDestinationFile(String sourceFile) {
        String sourceFileName = Path.of(sourceFile).getFileName().toString();
        int pointIndex = sourceFileName.indexOf(".");
        String destFileName = sourceFileName.substring(0, pointIndex) + "_copy." +
                sourceFileName.substring(pointIndex + 1);
        return Path.of(sourceFile).getParent().resolve(Path.of(destFileName)).toString();
    }

    private static String modifyLine(String line, Integer i) {
        StringBuilder result = new StringBuilder(line.trim());
        modifyNumber(result, i);
        if (ADD_REF) {
            addReference(result, i, REF_TYPE);
        }
        return result.toString();
    }

    private static void modifyNumber(StringBuilder line, Integer i) {
        StringBuilder num = new StringBuilder(i.toString());
        while (num.length() < 6) {
            num.insert(0, " ");
        }
        line.replace(28, 34, num.toString());
        while (num.length() < 10) {
            num.insert(0, " ");
        }
        line.replace(370, 380, num.toString());
    }

    private static void addReference(StringBuilder line, Integer i, RefType refType) {
        //если референс уже есть, отрезаем его
        if (line.length() > REF_OFFSET - 1) {
            line.replace(REF_OFFSET, line.length(), "");
        }
        //дополняем строку пробелами до нужной длины
        while (line.length() < REF_OFFSET - 1) {
            line.append(" ");
        }

        String date = line.substring(22, 28);
        String number = line.substring(370, 380).trim();
        line.append(refType == RefType.MT103 ? getReference103(date, number) : getReference202(date, number));
    }

    private static String getReference103(String date, String number) {
        String delimiter = (IS_MIS01s) ? "" : "-";
        return REF_BEGIN_103 + date + delimiter + number;
    }

    private static String getReference202(String date, String number) {
        return date + REF_BEGIN_202 + number;
    }
}

