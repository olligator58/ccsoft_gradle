package ru.bis.cc;

/*
Размножаем входящие срочные ED101
 */

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ED101UrgentMultiplier {
    private static final List<List<String>> documents = new ArrayList<>();
    private static final int BEGIN_COUNTER = 100;
    private static final int NUM_OF_REPEATS = 200;
    private static final String DATE = "2022-09-10";
    private static final String SOURCE_CODEPAGE = "Cp1251"; //Cp866 для DOS, Cp1251 для Windows-1251, KOI8_R для КОИ-8
    private static final String DEST_CODEPAGE = "Cp1251";
    private static final String EDNO_BEGIN = "3";

    public static void main(String[] args) {
        String sourceDir = FileHelper.chooseSourceDirectory("Выберите каталог с файлами ED101");
        if (sourceDir != null) {
            Charset charsetSource = Charset.forName(SOURCE_CODEPAGE);
            Charset charsetDest = Charset.forName(DEST_CODEPAGE);

            try {
                Path destinationDir = Path.of(sourceDir + "/out");
                Files.deleteIfExists(destinationDir);
                DirectoryStream<Path> paths = Files.newDirectoryStream(Path.of(sourceDir));

                String fileNameBegin = "";

                for (Path path : paths) {
                    List<String> body = new ArrayList<>();
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path.toFile()), charsetSource))) {
                        while (reader.ready()) {
                            body.add(reader.readLine());
                        }
                        documents.add(body);
                    }
                    fileNameBegin = path.getFileName().toString().substring(0, 9);
                }

                Files.createDirectories(destinationDir);

                int j = BEGIN_COUNTER;
                for (int i = 0; i < NUM_OF_REPEATS; i++) {
                    for (List<String> lines : documents) {
                        StringBuilder fileNum = new StringBuilder(Integer.valueOf(j).toString());
                        while (fileNum.toString().length() < 6) {
                            fileNum.insert(0, "0");
                        }
                        Path destFile = destinationDir.resolve(Path.of(fileNameBegin + fileNum.toString() + ".xml"));
                        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(destFile.toFile()), charsetDest))) {
                            for (String line : lines) {
                                if (line.contains("EDNo")) {
                                    line = modifyEdNo(line, j);
                                }
                                if (line.contains("ChargeOffDate")) {
                                    line = modifyDate(line, "ChargeOffDate");
                                }
                                if (line.contains("ReceiptDate")) {
                                    line = modifyDate(line, "ReceiptDate");
                                }
                                if (line.contains("EDDate")) {
                                    line = modifyDate(line, "EDDate");
                                }
                                if (line.contains("AccDocDate")) {
                                    line = modifyDate(line, "AccDocDate");
                                }
                                if (line.contains("AccDocNo")) {
                                    line = modifyAccDocNo(line, j);
                                }
                                writer.println(line);
                            }
                        }
                        j++;
                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

    private static String modifyDate(String line, String fieldName) {
        StringBuilder result = new StringBuilder(line.trim());

        int start = result.indexOf(fieldName) + fieldName.length() + 2;
        int end = result.indexOf("\"", start);

        result.replace(start, end, DATE);
        return result.toString();
    }
}

