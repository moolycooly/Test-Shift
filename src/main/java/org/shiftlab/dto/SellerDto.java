package org.shiftlab.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SellerDto {
    private Integer id;
    private String name;
    private String contactInfo;
    private LocalDateTime registrationDate;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<TransactionDto> transactions;
}
