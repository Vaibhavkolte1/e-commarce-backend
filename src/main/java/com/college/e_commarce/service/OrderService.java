package com.college.e_commarce.service;

import com.college.e_commarce.dto.*;

public interface OrderService {

    String orderProduct(OrderProductDto orderProductDto);

    OrderListDto getAllOrders();

    OrderDto getOrder(Long id);

    void cancelOrder(Long id);

    void payment(PaymentRequestDto dto);
}
