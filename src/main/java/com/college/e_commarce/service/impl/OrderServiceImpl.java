package com.college.e_commarce.service.impl;

import com.college.e_commarce.dto.OrderDto;
import com.college.e_commarce.dto.OrderListDto;
import com.college.e_commarce.dto.OrderProductDto;
import com.college.e_commarce.dto.PaymentRequestDto;
import com.college.e_commarce.entity.*;
import com.college.e_commarce.enums.Status;
import com.college.e_commarce.repository.CartProductRepository;
import com.college.e_commarce.repository.CartRepository;
import com.college.e_commarce.repository.OrderRepository;
import com.college.e_commarce.repository.PaymentRepository;
import com.college.e_commarce.service.OrderService;
import com.college.e_commarce.util.AuthUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartProductRepository cartProductRepository;
    private final PaymentRepository paymentRepository;
    private final AuthUtil authUtil;

    @Override
    @Transactional
    public String orderProduct(OrderProductDto orderProductDto) {

        User owner = authUtil.getCurrentUser();

        Cart cart = cartRepository.findByOwner(owner)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        CartProduct cartProduct1 = cartProductRepository
                .findByIdAndCart(orderProductDto.getCartProductId(), cart)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        Product productExists = cartProduct1.getProduct();

        int remainStock = productExists.getStock() - cartProduct1.getQuantity();
        int soldStock = productExists.getSold() + cartProduct1.getQuantity();
        if(remainStock < 0) {
            throw new RuntimeException("Stock not available");
        }
        productExists.setStock(remainStock);
        productExists.setSold(soldStock);

        BigDecimal totalAmount = productExists.getPrice().multiply(BigDecimal.valueOf(cartProduct1.getQuantity()));

        Order order = Order.builder()
                .name(productExists.getName())
                .price(productExists.getPrice())
                .quantity(cartProduct1.getQuantity())
                .totalAmount(totalAmount)
                .paymentStatus(Status.PENDING)
                .orderstatus(Status.PENDING)
                .createdAt(LocalDateTime.now())
                .product(productExists)
                .buyer(owner)
                .build();

        orderRepository.save(order);

        cartProductRepository.deleteById(orderProductDto.getCartProductId());
        cart.getCartProducts().remove(cartProduct1);

        return "Product order successfully";
    }

    @Override
    public OrderListDto getAllOrders() {
        User owner = authUtil.getCurrentUser();

        List<Order> orders = orderRepository.findAllByBuyer_Id(owner.getId());

        List<OrderDto> orderDtoList = orders.stream().map(order -> OrderDto.builder()
                .id(order.getId())
                .name(order.getName())
                .price(order.getPrice())
                .quantity(order.getQuantity())
                .totalAmount(order.getTotalAmount())
                .paymentStatus(order.getPaymentStatus())
                .orderstatus(order.getOrderstatus())
                .build())
                .toList();

        return OrderListDto.builder().orderList(orderDtoList).build();
    }

    @Override
    public OrderDto getOrder(Long id) {
        Order myOrder = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return  OrderDto.builder()
                .id(myOrder.getId())
                .name(myOrder.getName())
                .price(myOrder.getPrice())
                .quantity(myOrder.getQuantity())
                .totalAmount(myOrder.getTotalAmount())
                .paymentStatus(myOrder.getPaymentStatus())
                .orderstatus(myOrder.getOrderstatus())
                .build();
    }

    @Override
    public void cancelOrder(Long id) {
        Order myOrder = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        orderRepository.delete(myOrder);
    }

    @Override
    @Transactional
    public void payment(PaymentRequestDto dto) {
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Payment payment = Payment.builder()
                .orderId(order)
                .paymentMethod(dto.getPaymentMethod())
                .status("Paid")
                .amount(order.getTotalAmount())
                .paymentDate(LocalDateTime.now())
                .build();

        paymentRepository.save(payment);

        order.setPaymentStatus(Status.DONE);

    }
}
