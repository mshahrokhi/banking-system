package com.shahrokhi.bankingsystem.service.Transaction;

import com.shahrokhi.bankingsystem.entity.Account;

public interface Transaction {
    boolean execute(Account account, double amount);
}