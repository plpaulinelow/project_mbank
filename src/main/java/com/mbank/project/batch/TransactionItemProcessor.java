package com.mbank.project.batch;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.mbank.project.entity.Transaction;
import com.mbank.project.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TransactionItemProcessor implements ItemProcessor<Transaction, Transaction> {

	private final TransactionRepository transactionRepository;
	
    @Override
    public Transaction process(Transaction item) throws Exception {
    	 
    	boolean exists = transactionRepository.existsByAccountNumberAndTrxAmountAndDescriptionAndTrxDateAndTrxTimeAndCustomerId(
                item.getAccountNumber(),
                item.getTrxAmount(),
                item.getDescription(),
                item.getTrxDate(),
                item.getTrxTime(),
                item.getCustomerId()
        );

        if (exists) {
            // Duplicate found → skip this record
            System.out.println("Duplicate skipped: " + item);
            return null;
        }
    	
        if (item.getDescription() != null) {
            item.setDescription(item.getDescription().trim());
        }
        return item;
    }
}