package org.shiftlab.controllers.payload;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NewSellerPayload(@NotNull @Size(min=3, max=50) String name, @Size(min=5,max=100) String contactInfo) {
}
