package org.shiftlab.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.shiftlab.dto.SellerDto;
import org.shiftlab.exceptions.SellerNotFoundException;
import org.shiftlab.services.SellerService;
import org.shiftlab.services.mapper.EntityDtoMapper;
import org.shiftlab.store.entity.SellerEntity;
import org.shiftlab.store.entity.TransactionEntity;
import org.shiftlab.store.repos.SellerRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SellerServiceImpl implements SellerService{
    private final SellerRepository sellerRepository;
    private final EntityDtoMapper entityDtoMapper;
    private final Clock clock;

    @Override
    @Transactional
    public List<SellerDto> findAllSellers() {
        List<SellerEntity> sellers = sellerRepository.findAll();

        return sellers.stream()
                .map(entityDtoMapper::mapToSellerDto)
                .toList();

    }
    @Override
    @Transactional
    public SellerDto createSeller(String name,String contactInfo) {
        var registrationDate = LocalDateTime.now(clock);
        SellerEntity seller=  sellerRepository
                .save(SellerEntity.builder()
                        .name(name)
                        .registrationDate(registrationDate)
                        .contactInfo(contactInfo)
                        .build());
        return entityDtoMapper.mapToSellerDto(seller);

    }
    @Override
    @Transactional
    public Optional<SellerDto> findSellerById(int id) {
        return sellerRepository
                .findById(id)
                .map(seller -> {
                    var dto = entityDtoMapper.mapToSellerDto(seller);
                    dto.setTransactions(seller.getTransactions().stream().map(entityDtoMapper::mapToTransactionDto).toList());
                    return dto;
                });
    }
    @Override
    @Transactional
    public void updateSeller(int id, String name, String contactInfo) {
        var sellerEntity = sellerRepository
                .findById(id)
                .orElseThrow(()->new SellerNotFoundException(id));
        if(name != null) {
            sellerEntity.setName(name);
        }
        if(contactInfo != null) {
            sellerEntity.setContactInfo(contactInfo);
        }
    }
    @Override
    @Transactional
    public void deleteSellerById(int id) {
        if(!sellerRepository.existsById(id)) {
            throw new SellerNotFoundException(id);
        }
        sellerRepository.deleteById(id);
    }
    @Override
    @Transactional
    public List<SellerDto> findSellersFilteredByDateAndSumma(BigDecimal summa, LocalDateTime timeFrom, LocalDateTime timeTo) {

        return sellerRepository.findAllSellersJoinTransactions().stream().filter((seller)-> seller
                .getTransactions().stream()
                        .filter(transaction -> transaction.getRegistrationDate().isBefore(timeTo)&& transaction.getRegistrationDate().isAfter(timeFrom))
                        .map(TransactionEntity::getAmount)
                        .reduce(BigDecimal.ZERO,BigDecimal::add).compareTo(summa) < 0)
                .map(entityDtoMapper::mapToSellerDto)
                .toList();
    }
    @Override
    @Transactional
    public Optional<SellerDto> findMostProductiveSellerByDate(LocalDateTime timeFrom, LocalDateTime timeTo) {

        return sellerRepository.findAllSellersJoinTransactions().stream()
                .filter(seller -> {
                    var transactionCount = seller.getTransactions().stream()
                            .filter(transaction -> transaction.getRegistrationDate().isBefore(timeTo)
                                    && transaction.getRegistrationDate().isAfter(timeFrom))
                            .count();
                    return transactionCount > 0;
                }).max(Comparator.comparing((seller)-> seller
                        .getTransactions().stream()
                        .filter(transaction -> transaction.getRegistrationDate().isBefore(timeTo)&& transaction.getRegistrationDate().isAfter(timeFrom))
                        .map(TransactionEntity::getAmount)
                        .reduce(BigDecimal.ZERO,BigDecimal::add)))
                .map(entityDtoMapper::mapToSellerDto);
    }

}
