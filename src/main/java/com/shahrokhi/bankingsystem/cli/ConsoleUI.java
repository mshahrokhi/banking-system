package com.shahrokhi.bankingsystem.cli;

import com.shahrokhi.bankingsystem.entity.Account;
import com.shahrokhi.bankingsystem.entity.Bank;
import com.shahrokhi.bankingsystem.service.AccountService;
import com.shahrokhi.bankingsystem.service.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Component
public class ConsoleUI implements CommandLineRunner {

    private final BankService bankService;
    private final AccountService accountService;

    @Autowired
    public ConsoleUI(BankService bankService, AccountService accountService) {
        this.bankService = bankService;
        this.accountService = accountService;
    }

    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("==================================================");
            System.out.println("1. Create Bank");
            System.out.println("2. Create Account");
            System.out.println("3. Check Balance");
            System.out.println("4. Deposit");
            System.out.println("5. Withdraw");
            System.out.println("6. Transfer");
            System.out.println("0. Exit");

            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    scanner.nextLine(); // Consume the newline character
                    Bank createdBank = bankService.createBank();
                    System.out.println("Bank created with ID: " + createdBank.getId());
                    break;

                case 2:
                    scanner.nextLine(); // Consume the newline character
                    System.out.print("Enter bank ID: ");
                    long bankId = scanner.nextLong();
                    Optional<Bank> bankById = bankService.findById(bankId);

                    if (bankById.isPresent()) {
                        Bank bank = bankById.get();
                        scanner.nextLine(); // Consume the newline character
                        System.out.print("Enter account holder name: ");
                        String accountHolderName = scanner.nextLine();
                        System.out.print("Enter initial balance: ");
                        double initialBalance = scanner.nextDouble();

                        Account account = bankService.createAccount(bank, accountHolderName, initialBalance);
                        System.out.println("Account created with Account Number: " + account.getAccountNumber());
                    } else {
                        System.out.println("Bank with ID " + bankId + " not found.");
                    }
                    break;

                case 3:
                    scanner.nextLine(); // Consume the newline character
                    checkBalance(scanner);
                    break;

                case 4:
                    performTransaction("Deposit", scanner);
                    break;

                case 5:
                    performTransaction("Withdraw", scanner);
                    break;

                case 6:
                    performTransfer(scanner);
                    break;

                case 0:
                    System.out.println("Exiting the application. Goodbye!");
                    scanner.close();
                    bankService.shutdown();
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
    }

    private void performTransaction(String transactionType, Scanner scanner) {
        System.out.print("Enter account number: ");
        String accountNumber = scanner.next();
        Optional<Account> accountByAccountNumber = accountService.findByAccountNumber(accountNumber);

        if (accountByAccountNumber.isPresent()) {
            Account account = accountByAccountNumber.get();
            System.out.print("Enter amount: ");
            double amount = scanner.nextDouble();

            switch (transactionType.toLowerCase()) {
                case "deposit":
                    Future<Boolean> depositResult = bankService.depositAsync(account, amount);
                    System.out.println("Deposit in progress...");
                    try {
                        boolean success = depositResult.get();
                        if(success) {
                            System.out.println("Deposit successful. Updated balance: "
                                    + bankService.getBalance(account));
                        } else {
                            System.out.println("Deposit failed!");
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                        System.out.println("An error occurred during the deposit");
                    }
                    break;

                case "withdraw":
                    Future<Boolean> withdrawResult = bankService.withdrawAsync(account, amount);
                    System.out.println("Withdraw in progress...");
                    try {
                        boolean success = withdrawResult.get();
                        if(success) {
                            System.out.println("Withdrawal successful. Updated balance: "
                                    + bankService.getBalance(account));
                        } else {
                            System.out.println("Withdrawal failed, Insufficient balance!");
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                        System.out.println("An error occurred during the withdraw");
                    }
                    break;

                default:
                    System.out.println("Invalid transaction type.");
            }
        } else {
            System.out.println("Account not found!");
        }
    }

    private void performTransfer(Scanner scanner) {
        System.out.print("Enter source account number: ");
        String fromAccountNumber = scanner.next();
        Optional<Account> fromAccountByNumber = accountService.findByAccountNumber(fromAccountNumber);

        System.out.print("Enter destination account number: ");
        String toAccountNumber = scanner.next();
        Optional<Account> toAccountByNumber = accountService.findByAccountNumber(toAccountNumber);

        if (fromAccountByNumber.isPresent() && toAccountByNumber.isPresent()) {
            Account fromAccount = fromAccountByNumber.get();
            Account toAccount = toAccountByNumber.get();

            System.out.print("Enter transfer amount: ");
            double amount = scanner.nextDouble();

            Future<Boolean> transferResult = bankService.transferAsync(fromAccount, toAccount, amount);
            System.out.println("Transfer in progress...");

            try {
                boolean success = transferResult.get();
                if(success) {
                    System.out.println("Transfer successful.");
                    System.out.println("Updated balance for source account (" + fromAccountNumber + "): "
                            + bankService.getBalance(fromAccount));
                    System.out.println("Updated balance for destination account (" + toAccountNumber + "): "
                            + bankService.getBalance(toAccount));
                } else {
                    System.out.println("Transfer failed, Insufficient balance!");
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                System.out.println("An error occurred during the transfer");
            }
        } else {
            System.out.println("One or both accounts not found!");
        }
    }

    private void checkBalance(Scanner scanner) {
        System.out.print("Enter account number: ");
        String accountNumber = scanner.next();
        Optional<Account> account = accountService.findByAccountNumber(accountNumber);

        if (account.isPresent()) {
            System.out.println("Current balance for account " + accountNumber + ": "
                    + bankService.getBalance(account.get()));
        } else {
            System.out.println("Account not found!");
        }
    }
}
