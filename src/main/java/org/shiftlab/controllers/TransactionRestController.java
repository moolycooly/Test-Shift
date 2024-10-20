package org.shiftlab.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.shiftlab.controllers.payload.NewTransactionPayload;
import org.shiftlab.dto.PaymentType;
import org.shiftlab.dto.TransactionDto;
import org.shiftlab.exceptions.TransactionNotFoundException;
import org.shiftlab.services.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionRestController {
    private final TransactionService transactionalService;


    @GetMapping
    public List<TransactionDto> getAllTransactions(){
        return transactionalService.findAllTransactions();

    }
    @GetMapping("/{id}")
    public TransactionDto getTransactionById(@PathVariable(name="id") int id){
        return transactionalService.findTransactionById(id).orElseThrow(()->new TransactionNotFoundException(id));

    }
    @PostMapping
    public ResponseEntity<TransactionDto> createTransaction(@RequestBody @Valid NewTransactionPayload newTransactionPayload){
        PaymentType paymentType;
        try {
            paymentType = PaymentType.valueOf(newTransactionPayload.paymentType().toUpperCase());
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid payment type");
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionalService.createTransaction(
                                newTransactionPayload.sellerId(),
                                newTransactionPayload.amount(),
                                paymentType));

    }
}
