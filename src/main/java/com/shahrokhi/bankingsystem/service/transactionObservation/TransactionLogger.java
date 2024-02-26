package com.shahrokhi.bankingsystem.service.transactionObservation;

import java.io.FileWriter;
import java.io.IOException;

public class TransactionLogger implements TransactionObserver {
    private static final String LOG_FILE_PATH = "transactions_log.txt";

    @Override
    public void onTransaction(String accountNumber, String transactionType, double amount) {
        String logMessage = String.format("Account: %s, Type: %s, Amount: %.2f",
                accountNumber, transactionType, amount);
        appendToFile(logMessage);
    }

    private synchronized void appendToFile(String logMessage) {
        try (FileWriter writer = new FileWriter(LOG_FILE_PATH, true)) {
            writer.write(logMessage + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
