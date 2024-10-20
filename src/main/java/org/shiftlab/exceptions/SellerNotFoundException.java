package org.shiftlab.exceptions;

import lombok.Getter;
import org.shiftlab.controllers.payload.Period;

@Getter
public class SellerNotFoundException extends RuntimeException{
    private Integer id;
    private Period period;
    public SellerNotFoundException(Integer id){
        super("Seller not found");
        this.id = id;
    }
    public SellerNotFoundException(Period period){
        super("Seller not found");
        this.period = period;
    }
    public SellerNotFoundException(Integer id, Period period){
        super("Seller not found");
        this.id = id;
        this.period = period;
    }
}
