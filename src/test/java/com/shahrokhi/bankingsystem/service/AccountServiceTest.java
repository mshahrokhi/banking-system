package com.shahrokhi.bankingsystem.service;

import com.shahrokhi.bankingsystem.model.Account;
import com.shahrokhi.bankingsystem.model.Bank;
import com.shahrokhi.bankingsystem.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    public void testCreateAccount() {
        String accountHolderName = "Mohammad";
        double initialBalance = 100.0;
        Bank bank = new Bank();
        Account account = new Account(bank, accountHolderName, initialBalance);

        when(accountRepository.save(any(Account.class))).thenReturn(account);
        Account createdAccount = accountService.createAccount(bank, accountHolderName, initialBalance);

        assertNotNull(createdAccount);
        assertNotNull(createdAccount.getAccountNumber());
        assertEquals(accountHolderName, createdAccount.getHolderName());
        assertEquals(initialBalance, createdAccount.getBalance(), 0.001);
        assertEquals(bank, createdAccount.getBank());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    public void testFindByAccountNumber() {
        String accountNumber = "123456";
        Account mockAccount = new Account();
        Optional<Account> optionalAccount = Optional.of(mockAccount);

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(optionalAccount);
        Optional<Account> result = accountService.findByAccountNumber(accountNumber);

        assertTrue(result.isPresent());
        assertSame(mockAccount, result.get());
        verify(accountRepository).findByAccountNumber(accountNumber);
    }

    @Test
    public void testSave() {
        Account mockAccount = new Account();

        when(accountRepository.save(mockAccount)).thenReturn(mockAccount);
        Account result = accountService.save(mockAccount);

        assertSame(mockAccount, result);
        verify(accountRepository).save(mockAccount);
    }
}
