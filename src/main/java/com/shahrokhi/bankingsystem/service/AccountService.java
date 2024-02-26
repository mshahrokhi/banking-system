package com.shahrokhi.bankingsystem.service;

import com.shahrokhi.bankingsystem.model.Account;
import com.shahrokhi.bankingsystem.model.Bank;
import com.shahrokhi.bankingsystem.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {
    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account createAccount(Bank bank, String accountHolderName, double initialBalance) {
        return accountRepository.save(new Account(bank, accountHolderName, initialBalance));
    }

    public Optional<Account> findByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }

    public Account save(Account account) {
        return accountRepository.save(account);
    }
}
