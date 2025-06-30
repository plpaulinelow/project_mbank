package com.mbank.project.controller;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mbank.project.dto.TransactionDTO;
import com.mbank.project.dto.TransactionUpdateDTO;
import com.mbank.project.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;

/*
 *  RESTful API calls
 */
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public Page<TransactionDTO> search(
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String accountNumber,
            @RequestParam(required = false) String description,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return transactionService.searchTransactions(customerId, accountNumber, description, pageable);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public TransactionDTO updateDescription(
            @PathVariable Long id,
            @RequestBody TransactionUpdateDTO dto
    ) {
    	return transactionService.updateTransaction(id, dto);
    }
}

