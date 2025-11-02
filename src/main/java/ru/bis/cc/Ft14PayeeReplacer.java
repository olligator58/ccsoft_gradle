package ru.bis.cc;

/*
В FT14 заменяем реквизиты получателя для налоговых документов
 */

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Ft14PayeeReplacer {
    private static final String SOURCE_CODEPAGE = "ISO8859_5"; //Cp866 для DOS, Cp1251 для Windows-1251, KOI8_R для КОИ-8
    private static final String DEST_CODEPAGE = "ISO8859_5";
    private static final String EMPTY_ACC = "emptyAcc";
    private static final String TWENTY_SPACES = "                    ";

    public static void main(String[] args) {
        String rulesPath = FileHelper.chooseSourceFile("Выберите файл с правилами");
        if (rulesPath == null) {
            return;
        }
        String sourcePath = FileHelper.chooseSourceFile("Выберите файл FT14");
        if (sourcePath == null) {
            return;
        }
        String destPath = getDestinationFile(sourcePath);
        Charset sourceCharset = Charset.forName(SOURCE_CODEPAGE);
        Charset destCharset = Charset.forName(DEST_CODEPAGE);
        List<String[]> rules = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(rulesPath), sourceCharset))) {
            while (reader.ready()) {
                String line = reader.readLine();
                String[] fields = line.split(",");
                if (fields.length == 6) {
                    if (fields[0].equals(EMPTY_ACC)) {
                        fields[0] = TWENTY_SPACES;
                    }
                    rules.add(fields);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        int counter = 0;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(sourcePath), sourceCharset));
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(destPath), destCharset))) {
            while (reader.ready()) {
                String line = reader.readLine();
                String payeeCorrAcc = line.substring(1564, 1584);
                String payeeBik = line.substring(1595, 1604);
                String payeeAcc = line.substring(1764, 1784);
                for (String[] rule : rules) {
                    if (payeeCorrAcc.equals(rule[0]) && payeeBik.equals(rule[1]) && payeeAcc.equals(rule[2])) {
                        line = modifyLine(line, rule);
                        counter++;
                        break;
                    }
                }
            writer.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(String.format("Всего строк обработано: %s", counter));
    }

    private static String getDestinationFile(String sourceFile) {
        String sourceFileName = Path.of(sourceFile).getFileName().toString();
        int pointIndex = sourceFileName.indexOf(".");
        String destFileName = sourceFileName.substring(0, pointIndex) + "_res." +
                sourceFileName.substring(pointIndex + 1);
        return Path.of(sourceFile).getParent().resolve(Path.of(destFileName)).toString();
    }

    private static String modifyLine(String line, String[] rule) {
        StringBuilder result = new StringBuilder(line.trim());
        result.replace(1564, 1584, rule[3]);
        result.replace(1595, 1604, rule[4]);
        result.replace(1764, 1784, rule[5]);
        return result.toString();
    }
}

