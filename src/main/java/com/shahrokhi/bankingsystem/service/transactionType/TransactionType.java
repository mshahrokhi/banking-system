package com.shahrokhi.bankingsystem.service.transactionType;

import com.shahrokhi.bankingsystem.model.Account;

public interface TransactionType {
    boolean execute(Account account, double amount);
}