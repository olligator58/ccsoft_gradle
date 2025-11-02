package ru.bis.cc.makeinitialoperations;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MakeInitialOperations202 {
    private static final String DEST_FILE_NAME = "init202.txt";
    private static final String SOURCE_CODEPAGE = "ISO8859_5"; //Cp866 для DOS, Cp1251 для Windows-1251, KOI8_R для КОИ-8
    private static final String DEST_CODEPAGE = "UTF-8";
    private static final String HEADER = "{1:F01DEUTRUMMAXXX0493830007}{2:O2021215210324DEUTDEFF4XXX11340131182103241215N}{3:{108:E202210324AXHDUK}{121:a65b1625-fb89-42f7-b961-0d820306b7cc}}{4:";
    private static final String TAG52A = ":52A:DEUTDEFFXXX";
    private static final String TAG56A = ":56A:INGBRUMMXXX";
    private static final String TAG57A = ":57A:/30111810600001003126\nMGTCBEBEECL";
    private static final String TAG58A_BANK = "DEUTDEFFXXX";
    private static final String TAG72 = ":72:/REC/BIK 041806647/ VO60070\n//INN 0000000000/PAYMENT REASON FX";
    private static final String FOOTER = "-}{5:{CHK:473122A58058}}\n";

    public static void main(String[] args) {
        String sourcePath = getSourceFile();
        if (sourcePath != null) {
            String destPath = getDestinationFile(sourcePath);
            Charset charsetSource = Charset.forName(SOURCE_CODEPAGE);
            Charset charsetDest = Charset.forName(DEST_CODEPAGE);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(sourcePath), charsetSource));
                 PrintStream writer = new PrintStream(destPath, charsetDest)) {
                while (reader.ready()) {
                    String line = reader.readLine();
                    List<String> modLines = getModLines(line);
                    for (String modline : modLines) {
                        writer.println(modline);
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
        return Path.of(sourceFile).getParent().resolve(Path.of(DEST_FILE_NAME)).toString();
    }

    private static List<String> getModLines(String line) {
        List<String> lines = new ArrayList<>();
        InitialOperation202 initialOperation202 = new InitialOperation202(line);
        lines.add(getHeader(initialOperation202.getReference(), initialOperation202.getDate()));
        lines.add(getTag20(initialOperation202.getDate(), initialOperation202.getNumber()));
        lines.add(getTag21(initialOperation202.getDate(), initialOperation202.getNumber()));
        lines.add(getTag32A(initialOperation202.getDate(), initialOperation202.getAmount()));
        lines.add(getTag52A());
        lines.add(getTag53B(initialOperation202.getPayerAccount()));
        lines.add(getTag56A());
        lines.add(getTag57A());
        lines.add(getTag58A(initialOperation202.getPayeeAccount()));
        lines.add(getTag72());
        lines.add(getFooter());
        return lines;
    }

    private static String getHeader(String reference, String date) {
        StringBuilder result = new StringBuilder(HEADER);
        result.replace(68, 74, date);
        result.replace(92, 98, date);
        result.replace(40, 68, reference);
        return result.toString();
    }

    private static String getTag20(String date, String number) {
        return ":20:GCMS" + date + number;
    }

    private static String getTag21(String date, String number) {
        return ":21:GCMS" + date + number;
    }

    private static String getTag32A(String date, String amount) {
        return ":32A:" + date + "RUB" + amount;
    }

    private static String getTag52A() {
        return TAG52A;
    }

    private static String getTag53B(String payerAccount) {
        return ":53B:/" + payerAccount;
    }

    private static String getTag56A() {
        return TAG56A;
    }

    private static String getTag57A() {
        return TAG57A;
    }

    private static String getTag58A(String payeeAccount) {
        return ":58A:/" + payeeAccount + "\n" + TAG58A_BANK;
    }

    private static String getTag72() {
        return TAG72;
    }

    private static String getFooter() {
        return FOOTER;
    }

}

