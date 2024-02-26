package com.shahrokhi.bankingsystem.service.transactionType;

import com.shahrokhi.bankingsystem.model.Account;

public class Deposit implements TransactionType {
    @Override
    public boolean execute(Account account, double amount) {
        account.deposit(amount);
        return true;
    }
}
