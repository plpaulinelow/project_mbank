package com.mbank.project.specification;

import org.springframework.data.jpa.domain.Specification;

import com.mbank.project.entity.Transaction;

public class TransactionSpecifications {

    public static Specification<Transaction> customerIdEquals(String customerId) {
        return (root, query, cb) ->
                customerId == null ? null :
                        cb.like(root.get("customerId"),  customerId);
    }

    public static Specification<Transaction> accountNumberEquals(String accountNumber) {
        return (root, query, cb) ->
                accountNumber == null ? null :
                        cb.like(root.get("accountNumber"), accountNumber);
    }

    public static Specification<Transaction> descriptionLike(String description) {
        return (root, query, cb) ->
                description == null ? null :
                        cb.like(root.get("description"), "%" + description + "%");
    }
}