package org.shiftlab.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class TransactionDto {
    private Integer id;
    private BigDecimal amount;
    private PaymentType paymentType;
    private LocalDateTime transactionDate;
    private Integer sellerId;

}
