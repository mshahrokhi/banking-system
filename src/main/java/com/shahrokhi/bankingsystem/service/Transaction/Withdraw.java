package com.shahrokhi.bankingsystem.service.Transaction;

import com.shahrokhi.bankingsystem.entity.Account;

public class Withdraw implements Transaction {
    @Override
    public boolean execute(Account account, double amount) {
        return account.withdraw(amount);
    }
}
