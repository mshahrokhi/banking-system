package com.shahrokhi.bankingsystem.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.concurrent.atomic.AtomicLong;

@Entity
@ToString
@NoArgsConstructor
public class Account {
    private static final AtomicLong accountNumberCounter = new AtomicLong(1);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_id")
    private Bank bank;
    @Getter
    @Column(unique = true)
    private String accountNumber;
    @Getter
    private String holderName;
    private double balance;

    public Account(String holderName, double initialBalance) {
        this.accountNumber = generateAccountNumber();
        this.holderName = holderName;
        this.balance = initialBalance;
    }

    public Account(Bank bank, String holderName, double initialBalance) {
        this.bank = bank;
        this.accountNumber = generateAccountNumber();
        this.holderName = holderName;
        this.balance = initialBalance;
    }

    public synchronized double getBalance() {
        return balance;
    }

    public synchronized void deposit(double amount) {
        balance += amount;
    }

    public synchronized boolean withdraw(double amount) {
        if (balance >= amount) {
            balance -= amount;
            return true;
        }
        return false;
    }

    // Custom method to generate a unique account number
    private String generateAccountNumber() {
        return "Account-" + String.format("%06d", accountNumberCounter.getAndIncrement());
    }
}
