package com.shahrokhi.bankingsystem.service;

import java.util.ArrayList;
import java.util.List;

public interface TransactionObservable {
    List<TransactionObserver> observers = new ArrayList<>();

    void addObserver(TransactionObserver observer);

    void removeObserver(TransactionObserver observer);

    void notifyObservers(String accountNumber, String transactionType, double amount);
}