package ru.bis.cc.makeinitialoperations;

public class InitialOperation202 {
    private static final String MT202_SOURCE = "MT202";
    private String reference;
    private String date;
    private String number;
    private String amount;
    private String payerAccount;
    private String payeeAccount;
    private String source;

    public InitialOperation202(String line) {
        reference = line.substring(2574).trim();
        date = line.substring(22, 28).trim();
        number = line.substring(370, 380).trim();
        amount = findAmount(line.substring(115, 133).trim());
        payerAccount = line.substring(380, 400).trim();
        payeeAccount = line.substring(1764, 1784).trim();
        source = MT202_SOURCE;
    }

    private String findAmount(String amount) {
        Double doubleAmount = (Double.parseDouble(amount) / 1000);
        return doubleAmount.toString().replace('.', ',');
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

    public String getPayeeAccount() {
        return payeeAccount;
    }
}
