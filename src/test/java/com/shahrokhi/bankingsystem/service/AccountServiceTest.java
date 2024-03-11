package com.shahrokhi.bankingsystem.service;

import com.shahrokhi.bankingsystem.model.Account;
import com.shahrokhi.bankingsystem.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

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
