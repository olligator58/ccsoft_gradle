package ru.bis.cc.makeinitialoperations;

public class InitialOperation103 {
    private static final String PAYER_INN = "776521543603";
    private static final String MT103_SOURCE = "MT103";
    private String reference;
    private String date;
    private String number;
    private String amount;
    private String payerAccount;
    private String payerName;
    private String payerInn;
    private String payeeAccount;
    private String payeeName;
    private String payeeInn;
    private String payeeBik;
    private String Uin;
    private String purpose;
    private String source;

    public InitialOperation103(String line) {
        reference = line.substring(2574).trim();
        date = line.substring(22, 28).trim();
        number = line.substring(370, 380).trim();
        amount = findAmount(line.substring(115, 133).trim());
        payerAccount = line.substring(380, 400).trim();
        payerName = line.substring(592, 752).trim();
        payerInn = PAYER_INN;
        payeeAccount = line.substring(1764, 1784).trim();
        payeeName = line.substring(1827, 1987).trim();
        payeeInn = findPayeeInn(line.substring(1795, 1807).trim());
        payeeBik = line.substring(1595, 1604).trim();
        Uin = ""; //line.substring(2269, 2294).trim();
        purpose = correctPurpose(line.substring(2124, Math.min(line.length(), 2264)).trim() + line.substring(1364, 1560).trim());
        source = MT103_SOURCE;
    }

    private String findAmount(String amount) {
        Double doubleAmount = (Double.parseDouble(amount) / 1000);
        return doubleAmount.toString().replace('.', ',');
    }

    private String findPayeeInn(String inn) {
        String result = inn;
        if (inn.indexOf('/') >= 0) {
            result = inn.substring(0, inn.indexOf('/'));
        }
        return result;
    }

    //добавляем символ '/' для налоговых платежей, чтобы корректно распарсилось назначение платежа в первичке
    private String correctPurpose(String purpose) {
        StringBuilder result = new StringBuilder(purpose);
        if (purpose.substring(0, 2).equals("//") || purpose.substring(0, 2).equals("\\\\")) {
            char delimiter = purpose.substring(0, 2).equals("//") ? '/' : '\\';
            int slash = purpose.lastIndexOf(delimiter);
            result.insert(slash, delimiter);
        }
        return result.toString();
    }

    public String getPayerInn() {
        return PAYER_INN;
    }

    public String getPayeeAccount() {
        return payeeAccount;
    }

    public String getPayeeName() {
        return payeeName;
    }

    public String getPayeeInn() {
        return payeeInn;
    }

    public String getPayeeBik() {
        return payeeBik;
    }

    public String getUin() {
        return Uin;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getSource() {
        return source;
    }

    public String getReference() {
        return reference;
    }

    public String getDate() {
        return date;
    }

    public String getNumber() {
        return number;
    }

    public String getAmount() {
        return amount;
    }

    public String getPayerAccount() {
        return payerAccount;
    }

    public String getPayerName() {
        return payerName;
    }
}
