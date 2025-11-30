package com.pruebatecnica.pruebatecnica.validator;

import com.pruebatecnica.pruebatecnica.dto.CreateOrderRequest;

public interface OrderValidator {
    void validate(CreateOrderRequest request);
}
