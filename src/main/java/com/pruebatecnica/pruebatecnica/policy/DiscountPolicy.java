package com.pruebatecnica.pruebatecnica.policy;

import com.pruebatecnica.pruebatecnica.model.OrderItem;

import java.math.BigDecimal;
import java.util.List;

public interface DiscountPolicy {
    BigDecimal apply(BigDecimal total, List<OrderItem> items);
}
