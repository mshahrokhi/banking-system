package com.shahrokhi.bankingsystem.service.transactionObservation;

public interface TransactionObserver {
    void onTransaction(String accountNumber, String transactionType, double amount);
}
