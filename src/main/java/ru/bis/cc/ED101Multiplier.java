package ru.bis.cc;
/*
Размножаем входящий ED101
 */

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ED101Multiplier {
    private static final List<String> header = new ArrayList<>();
    private static final List<String> body = new ArrayList<>();
    private static final int BEGIN_COUNTER = 100;
    private static final int NUM_OF_REPEATS = 20;
    private static final String SOURCE_CODEPAGE = "Cp1251"; //Cp866 для DOS, Cp1251 для Windows-1251, KOI8_R для КОИ-8
    private static final String DEST_CODEPAGE = "Cp1251";
    private static boolean isHeader = true;
    private static boolean isBody = false;
    private static final String FOOTER = "</PacketEPD>";
    private static int isNextDoc = 0;
    private static final String EDNO_BEGIN = "3";

    public static void main(String[] args) {
        String sourcePath = FileHelper.chooseSourceFile("Выберите файл ED101");
        if (sourcePath != null) {
            String destPath = getDestinationFile(sourcePath);
            Charset charsetSource = Charset.forName(SOURCE_CODEPAGE);
            Charset charsetDest = Charset.forName(DEST_CODEPAGE);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(sourcePath), charsetSource));
                 PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(destPath), charsetDest))) {
                while (reader.ready()) {
                    String line = reader.readLine();
                    if (isHeader && line.contains("<ED101")) {
                        isHeader = false;
                        isBody = true;
                    }
                    if (isBody && line.contains("</PacketEPD>")) {
                        isBody = false;
                    }
                    if (isHeader) {
                        header.add(line);
                    } else if (isBody) {
                        body.add(line);
                    }
                }

                for (String line : header) {
                    writer.println(line);
                }

                int j = BEGIN_COUNTER;
                for (int i = 0; i < NUM_OF_REPEATS; i++) {
                    for (String line : body) {
                        if (line.contains("EDNo")) {
                            line = modifyEdNo(line, j);
                            isNextDoc++;
                        }
                        if (line.contains("AccDocNo")) {
                            line = modifyAccDocNo(line, j);
                            isNextDoc++;
                        }
                        writer.println(line);
                        if (isNextDoc == 2) {
                            j++;
                            isNextDoc = 0;
                        }
                    }
                }
                writer.println(FOOTER);
                FileHelper.showMessage("Обработка завершена успешно !", "",false);
            } catch (IOException e) {
                FileHelper.showMessage("Произошла ошибка !", "Ошибка", true);
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

    private static String modifyEdNo(String line, Integer i) {
        StringBuilder result = new StringBuilder(line.trim());
        StringBuilder edNo = new StringBuilder(i.toString());
        while (edNo.length() < 8) {
            edNo.insert(0, "0");
        }
        edNo.insert(0, EDNO_BEGIN);

        int start = result.indexOf("EDNo") + 6;
        int end = result.indexOf("\"", start);

        result.replace(start, end, edNo.toString());
        return result.toString();
    }

    private static String modifyAccDocNo(String line, Integer i) {
        StringBuilder result = new StringBuilder(line.trim());

        int start = result.indexOf("AccDocNo") + 10;
        int end = result.indexOf("\"", start);

        result.replace(start, end, i.toString());
        return result.toString();
    }
}

