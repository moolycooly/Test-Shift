package org.shiftlab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BestPeriod {
    private LocalDate start;
    private LocalDate end;
    private int count;
}
