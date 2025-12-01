package com.pruebatecnica.pruebatecnica.policy.impl;

import com.pruebatecnica.pruebatecnica.model.OrderItem;
import com.pruebatecnica.pruebatecnica.policy.DiscountPolicy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class VarietyDiscountPolicy implements DiscountPolicy {

    @Override
    public BigDecimal apply(BigDecimal total, List<OrderItem> items) {
        long uniqueProducts = items.stream()
                .map(item -> item.getProduct().getId())
                .distinct()
                .count();
        if (uniqueProducts > 3)
            return total.multiply(BigDecimal.valueOf(0.90));
        return total;
    }
}
