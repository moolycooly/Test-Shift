package org.shiftlab.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.shiftlab.dto.PaymentType;
import org.shiftlab.dto.TransactionDto;
import org.shiftlab.exceptions.SellerNotFoundException;
import org.shiftlab.services.TransactionService;
import org.shiftlab.services.mapper.EntityDtoMapper;
import org.shiftlab.store.entity.TransactionEntity;
import org.shiftlab.store.repos.SellerRepository;
import org.shiftlab.store.repos.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final SellerRepository sellerRepository;
    private final EntityDtoMapper entityDtoMapper;
    private final Clock clock;

    @Override
    @Transactional
    public List<TransactionDto> findAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(entityDtoMapper::mapToTransactionDto)
                .toList();
    }
    @Override
    @Transactional
    public TransactionDto createTransaction(int sellerId, BigDecimal amount, PaymentType paymentType) {
        var registrationDate = LocalDateTime.now(clock);
        TransactionEntity entity = transactionRepository.save(TransactionEntity
                .builder()
                .seller(sellerRepository.findById(sellerId).orElseThrow(()->new SellerNotFoundException(sellerId)) )
                .registrationDate(registrationDate)
                .paymentType(paymentType)
                .amount(amount)
                .build());

        return  entityDtoMapper.mapToTransactionDto(entity);

    }
    @Override
    @Transactional
    public Optional<TransactionDto> findTransactionById(int id) {
        return transactionRepository.findById(id).map(entityDtoMapper::mapToTransactionDto);
    }




}
