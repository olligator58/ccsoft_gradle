package ru.bis.cc;
/*
Добавляем референсы первичной информации в FT14
 */

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Scanner;

public class Ft14AddReference {
    private static final boolean CREATE_MT103_REF = true; //Создавать референс для 103-их операций
    private static final String SOURCE_CODEPAGE = "ISO8859_5"; //Cp866 для DOS, Cp1251 для Windows-1251, KOI8_R для КОИ-8
    private static final String DEST_CODEPAGE = "ISO8859_5";
    private static final int REF_OFFSET = 2575;
    private static final String REF_BEGIN_103 = "58334455";
    private static final String REF_BEGIN_202 = "DEUTDEFF4XXX1134013";
    private static final boolean IS_MIS01s = true;

    public static void main(String[] args) {
        String sourcePath = getSourceFile();
        if (sourcePath != null) {
            String destPath = getDestinationFile(sourcePath);
            Charset charsetSource = Charset.forName(SOURCE_CODEPAGE);
            Charset charsetDest = Charset.forName(DEST_CODEPAGE);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(sourcePath), charsetSource));
                 PrintWriter writer = new PrintWriter(new OutputStreamWriter (new FileOutputStream(destPath), charsetDest))) {
                while (reader.ready()) {
                    String line = reader.readLine();
                    writer.println(getModifiedLine(line));
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
            System.out.println("Путь к файлу введен неверно");;
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

    private static String getModifiedLine(String line) {
        StringBuilder result = new StringBuilder(line.trim());
        if (result.length() < REF_OFFSET) {
            //дополняем строку пробелами до нужной длины
            while (result.length() < REF_OFFSET - 1) {
                result.append(" ");
            }
            result.append(CREATE_MT103_REF ? getReference103(line) : getReference202(line));
        }
        return result.toString();
    }

    private static String getReference103(String line) {
        String delimiter = (IS_MIS01s) ? "" : "-";
        String date = line.substring(22, 28);
        String number = line.substring(370, 380).trim();
        return REF_BEGIN_103 + date + delimiter + number;
    }

    private static String getReference202(String line) {
        String date = line.substring(22, 28);
        String number = line.substring(370, 380).trim();
        return date + REF_BEGIN_202 + number;
    }
}
