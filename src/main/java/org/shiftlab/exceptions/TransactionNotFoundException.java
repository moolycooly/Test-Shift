package org.shiftlab.exceptions;

import lombok.Getter;

@Getter
public class TransactionNotFoundException extends RuntimeException{
    private final int id;
    public TransactionNotFoundException(int id){
        super("Transaction Not Found");
        this.id = id;
    }
}
