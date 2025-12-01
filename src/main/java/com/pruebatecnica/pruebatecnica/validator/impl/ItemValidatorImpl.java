package com.pruebatecnica.pruebatecnica.validator.impl;

import com.pruebatecnica.pruebatecnica.dto.OrderItemRequest;
import com.pruebatecnica.pruebatecnica.validator.ItemValidator;
import org.springframework.stereotype.Component;

@Component
public class ItemValidatorImpl implements ItemValidator {

    @Override
    public void validate(OrderItemRequest item) {
        if (item.getProductId() == null)
            throw new IllegalArgumentException("Product ID is required");
        if (item.getQuantity() == null || item.getQuantity() <= 0)
            throw new IllegalArgumentException("Quantity must be greater than 0");
    }
}
