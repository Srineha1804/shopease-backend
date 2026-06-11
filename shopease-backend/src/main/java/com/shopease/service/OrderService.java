package com.shopease.service;

import com.shopease.entity.*;
import com.shopease.repository.CartItemRepository;
import com.shopease.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;

    public Order placeOrder(User buyer, String shippingAddress) {
        List<CartItem> cartItems = cartItemRepository.findByUser(buyer);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty!");
        }

        Order order = new Order();
        order.setBuyer(buyer);
        order.setShippingAddress(shippingAddress);

        List<OrderItem> orderItems = cartItems.stream().map(ci -> {
            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(ci.getProduct());
            oi.setQuantity(ci.getQuantity());
            oi.setPrice(ci.getProduct().getPrice());
            return oi;
        }).collect(Collectors.toList());

        order.setItems(orderItems);
        order.setTotalAmount(
                orderItems.stream()
                        .mapToDouble(i -> i.getPrice() * i.getQuantity())
                        .sum()
        );

        Order saved = orderRepository.save(order);
        cartItemRepository.deleteByUser(buyer);
        return saved;
    }

    public List<Order> getMyOrders(User buyer) {
        return orderRepository.findByBuyer(buyer);
    }

    public Order getOrderById(Long id, User user) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (!order.getBuyer().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized!");
        }
        return order;
    }

    public Order updateStatus(Long id, Order.OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        return orderRepository.save(order);
    }
    public void cancelOrder(Long id, User user) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (!order.getBuyer().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized!");
        }
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new RuntimeException(
                    "Only PENDING orders can be cancelled!");
        }
        order.setStatus(Order.OrderStatus.CANCELLED);
        orderRepository.save(order);
    }
    public boolean hasOrdered(User buyer, Long productId) {
        return orderRepository.findByBuyer(buyer)
                .stream()
                .anyMatch(o -> o.getItems().stream()
                        .anyMatch(i -> i.getProduct().getId()
                                .equals(productId)));
    }

    public List<Order> getSellerOrders(Long sellerId) {
        return orderRepository.findByItemsProductSellerId(sellerId);
    }
}