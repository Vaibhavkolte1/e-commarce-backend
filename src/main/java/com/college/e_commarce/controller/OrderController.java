package com.college.e_commarce.controller;


import com.college.e_commarce.dto.*;
import com.college.e_commarce.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/order-product")
    public ResponseEntity<String> orderProduct(@Valid @RequestBody OrderProductDto orderProductDto) {
        return ResponseEntity.ok(orderService.orderProduct(orderProductDto));
    }

    @GetMapping("/get-all")
    public ResponseEntity<OrderListDto> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrder(id));
    }

    @DeleteMapping("/cancel/{id}")
    public ResponseEntity<String> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.ok("Order was canceled.");
    }

    @PostMapping("/payment")
    public ResponseEntity<String> payment(@Valid @RequestBody PaymentRequestDto paymentRequestDto) {
        orderService.payment(paymentRequestDto);
        return ResponseEntity.ok("Payment Done.");
    }
}
