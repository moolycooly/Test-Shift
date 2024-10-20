package org.shiftlab.services;

import org.shiftlab.dto.PaymentType;
import org.shiftlab.dto.TransactionDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface TransactionService {
    List<TransactionDto> findAllTransactions();
    TransactionDto createTransaction(int sellerId, BigDecimal amount, PaymentType paymentType);
    Optional<TransactionDto> findTransactionById(int id);

}
