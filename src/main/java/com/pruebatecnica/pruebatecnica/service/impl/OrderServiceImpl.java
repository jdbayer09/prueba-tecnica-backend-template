package com.pruebatecnica.pruebatecnica.service.impl;

import com.pruebatecnica.pruebatecnica.dto.CreateOrderRequest;
import com.pruebatecnica.pruebatecnica.dto.OrderItemRequest;
import com.pruebatecnica.pruebatecnica.model.Order;
import com.pruebatecnica.pruebatecnica.model.OrderItem;
import com.pruebatecnica.pruebatecnica.model.OrderStatus;
import com.pruebatecnica.pruebatecnica.model.Product;
import com.pruebatecnica.pruebatecnica.policy.DiscountPolicy;
import com.pruebatecnica.pruebatecnica.repository.OrderRepository;
import com.pruebatecnica.pruebatecnica.service.OrderService;
import com.pruebatecnica.pruebatecnica.service.ProductService;
import com.pruebatecnica.pruebatecnica.validator.ItemValidator;
import com.pruebatecnica.pruebatecnica.validator.OrderValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private OrderValidator orderValidator;
    @Autowired
    private ItemValidator itemValidator;
    @Autowired
    private List<DiscountPolicy> discountPolicies;

    @Override
    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        orderValidator.validate(request);
        Order order = new Order(request.getCustomerName(), request.getCustomerEmail());
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal total = calculateTotal(orderItems, order, request.getItems());

        for (DiscountPolicy discountPolicy : discountPolicies)
            total = discountPolicy.apply(total, orderItems);

        order.setItems(orderItems);
        order.setTotalAmount(total);
        order.setStatus(OrderStatus.CONFIRMED);
        
        return orderRepository.save(order);
    }

    private BigDecimal calculateTotal(List<OrderItem> orderItems,Order order, List<OrderItemRequest> items) {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemRequest itemRequest : items) {
            itemValidator.validate(itemRequest);

            Product product = productService.reserveStock(
                    itemRequest.getProductId(),
                    itemRequest.getQuantity()
            );

            OrderItem orderItem = new OrderItem(product, itemRequest.getQuantity());
            orderItem.setOrder(order);
            orderItems.add(orderItem);

            total = total.add(
                    product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()))
            );
        }
        return total;
    }

    @Override
    @Transactional(readOnly = true)
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
