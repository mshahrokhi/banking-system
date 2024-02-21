package com.shahrokhi.bankingsystem.service.Transaction;

import com.shahrokhi.bankingsystem.entity.Account;

public class Deposit implements Transaction {
    @Override
    public boolean execute(Account account, double amount) {
        account.deposit(amount);
        return true;
    }
}
