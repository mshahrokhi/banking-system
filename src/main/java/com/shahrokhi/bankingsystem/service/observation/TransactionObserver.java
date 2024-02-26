package com.shahrokhi.bankingsystem.service.observation;

public interface TransactionObserver {
    void onTransaction(String accountNumber, String transactionType, double amount);
}
