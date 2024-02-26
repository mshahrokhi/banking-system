package com.shahrokhi.bankingsystem.service.transactionType;

import com.shahrokhi.bankingsystem.model.Account;

public class Withdraw implements TransactionType {
    @Override
    public boolean execute(Account account, double amount) {
        return account.withdraw(amount);
    }
}
