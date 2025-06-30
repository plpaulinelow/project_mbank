package com.mbank.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mbank.project.dto.TransactionDTO;
import com.mbank.project.dto.TransactionUpdateDTO;
import com.mbank.project.entity.Transaction;
import com.mbank.project.repository.TransactionRepository;
import com.mbank.project.specification.TransactionSpecifications;

/*
 * Transaction Service to handle bussiness logic
 */
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public Page<TransactionDTO> searchTransactions(
            String customerId,
            String accountNumber,
            String description,
            Pageable pageable
    ) {
        Specification<Transaction> spec = Specification
                .where(TransactionSpecifications.customerIdEquals(customerId))
                .and(TransactionSpecifications.accountNumberEquals(accountNumber))
                .and(TransactionSpecifications.descriptionLike(description));

        return transactionRepository.findAll(spec, pageable)
                .map(this::convertToDTO);
    }

    @Transactional
    public TransactionDTO updateTransaction(Long id, TransactionUpdateDTO dto) {
        Transaction entity = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        
        entity.setDescription(dto.getDescription());

        try {
            Transaction updated = transactionRepository.saveAndFlush(entity);
            return convertToDTO(updated);
        } catch (ObjectOptimisticLockingFailureException ex) {
            throw new RuntimeException("Concurrent update detected!");
        }
    }

    private TransactionDTO convertToDTO(Transaction trx) {
        return new TransactionDTO(
                trx.getId(),
                trx.getAccountNumber(),
                trx.getTrxAmount(),
                trx.getDescription(),
                trx.getTrxDate(),
                trx.getTrxTime(),
                trx.getCustomerId(),
                trx.getVersion()
        );
    }
}