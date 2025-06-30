package com.mbank.project.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Id;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "transactions",
		uniqueConstraints = @UniqueConstraint(
        name = "unique_transaction",
        columnNames = {
            "account_number",
            "trx_amount",
            "description",
            "trx_date",
            "trx_time",
            "customer_id"
        }
    ))
@Data
@NoArgsConstructor
public class Transaction {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "trx_amount")
    private BigDecimal trxAmount;

    private String description;

    @Column(name = "trx_date")
    private LocalDate trxDate;

    @Column(name = "trx_time")
    private LocalTime trxTime;

    @Column(name = "customer_id")
    private String customerId;

    @Version
    @Column(name = "version")
    private Long version;
}
