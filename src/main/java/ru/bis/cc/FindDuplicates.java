package ru.bis.cc;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class FindDuplicates {
    private static final String[] EXC_SYMBOLS = {"!"}; // символы, которые игнорируются при поиске дублей

    public static void main(String[] args) {
        Set<String> words = new HashSet<>();
        String fileName = FileHelper.chooseSourceFile("Выберите файл, в котором нужно отыскать дубли");

        if (fileName != null) {
            int doublesCounter = 0;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)))) {
                while (reader.ready()) {
                    String line = reader.readLine();
                    for (String symbol : EXC_SYMBOLS) {
                        line = line.replaceAll(symbol, "");
                    }
                    if (words.contains(line)) {
                        System.out.println(String.format("Дубль: %s", line));
                        doublesCounter++;
                    } else {
                        words.add(line);
                    }
                }
                if (doublesCounter == 0) {
                    System.out.println(String.format("Дубли не обнаружены. Всего уникальных значений: %d", words.size()));
                } else {
                    System.out.println(String.format("Обнаружено %s дублей", doublesCounter));
                }
            } catch (IOException ignored) {
            }
        }
    }
}
