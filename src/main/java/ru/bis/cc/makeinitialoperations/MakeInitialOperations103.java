package ru.bis.cc.makeinitialoperations;

import ru.bis.cc.FileHelper;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MakeInitialOperations103 {
    private static final String DEST_FILE_NAME = "init103.txt";
    private static final String SOURCE_CODEPAGE = "ISO8859_5"; //Cp866 ‰Îˇ DOS, Cp1251 ‰Îˇ Windows-1251, KOI8_R ‰Îˇ  Œ»-8
    private static final String DEST_CODEPAGE = "UTF-8";
    private static final String HEADER = "{1:F01DEUTRUMMXXXX0000239385}{2:O1030708211215DBEBRUMMAXXX00002393852112150804N}{4:";
    private static final String TAG23B = ":23B:CRED";
    private static final String TAG52A = ":52A:DEUTRUMM";
    private static final String TAG57D_CORRACCT = "30101810600000000718";
    private static final String TAG57D_BANKNAME = "œ≈Õ«≈Õ— »… –‘ ¿Œ –Œ——≈À‹’Œ«¡¿Õ \n„. œÂÌÁ‡\nRUSSIAN FEDERATION";
    private static final String TAG71A = ":71A:OUR";
    private static final String TAG72 = ":72:/REC/0101";
    private static final String FOOTER = "-}{5:{CHK:239385239385}}\n";
    private static final int TAG_MAX_LENGTH = 35;

    public static void main(String[] args) {
        String sourcePath = FileHelper.chooseSourceFile("¬˚·ÂËÚÂ Ù‡ÈÎ FT14");
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

    private static String getDestinationFile(String sourceFile) {
        return Path.of(sourceFile).getParent().resolve(Path.of(DEST_FILE_NAME)).toString();
    }

    private static List<String> getModLines(String line) {
        List<String> lines = new ArrayList<>();
        InitialOperation103 initialOperation103 = new InitialOperation103(line);
        lines.add(getHeader(initialOperation103.getDate()));
        lines.add(getTag20(initialOperation103.getNumber()));
        lines.add(getTag23B());
        lines.add(getTag32A(initialOperation103.getDate(), initialOperation103.getAmount()));
        lines.add(getTag50K(initialOperation103.getPayerAccount(), initialOperation103.getPayerInn(), initialOperation103.getPayerName()));
        lines.add(getTag52A());
        lines.add(getTag53B(initialOperation103.getPayerAccount()));
        lines.add(getTag57D(initialOperation103.getPayeeBik()));
        lines.add(getTag59(initialOperation103.getPayeeAccount(), initialOperation103.getPayeeInn(), initialOperation103.getPayeeName()));
        lines.add(getTag70(initialOperation103.getPurpose()));
        lines.add(getTag71A());
        lines.add(getTag72(initialOperation103.getPurpose()));
        lines.add(getTag77B(initialOperation103.getReference()));
        lines.add(getFooter());
        return lines;
    }

    private static String getHeader(String date) {
        StringBuilder result = new StringBuilder(HEADER);
        result.replace(40, 46, date);
        result.replace(68, 74, date);
        return result.toString();
    }

    private static String getTag20(String number) {
        return ":20:" + number;
    }

    private static String getTag23B() {
        return TAG23B;
    }

    private static String getTag32A(String date, String amount) {
        return ":32A:" + date + "RUR" + amount;
    }

    private static String getTag50K(String payerAccount, String payerInn, String payerName) {
        return ":50K:/" + payerAccount + "\n" +
                "INN" + payerInn + "\n" +
                splitLines(payerName);
    }

    private static String getTag52A() {
        return TAG52A;
    }

    private static String getTag53B(String payerAccount) {
        return ":53B:/" + payerAccount;
    }

    private static String getTag57D(String payeeBik) {
        return ":57D:/" + TAG57D_CORRACCT + "\n" +
                "BIK" + payeeBik + "\n" +
                TAG57D_BANKNAME;
    }

    private static String getTag59(String payeeAccount, String payeeInn, String payeeName) {
        return ":59:/" + payeeAccount + "\n" +
                "INN" + payeeInn + "\n" +
                splitLines(payeeName);
    }

    private static String getTag70(String purpose) {
        return ":70:" + splitLines(purpose.substring(0, Math.min(purpose.length(), TAG_MAX_LENGTH * 4)));
    }

    private static String getTag71A() {
        return TAG71A;
    }

    private static String getTag72(String purpose) {
        String result = TAG72;
        if (purpose.length() > TAG_MAX_LENGTH * 4) {
            result = result + "\n" + splitLines(purpose.substring(TAG_MAX_LENGTH * 4));
        }
        return result;
    }

    private static String getTag77B(String reference) {
        return ":77B:" + reference;
    }

    private static String getFooter() {
        return FOOTER;
    }

    private static String splitLines(String text) {
        String result = "";
        StringBuilder tempString = new StringBuilder(text);
        while (tempString.length() > 0) {
            int index = (tempString.length() > TAG_MAX_LENGTH) ? TAG_MAX_LENGTH : tempString.length();
            result = result + tempString.substring(0, index) + "\n";
            tempString = tempString.replace(0, index, "");
        }
        return result.substring(0, result.length() - 1);
    }
}

