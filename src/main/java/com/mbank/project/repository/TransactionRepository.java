package com.mbank.project.repository;
import com.mbank.project.entity.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/*
 * Transaction repository
 */
public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {
	boolean existsByAccountNumberAndTrxAmountAndDescriptionAndTrxDateAndTrxTimeAndCustomerId(
            String accountNumber,
            BigDecimal trxAmount,
            String description,
            LocalDate trxDate,
            LocalTime trxTime,
            String customerId
    );
}