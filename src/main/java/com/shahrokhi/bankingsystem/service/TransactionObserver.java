package com.shahrokhi.bankingsystem.service;

public interface TransactionObserver {
    void onTransaction(String accountNumber, String transactionType, double amount);
}
