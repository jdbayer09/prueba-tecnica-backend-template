package com.pruebatecnica.pruebatecnica.service;

import com.pruebatecnica.pruebatecnica.dto.CreateOrderRequest;
import com.pruebatecnica.pruebatecnica.dto.OrderItemRequest;
import com.pruebatecnica.pruebatecnica.model.Order;
import com.pruebatecnica.pruebatecnica.model.OrderStatus;
import com.pruebatecnica.pruebatecnica.model.Product;
import com.pruebatecnica.pruebatecnica.policy.DiscountPolicy;
import com.pruebatecnica.pruebatecnica.repository.OrderRepository;
import com.pruebatecnica.pruebatecnica.service.impl.OrderServiceImpl;
import com.pruebatecnica.pruebatecnica.validator.ItemValidator;
import com.pruebatecnica.pruebatecnica.validator.OrderValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductService productService;

    @Mock
    private OrderValidator orderValidator;

    @Mock
    private ItemValidator itemValidator;

    @Mock
    private DiscountPolicy discountPolicy;

    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(orderService, "discountPolicies", List.of(discountPolicy));
    }

    /* ---------------------------------------------------------
     TEST 1 - NO aplica descuento si hay 3 o menos productos
     ---------------------------------------------------------*/
    @Test
    void testCreateOrderWithoutDiscount_ShouldNotApplyVarietyDiscount() {
        CreateOrderRequest request = new CreateOrderRequest(
                "Julian Bayer", "jdbayer@example.com",
                List.of(
                        new OrderItemRequest(1L, 1),
                        new OrderItemRequest(2L, 1),
                        new OrderItemRequest(3L, 1)
                )
        );

        Product p1 = new Product(1L, "Prod1", BigDecimal.TEN, 10);
        Product p2 = new Product(2L, "Prod2", BigDecimal.TEN, 10);
        Product p3 = new Product(3L, "Prod3", BigDecimal.TEN, 10);

        doNothing().when(orderValidator).validate(request);
        doNothing().when(itemValidator).validate(any());

        when(productService.reserveStock(1L, 1)).thenReturn(p1);
        when(productService.reserveStock(2L, 1)).thenReturn(p2);
        when(productService.reserveStock(3L, 1)).thenReturn(p3);

        when(discountPolicy.apply(any(), any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderService.createOrder(request);

        assertEquals(BigDecimal.valueOf(30), result.getTotalAmount());
        assertEquals(3, result.getItems().size());
        assertEquals(OrderStatus.CONFIRMED, result.getStatus());

        verify(discountPolicy).apply(BigDecimal.valueOf(30), result.getItems());
    }

    /* ---------------------------------------------------------
     TEST 2 - Aplica descuento cuando hay 4 o más productos
     ---------------------------------------------------------*/
    @Test
    void testCreateOrderWithDiscount_ShouldApplyVarietyDiscount() {
        CreateOrderRequest request = new CreateOrderRequest(
                "Julian Bayer", "jdbayer@example.com",
                List.of(
                        new OrderItemRequest(1L, 1),
                        new OrderItemRequest(2L, 1),
                        new OrderItemRequest(3L, 1),
                        new OrderItemRequest(4L, 1)
                )
        );

        Product p1 = new Product(1L, "Prod1", BigDecimal.TEN, 10);
        Product p2 = new Product(2L, "Prod2", BigDecimal.TEN, 10);
        Product p3 = new Product(3L, "Prod3", BigDecimal.TEN, 10);
        Product p4 = new Product(4L, "Prod4", BigDecimal.TEN, 10);

        doNothing().when(orderValidator).validate(request);
        doNothing().when(itemValidator).validate(any());

        when(productService.reserveStock(1L, 1)).thenReturn(p1);
        when(productService.reserveStock(2L, 1)).thenReturn(p2);
        when(productService.reserveStock(3L, 1)).thenReturn(p3);
        when(productService.reserveStock(4L, 1)).thenReturn(p4);

        BigDecimal initialTotal = BigDecimal.valueOf(40);
        BigDecimal expected = initialTotal.multiply(BigDecimal.valueOf(0.90));

        when(discountPolicy.apply(any(), any()))
                .thenReturn(expected);

        when(orderRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        Order result = orderService.createOrder(request);

        assertEquals(expected, result.getTotalAmount());
        verify(discountPolicy).apply(initialTotal, result.getItems());
    }

    /* ---------------------------------------------------------
     TEST 3 - NO aplica descuento si es un solo producto repetido
     ---------------------------------------------------------*/
    @Test
    void testCreateOrderWithSameProductMultipleTimes_ShouldNotApplyDiscount() {
        CreateOrderRequest request = new CreateOrderRequest(
                "Julian Bayer", "jdbayer@example.com",
                List.of(new OrderItemRequest(1L, 10))
        );

        Product product = new Product(1L, "Manzana", BigDecimal.TEN, 100);

        doNothing().when(orderValidator).validate(request);
        doNothing().when(itemValidator).validate(any());

        when(productService.reserveStock(1L, 10))
                .thenReturn(product);

        BigDecimal initialTotal = BigDecimal.valueOf(100);

        when(discountPolicy.apply(any(), any()))
                .thenAnswer(inv -> inv.getArgument(0));

        when(orderRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        Order result = orderService.createOrder(request);

        assertEquals(initialTotal, result.getTotalAmount());
        verify(discountPolicy).apply(initialTotal, result.getItems());
    }

    /* ---------------------------------------------------------
     TEST 4 - Test básico general del servicio
     ---------------------------------------------------------*/
    @Test
    void testCreateBasicOrder() {
        Product product1 = new Product(1L, "Test Product", BigDecimal.valueOf(10), 5);

        doNothing().when(orderValidator).validate(any());
        doNothing().when(itemValidator).validate(any());

        when(productService.reserveStock(1L, 2))
                .thenReturn(product1);

        when(discountPolicy.apply(any(), any()))
                .thenAnswer(inv -> inv.getArgument(0));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        OrderItemRequest item = new OrderItemRequest(1L, 2);
        CreateOrderRequest request = new CreateOrderRequest(
                "John Doe", "john@test.com", List.of(item)
        );

        Order result = orderService.createOrder(request);

        assertNotNull(result);
        assertEquals("John Doe", result.getCustomerName());
        assertEquals(BigDecimal.valueOf(20), result.getTotalAmount());
    }
}
