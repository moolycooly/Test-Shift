package org.shiftlab.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.shiftlab.dto.PaymentType;
import org.shiftlab.dto.TransactionDto;
import org.shiftlab.exceptions.SellerNotFoundException;
import org.shiftlab.services.impl.TransactionServiceImpl;
import org.shiftlab.services.mapper.EntityDtoMapper;
import org.shiftlab.store.entity.SellerEntity;
import org.shiftlab.store.entity.TransactionEntity;
import org.shiftlab.store.repos.SellerRepository;
import org.shiftlab.store.repos.TransactionRepository;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceImplTest {
    @Mock
    TransactionRepository transactionRepository;
    @Mock
    SellerRepository sellerRepository;
    @InjectMocks
    TransactionServiceImpl transactionServiceImpl;

    @BeforeEach
    void setUp() {
        EntityDtoMapper entityDtoMapper = new EntityDtoMapper();
        Clock clock = Clock.systemUTC();
        transactionServiceImpl=new TransactionServiceImpl(transactionRepository,sellerRepository,entityDtoMapper,clock);
    }
    @Test
    void findAllTransactions_TransactionsExist_ReturnsTransaction() {
        //given
        var transactions = IntStream.range(1, 4)
                .mapToObj(i -> TransactionEntity
                        .builder()
                        .id(i)
                        .amount(BigDecimal.valueOf(new Random().nextDouble()))
                        .seller(getAnySeller())
                        .build())
                .toList();
        var transactionsDto = IntStream.range(1, 4)
                .mapToObj(i -> TransactionDto
                        .builder()
                        .id(i)
                        .amount(transactions.get(i-1).getAmount())
                        .sellerId(1)
                        .build())
                .toList();
        when(transactionRepository.findAll()).thenReturn(transactions);

        //when
        var result = transactionServiceImpl.findAllTransactions();
        //then
        assertEquals(transactionsDto, result);
    }
    @Test
    void findAllTransactions_TransactionNotExist_ReturnsEmptyList() {
        //given
        when(transactionRepository.findAll()).thenReturn(List.of());
        //when
        var result = transactionServiceImpl.findAllTransactions();
        //then
        assertTrue(result.isEmpty());
    }
    @Test
    void createTransaction_SellerExist_Successfully() {
        //given
        when(sellerRepository.findById(1)).thenReturn(Optional.of(getAnySeller()));
        when(transactionRepository.save(TransactionEntity.builder()
                        .seller(getAnySeller())
                        .registrationDate(any())
                        .paymentType(PaymentType.TRANSFER)
                        .amount(BigDecimal.valueOf(100.5))
                        .build()))
                .thenReturn(TransactionEntity.builder()
                        .id(1)
                        .seller(getAnySeller())
                        .registrationDate(any())
                        .paymentType(PaymentType.TRANSFER)
                        .amount(BigDecimal.valueOf(100.5))
                        .build());
        var dto = TransactionDto.builder()
                .amount(BigDecimal.valueOf(100.5))
                .paymentType(PaymentType.TRANSFER)
                .id(1)
                .sellerId(1)
                .build();
        //when
        var result = transactionServiceImpl.createTransaction(1,BigDecimal.valueOf(100.5), PaymentType.TRANSFER);
        //then
        assertEquals(dto, result);

    }
    @Test
    void createTransaction_SellerNotExist_ReturnSellerNotFoundException() {
        //given
        when(sellerRepository.findById(1)).thenReturn(Optional.empty());

        //then
        assertThrows(SellerNotFoundException.class, ()->transactionServiceImpl.createTransaction(1,BigDecimal.valueOf(100.5), PaymentType.TRANSFER));

    }
    @Test
    void findTransactionById_TransactionExist_ReturnsOptionalTransactionDto() {
        //given
        when(transactionRepository.findById(1)).thenReturn(Optional.of(getAnyTransaction()));
        //when
        var result = transactionServiceImpl.findTransactionById(1);
        //then
        var dto = TransactionDto.builder()
                .id(1)
                .amount(BigDecimal.valueOf(100.5))
                .paymentType(PaymentType.TRANSFER)
                .sellerId(1)
                .build();

        assertEquals(Optional.of(dto), result);
    }

    SellerEntity getAnySeller() {

        return SellerEntity.builder()
                .id(1)
                .name("Alexander M")
                .contactInfo("212-521-122")
                .build();
    }
    TransactionEntity getAnyTransaction() {
        return TransactionEntity.builder()
                .id(1)
                .seller(getAnySeller())
                .paymentType(PaymentType.TRANSFER)
                .amount(BigDecimal.valueOf(100.5))
                .build();
    }


}
