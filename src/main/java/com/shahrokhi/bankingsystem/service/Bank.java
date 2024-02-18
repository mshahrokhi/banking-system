package com.shahrokhi.bankingsystem.service;

import com.shahrokhi.bankingsystem.entity.Account;

import java.util.HashMap;
import java.util.Map;

public class Bank {
    private final Map<String, Account> accounts = new HashMap<>();
    private final TransactionObserver transactionObserver;

    public Bank(TransactionObserver transactionObserver) {
        this.transactionObserver = transactionObserver;
    }

    public synchronized void createAccount(String accountNumber, String accountHolderName, double initialBalance) {
        if (!accounts.containsKey(accountNumber)) {
            Account account = new Account(accountHolderName, initialBalance);
            accounts.put(accountNumber, account);
        }
    }

    public synchronized void deposit(String accountNumber, double amount) {
        if (accounts.containsKey(accountNumber)) {
            Account account = accounts.get(accountNumber);
            account.deposit(amount);
            notifyObserver(accountNumber, "Deposit", amount);
        }
    }

    public synchronized boolean withdraw(String accountNumber, double amount) {
        if (accounts.containsKey(accountNumber)) {
            Account account = accounts.get(accountNumber);
            boolean success = account.withdraw(amount);
            if (success) {
                notifyObserver(accountNumber, "Withdrawal", amount);
            }
            return success;
        }
        return false;
    }

    public synchronized boolean transfer(String fromAccountNumber, String toAccountNumber, double amount) {
        if (accounts.containsKey(fromAccountNumber) && accounts.containsKey(toAccountNumber)) {
            Account fromAccount = accounts.get(fromAccountNumber);
            Account toAccount = accounts.get(toAccountNumber);

            if (fromAccount.withdraw(amount)) {
                toAccount.deposit(amount);
                notifyObserver(fromAccountNumber, "Transfer", amount);
                notifyObserver(toAccountNumber, "Transfer", amount);
                return true;
            }
        }
        return false;
    }

    public synchronized double getBalance(String accountNumber) {
        if (accounts.containsKey(accountNumber)) {
            return accounts.get(accountNumber).getBalance();
        }
        return -1; // Account not found
    }

    private void notifyObserver(String accountNumber, String transactionType, double amount) {
        if (transactionObserver != null) {
            transactionObserver.onTransaction(accountNumber, transactionType, amount);
        }
    }
}
