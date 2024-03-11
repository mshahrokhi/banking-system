package com.shahrokhi.bankingsystem.service;

import com.shahrokhi.bankingsystem.model.Account;
import com.shahrokhi.bankingsystem.model.Bank;
import com.shahrokhi.bankingsystem.repository.BankRepository;
import com.shahrokhi.bankingsystem.service.transactionObservation.TransactionLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BankServiceTest {

    @Mock
    private TransactionLogger transactionLogger;

    @Mock
    private BankRepository bankRepository;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private BankService bankService;

    @BeforeEach
    public void setUp() {
        bankService.addObserver(transactionLogger);
    }

    @Test
    public void testAddAccount() {
        Bank bank = new Bank(1L, new HashSet<>());
        when(bankRepository.findById(any(Long.class))).thenReturn(Optional.of(bank));
        String accountHolderName = "Mohammad";
        double initialBalance = 100.0;
        Account account;
        try {
            account = bankService.addAccount(bank.getId(), accountHolderName, initialBalance);
        } catch (Exception e) {
            e.printStackTrace();
            account = null;
        }
        assertNotNull(account);
        assertEquals(1, bank.getAccounts().size());
        verify(accountService).save(eq(account));
        verify(bankRepository).save(eq(bank));
    }


    @Test
    public void testCountAccounts() {
        Bank bank = new Bank(1L, new HashSet<>());
        when(bankRepository.findById(any(Long.class))).thenReturn(Optional.of(bank));
        String accountHolderName = "Mohammad";
        double initialBalance = 100.0;

        try {
            bankService.addAccount(bank.getId(), accountHolderName, initialBalance);
            bankService.addAccount(bank.getId(), accountHolderName, initialBalance);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(2, bankService.countAccounts(bank.getId()));
    }

    @Test
    public void testWithdrawWithSufficientBalance() {
        String accountHolderName = "Mohammad";
        double initialBalance = 100.0;
        Bank bank = new Bank();
        Account account = new Account(bank, accountHolderName, initialBalance);
        double amount = 50.0;

        boolean result;
        try {
            result = bankService.withdrawAsync(account, amount).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            result = false;
        }

        assertTrue(result);
        assertEquals(initialBalance - amount, account.getBalance(), 0.001);
        verify(accountService).save(eq(account));
        verify(transactionLogger).onTransaction(eq(account.getAccountNumber()), eq("Withdraw"), eq(amount));
    }

    @Test
    public void testWithdrawAsyncWithInsufficientBalance() {
        String accountHolderName = "Mohammad";
        double initialBalance = 50.0;
        Bank bank = new Bank();
        Account account = new Account(bank, accountHolderName, initialBalance);
        double amount = 100.0;

        boolean result;
        try {
            result = bankService.withdrawAsync(account, amount).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            result = false;
        }

        assertFalse(result);
        assertEquals(initialBalance, account.getBalance(), 0.001);
    }

    @Test
    public void testDepositAsync() {
        String accountHolderName = "Mohammad";
        double initialBalance = 50.0;
        Bank bank = new Bank();
        Account account = new Account(bank, accountHolderName, initialBalance);
        double amount = 100.0;

        boolean result;
        try {
            result = bankService.depositAsync(account, amount).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            result = false;
        }

        assertTrue(result);
        assertEquals(initialBalance + amount, account.getBalance(), 0.001);
        verify(accountService).save(eq(account));
        verify(transactionLogger).onTransaction(eq(account.getAccountNumber()), eq("Deposit"), eq(amount));
    }

    @Test
    public void testTransferAsyncWithSufficientBalance() {
        String fromAccountHolderName = "Mohammad";
        String toAccountHolderName = "Ali";
        double initialBalance = 100.0;
        Bank bank = new Bank();
        Account fromAccount = new Account(bank, fromAccountHolderName, initialBalance);
        Account toAccount = new Account(bank, toAccountHolderName, initialBalance);
        double amount = 50.0;

        boolean result;
        try {
            result = bankService.transferAsync(fromAccount, toAccount, amount).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            result = false;
        }

        assertTrue(result);
        assertEquals(initialBalance - amount, fromAccount.getBalance(), 0.001);
        assertEquals(initialBalance + amount, toAccount.getBalance(), 0.001);
        verify(accountService).save(eq(fromAccount));
        verify(accountService).save(eq(toAccount));
        verify(transactionLogger).onTransaction(eq(fromAccount.getAccountNumber()), eq("Withdraw"), eq(amount));
        verify(transactionLogger).onTransaction(eq(toAccount.getAccountNumber()), eq("Deposit"), eq(amount));
    }

    @Test
    public void testTransferAsyncWithInsufficientBalance() {
        String fromAccountHolderName = "Mohammad";
        String toAccountHolderName = "Ali";
        double initialBalance = 50.0;
        Bank bank = new Bank();
        Account fromAccount = new Account(bank, fromAccountHolderName, initialBalance);
        Account toAccount = new Account(bank, toAccountHolderName, initialBalance);
        double amount = 100.0;

        boolean result;
        try {
            result = bankService.transferAsync(fromAccount, toAccount, amount).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            result = false;
        }

        assertFalse(result);
        assertEquals(initialBalance, fromAccount.getBalance(), 0.001);
        assertEquals(initialBalance, toAccount.getBalance(), 0.001);
    }

    @Test
    public void testCheckBalance() {
        String accountHolderName = "Mohammad";
        double initialBalance = 100.0;
        Bank bank = new Bank();
        Account account = new Account(bank, accountHolderName, initialBalance);

        double balance = bankService.getBalance(account);

        assertEquals(initialBalance, balance, 0.001);
    }
}