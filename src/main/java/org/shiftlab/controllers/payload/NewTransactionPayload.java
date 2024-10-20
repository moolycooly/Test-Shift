package org.shiftlab.controllers.payload;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record NewTransactionPayload(@NotNull Integer sellerId, @Positive @NotNull BigDecimal amount, @NotNull String paymentType) {
}
