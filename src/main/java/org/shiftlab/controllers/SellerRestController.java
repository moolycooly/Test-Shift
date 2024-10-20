package org.shiftlab.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shiftlab.controllers.payload.NewSellerPayload;
import org.shiftlab.controllers.payload.Period;
import org.shiftlab.controllers.payload.UpdateSellerPayload;
import org.shiftlab.dto.SellerDto;
import org.shiftlab.exceptions.SellerNotFoundException;
import org.shiftlab.services.SellerService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/seller")
@RequiredArgsConstructor
@Slf4j
public class SellerRestController {
    private final SellerService sellerService;

    @GetMapping
    public List<SellerDto> getAllSellers(){
        return sellerService.findAllSellers();

    }
    @GetMapping("/{id}")
    public SellerDto getSellerById(@RequestParam(name = "transactions",required = false) boolean flag, @PathVariable(name = "id") int id) {
        SellerDto seller = sellerService.findSellerById(id).orElseThrow(()->new SellerNotFoundException(id));
        if(!flag) seller.setTransactions(null);
        return seller;
    }
    @GetMapping("/less-then-summa")
    public List<SellerDto> getSellersTransactionsAmountLessThenSumma(
            @RequestParam(name="summa") Double budget,
            @RequestParam(name="dateFrom") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(name="dateTo")  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {

        LocalDateTime timeFrom = dateFrom.atStartOfDay();
        LocalDateTime timeTo = dateTo.atTime(23,59,59);
        return sellerService.findSellersFilteredByDateAndSumma(BigDecimal.valueOf(budget),timeFrom,timeTo);

    }
    @GetMapping("/most-productive")
    public SellerDto getMostProductiveSellerInPeriod(@RequestParam(name="period") String per) {
        Period period;
        try {
            period = Period.fromString(per);
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid period: " + per);
        }
        LocalDateTime timeFrom = period.getStartDate();
        LocalDateTime timeTo = LocalDateTime.now();

        return sellerService.findMostProductiveSellerByDate(timeFrom,timeTo).orElseThrow(()->new SellerNotFoundException(period));

    }

    @PostMapping
    public ResponseEntity<SellerDto> createSeller(@RequestBody @Valid NewSellerPayload newSellerPayload) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(sellerService.createSeller(newSellerPayload.name(), newSellerPayload.contactInfo()));
    }

    @PutMapping("/{id}")
    public void updateSeller(@PathVariable(name = "id") int id,@RequestBody @Valid UpdateSellerPayload updateSellerPayload) {
        sellerService.updateSeller(id, updateSellerPayload.name(),updateSellerPayload.contactInfo());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSeller(@PathVariable(name = "id") int id) {
        sellerService.deleteSellerById(id);
        return ResponseEntity.noContent().build();
    }



}
