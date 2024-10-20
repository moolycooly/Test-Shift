package org.shiftlab.store.entity;

import jakarta.persistence.*;
import lombok.*;
import org.shiftlab.dto.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(exclude = {"seller"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(schema = "sales_management", name="transaction")

public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false)
    private PaymentType paymentType;


    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime registrationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private SellerEntity seller;


}
