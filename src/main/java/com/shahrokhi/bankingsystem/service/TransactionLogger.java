package com.shahrokhi.bankingsystem.service;

public class TransactionLogger implements TransactionObserver {
    @Override
    public void onTransaction(String accountNumber, String transactionType, double amount) {
        // Implement write logging to a file
        System.out.println("Transaction: Account=" + accountNumber +
                ", Type=" + transactionType + ", Amount=" + amount);
    }
}
