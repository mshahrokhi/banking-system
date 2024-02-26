package com.shahrokhi.bankingsystem.service;

import com.shahrokhi.bankingsystem.entity.Account;
import com.shahrokhi.bankingsystem.entity.Bank;
import com.shahrokhi.bankingsystem.repository.AccountRepository;
import com.shahrokhi.bankingsystem.repository.BankRepository;
import com.shahrokhi.bankingsystem.service.observation.TransactionLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BankServiceTest {

    @Mock
    private TransactionLogger transactionLogger;

    @InjectMocks
    private BankService bankService;

    @BeforeEach
    public void setUp() {
        transactionLogger = mock(TransactionLogger.class);
        bankService = new BankService(mock(BankRepository.class), mock(AccountService.class));
        bankService.addObserver(transactionLogger);
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