package org.shiftlab.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.shiftlab.dto.SellerDto;
import org.shiftlab.exceptions.SellerNotFoundException;
import org.shiftlab.services.impl.SellerServiceImpl;
import org.shiftlab.services.mapper.EntityDtoMapper;
import org.shiftlab.store.entity.SellerEntity;
import org.shiftlab.store.repos.SellerRepository;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SellerServiceImplTest {

    @Mock
    private SellerRepository sellerRepository;
    @InjectMocks
    private SellerServiceImpl sellerService;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.systemUTC();
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


    SellerEntity getAnySellerEntity() {
        return SellerEntity.builder()
                .id(1)
                .contactInfo("123-123-123")
                .name("Alexander M")
                .transactions(List.of())
                .build();
    }

}
