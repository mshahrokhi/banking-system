package com.shahrokhi.bankingsystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.annotation.Primary;

import java.util.Random;

@Entity
@ToString
@NoArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String accountNumber;
    private String holderName;
    private double balance;

    public Account(String holderName, double initialBalance) {
        this.accountNumber = generateAccountNumber();
        this.holderName = holderName;
        this.balance = initialBalance;
    }

    // Custom method to generate a unique account number
    private String generateAccountNumber() {
        return "ACCT-" + String.format("%06d", new Random().nextInt(1000000));
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getHolderName() {
        return holderName;
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
}
