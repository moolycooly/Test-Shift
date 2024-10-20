package org.shiftlab.controllers.payload;

import jakarta.validation.constraints.Size;

public record UpdateSellerPayload(@Size(min=3,max =50) String name, @Size(min=5,max=100) String contactInfo) {
}
