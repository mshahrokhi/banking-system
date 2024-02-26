package com.shahrokhi.bankingsystem.service;

import com.shahrokhi.bankingsystem.entity.Account;
import com.shahrokhi.bankingsystem.entity.Bank;
import com.shahrokhi.bankingsystem.repository.BankRepository;
import com.shahrokhi.bankingsystem.service.transaction.Deposit;
import com.shahrokhi.bankingsystem.service.transaction.Transaction;
import com.shahrokhi.bankingsystem.service.transaction.Withdraw;
import com.shahrokhi.bankingsystem.service.observation.TransactionLogger;
import com.shahrokhi.bankingsystem.service.observation.TransactionObservable;
import com.shahrokhi.bankingsystem.service.observation.TransactionObserver;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class BankService implements TransactionObservable {

    private final BankRepository bankRepository;
    private final AccountService accountService;

    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Autowired
    public BankService(BankRepository bankRepository, AccountService accountService) {
        this.bankRepository = bankRepository;
        this.accountService = accountService;
    }

    @PostConstruct
    public void initialize() {
        addObserver(new TransactionLogger());
    }

    public Bank createBank() {
        return bankRepository.save(new Bank());
    }

    public Optional<Bank> findById(Long bankId) {
        return bankRepository.findById(bankId);
    }

    public Account createAccount(Bank bank, String accountHolderName, double initialBalance) {
        return accountService.createAccount(bank, accountHolderName, initialBalance);
    }

    public synchronized double getBalance(Account account) {
        return account.getBalance();
    }

    public Future<Boolean> depositAsync(Account account, double amount) {
        return executorService.submit(() -> deposit(account, amount));
    }

    private synchronized boolean deposit(Account account, double amount) {
        return performTransaction(account, amount, new Deposit());
    }

    public Future<Boolean> withdrawAsync(Account account, double amount) {
        return executorService.submit(() -> withdraw(account, amount));
    }

    private synchronized boolean withdraw(Account account, double amount) {
        return performTransaction(account, amount, new Withdraw());
    }

    public Future<Boolean> transferAsync(Account fromAccount, Account toAccount, double amount) {
        return executorService.submit(() -> transfer(fromAccount, toAccount, amount));
    }

    private synchronized boolean transfer(Account fromAccount, Account toAccount, double amount) {
        if(performTransaction(fromAccount, amount, new Withdraw())) {
            return performTransaction(toAccount, amount, new Deposit());
        }
        return false;
    }

    private boolean performTransaction(Account account, double amount, Transaction transaction) {
        if(transaction.execute(account, amount)) {
            accountService.save(account);
            notifyObservers(account.getAccountNumber(), transaction.getClass().getSimpleName(), amount);
            return true;
        }
        return false;
    }

    @PreDestroy
    public void shutdown() {
        executorService.shutdown();
    }

    @Override
    public void addObserver(TransactionObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(TransactionObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String accountNumber, String transactionType, double amount) {
        for (TransactionObserver observer : observers) {
            observer.onTransaction(accountNumber, transactionType, amount);
        }
    }
}
