package org.shiftlab.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.shiftlab.dto.PaymentType;
import org.shiftlab.dto.SellerDto;
import org.shiftlab.exceptions.SellerNotFoundException;
import org.shiftlab.services.impl.SellerServiceImpl;
import org.shiftlab.services.mapper.EntityDtoMapper;
import org.shiftlab.store.entity.SellerEntity;
import org.shiftlab.store.entity.TransactionEntity;
import org.shiftlab.store.repos.SellerRepository;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SellerServiceImplTest {

    @Mock
    private SellerRepository sellerRepository;
    @Mock
    private Clock clock;
    @InjectMocks
    private SellerServiceImpl sellerService;

    @BeforeEach
    void setUp(){
        clock = Clock.systemUTC();
        EntityDtoMapper entityDtoMapper = new EntityDtoMapper();
        sellerService=new SellerServiceImpl(sellerRepository,entityDtoMapper,clock);
    }
    @Test
    void findAllSellers_SellersExist_ReturnListSellerDto() {
        //given
        var sellers = IntStream.range(1, 4)
                .mapToObj(i -> SellerEntity
                        .builder()
                        .id(i)
                        .name(String.format("Seller: %d",i))
                        .build())
                .toList();
        var sellersDto = IntStream.range(1, 4)
                .mapToObj(i -> SellerDto
                        .builder()
                        .id(i)
                        .name(String.format("Seller: %d",i))
                        .build())
                .toList();
        when(sellerRepository.findAll()).thenReturn(sellers);

        //when
        var result = sellerService.findAllSellers();
        //then
        assertEquals(sellersDto, result);
    }
    @Test
    void findAllSellers_SellersNotExist_ReturnEmptyListSellerDto() {
        //given
        when(sellerRepository.findAll()).thenReturn(List.of());
        //when
        var result = sellerService.findAllSellers();
        //then
        assertTrue(result.isEmpty());
    }
    @Test
    void createSeller__ReturnsSellerDto() {
        //given
        when(sellerRepository.save(SellerEntity.builder()
                .name("Alexander M")
                .registrationDate(any())
                .contactInfo("123-123-123")
                .build()))
                .thenReturn(SellerEntity.builder()
                        .id(1)
                        .name("Alexander M")
                        .registrationDate(any())
                        .contactInfo("123-123-123")
                        .build());
        var dto = SellerDto.builder()
                .id(1)
                .name("Alexander M")
                .contactInfo("123-123-123")
                .build();
        //when
        var result = sellerService.createSeller("Alexander M", "123-123-123");
        //then
        assertEquals(dto, result);

    }
    @Test
    void findSellerById_SellersExist_ReturnNotEmptyOptionalSellerDto() {
        //given
        when(sellerRepository.findById(1)).thenReturn(Optional.of(getAnySellerEntity()));
        //when
        var result = sellerService.findSellerById(1);
        //then
        var dto = SellerDto.builder()
                .id(1)
                .name("Alexander M")
                .contactInfo("123-123-123")
                .transactions(List.of())
                .build();
        assertEquals(Optional.of(dto), result);

    }
    @Test
    void findSellerById_SellersNotExist_ReturnEmptyOptionalSellerDto() {
        //given
        when(sellerRepository.findById(1)).thenReturn(Optional.empty());

        //when
        var result = sellerService.findSellerById(1);
        //then
        assertTrue(result.isEmpty());

    }

    @Test
    void updateSeller_SellersExist_Successful() {
        //given
        var entity = getAnySellerEntity();
        when(sellerRepository.findById(1)).thenReturn(Optional.of(entity));
        //when
        sellerService.updateSeller(1,"Alexander V", "555-555-555");
        //then
        assertEquals(entity.getName(), "Alexander V");
        assertEquals(entity.getContactInfo(), "555-555-555");
    }
    @Test
    void updateSeller_SellerNotExist_ReturnsSellerNotFoundException() {
        //given
        when(sellerRepository.findById(1)).thenReturn(Optional.empty());
        //then
        assertThrows(SellerNotFoundException.class,
                ()->{sellerService.updateSeller(1,"Alexander V", "555-555-555");});
    }
    @Test
    void findSellersFilteredByDateAndSumma_Sellers_Exist_ReturnListSellerDto() {
        //given
        var sellers = IntStream.range(1, 4)
                .mapToObj(i -> SellerEntity
                        .builder()
                        .id(i)
                        .name(String.format("Seller: %d",i))
                        .transactions(List.of())
                        .build())
                .toList();
        when(sellerRepository.findAllSellersJoinTransactions()).thenReturn(sellers);

        //when
        var result = sellerService.findSellersFilteredByDateAndSumma(BigDecimal.valueOf(15.5),
                LocalDateTime.now().minusMonths(1),
                LocalDateTime.now());
        //then
        assertTrue(result.size()==sellers.size());
    }
    @Test
    void findMostProductiveSellerByDate_SellerExist_ReturnNotEmptyOptionalSellerDto() {
        //given
        var sellers = IntStream.range(1, 4)
                .mapToObj(i -> SellerEntity
                        .builder()
                        .id(i)
                        .name(String.format("Seller: %d",i))
                        .build())
                .toList();
        BigDecimal max =  BigDecimal.valueOf(0);
        int id = 0;
        for(var seller : sellers) {
            var transactions = getTransactionEntityList(seller);
            seller.setTransactions(transactions);
            var summa = BigDecimal.valueOf(0);
            for(var transaction : transactions) {
                summa=summa.add(transaction.getAmount());
            }
            if(max.compareTo(summa) < 0) {
                id = seller.getId();
                max = summa;
            }

        }
        when(sellerRepository.findAllSellersJoinTransactions()).thenReturn(sellers);
        //when
        var result = sellerService.findMostProductiveSellerByDate(
                LocalDateTime.of(2021,10,10,10,10),
                LocalDateTime.of(2024,12,31,10,10));

        //then
        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());

    }
    @Test
    void findMostProductiveSellerByDate_SellerNotExist_ReturnEmptyOptionalSellerDto() {
        //given

        when(sellerRepository.findAllSellersJoinTransactions()).thenReturn(List.of());
        //when
        var result = sellerService.findMostProductiveSellerByDate(LocalDateTime.now().minusMonths(1),
                LocalDateTime.now());
        //then
        assertTrue(result.isEmpty());

    }

    @Test
    void deleteById_SellerNotExists_ReturnSellerNotFoundException() {

        //given
        when(sellerRepository.existsById(1)).thenReturn(false);
        //then
        assertThrows(SellerNotFoundException.class,()->sellerService.deleteSellerById(1));
    }
    @Test
    void deleteById_SellerExists_ReturnNothing() {

        //given
        when(sellerRepository.existsById(1)).thenReturn(true);
        //then
        sellerService.deleteSellerById(1);

    }


    SellerEntity getAnySellerEntity() {
        return SellerEntity.builder()
                .id(1)
                .contactInfo("123-123-123")
                .name("Alexander M")
                .transactions(List.of())
                .build();
    }
    List<TransactionEntity> getTransactionEntityList(SellerEntity sellerEntity) {
        var rand = new Random();
        return IntStream.range(1,rand.nextInt(1,4))
                .mapToObj(i -> TransactionEntity
                        .builder()
                        .id(i)
                        .amount(BigDecimal.valueOf(rand.nextDouble(10,500)))
                        .registrationDate(LocalDateTime.of(2024,10,10,12,0))
                        .seller(sellerEntity)
                        .paymentType(PaymentType.TRANSFER)
                        .build()).toList();
    }

}
