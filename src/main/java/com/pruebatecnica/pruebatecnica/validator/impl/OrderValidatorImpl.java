package com.pruebatecnica.pruebatecnica.validator.impl;

import com.pruebatecnica.pruebatecnica.dto.CreateOrderRequest;
import com.pruebatecnica.pruebatecnica.validator.OrderValidator;
import org.springframework.stereotype.Component;

@Component
public class OrderValidatorImpl implements OrderValidator {
    public void validate(CreateOrderRequest request) {
        if (isBlank(request.getCustomerName()))
            throw new IllegalArgumentException("Customer name is required");
        if (isBlank(request.getCustomerEmail()))
            throw new IllegalArgumentException("Customer email is required");
        if (request.getItems() == null || request.getItems().isEmpty())
            throw new IllegalArgumentException("Order items are required");
    }

    private boolean isBlank(String v) {
        return v == null || v.trim().isEmpty();
    }
}
