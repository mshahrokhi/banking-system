package com.shahrokhi.bankingsystem.service.transaction;

import com.shahrokhi.bankingsystem.entity.Account;

public interface Transaction {
    boolean execute(Account account, double amount);
}