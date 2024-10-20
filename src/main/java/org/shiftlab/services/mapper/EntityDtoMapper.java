package org.shiftlab.services.mapper;

import org.shiftlab.dto.SellerDto;
import org.shiftlab.dto.TransactionDto;
import org.shiftlab.store.entity.SellerEntity;
import org.shiftlab.store.entity.TransactionEntity;
import org.springframework.stereotype.Component;

@Component
public class EntityDtoMapper {
    public TransactionDto mapToTransactionDto(TransactionEntity entity) {
        return TransactionDto.builder()
                .id(entity.getId())
                .transactionDate(entity.getRegistrationDate())
                .amount(entity.getAmount())
                .paymentType(entity.getPaymentType())
                .sellerId(entity.getSeller().getId())
                .build();

    }
    public SellerDto mapToSellerDto(SellerEntity entity) {
        return SellerDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .registrationDate(entity.getRegistrationDate())
                .contactInfo(entity.getContactInfo())
//                .transactions(entity.getTransactions().stream().map(this::mapToTransactionDto).toList())
                .build();
    }
}
