package org.shiftlab.services;

import org.shiftlab.dto.BestPeriod;
import org.shiftlab.dto.SellerDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SellerService {
    List<SellerDto> findAllSellers();
    SellerDto createSeller(String name,String contactInfo);
    Optional<SellerDto> findSellerById(int id);
    void updateSeller(int id, String name, String contactInfo);
    void deleteSellerById(int id);
    List<SellerDto> findSellersFilteredByDateAndSumma(BigDecimal summa, LocalDateTime timeFrom, LocalDateTime timeTo);
    Optional<SellerDto> findMostProductiveSellerByDate(LocalDateTime timeFrom, LocalDateTime timeTo);
    BestPeriod findBestPeriodOfSeller(int id);
}
