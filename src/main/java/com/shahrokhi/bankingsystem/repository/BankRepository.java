package com.shahrokhi.bankingsystem.repository;

import com.shahrokhi.bankingsystem.model.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankRepository extends JpaRepository<Bank, Long> {
}
