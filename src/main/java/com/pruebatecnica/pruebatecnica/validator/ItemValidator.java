package com.pruebatecnica.pruebatecnica.validator;

import com.pruebatecnica.pruebatecnica.dto.OrderItemRequest;

public interface ItemValidator {
    void validate(OrderItemRequest item);
}
