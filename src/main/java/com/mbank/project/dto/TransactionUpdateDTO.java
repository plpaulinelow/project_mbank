package com.mbank.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * DTO for transaction update
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionUpdateDTO {
    private Long id;
    private String description;
}
