package com.wsd.restaurant.controller;

import com.wsd.restaurant.dto.OrderDTO;
import com.wsd.restaurant.dto.response.PageInfo;
import com.wsd.restaurant.dto.response.Response;
import com.wsd.restaurant.service.OrderService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
  private final Logger log = LoggerFactory.getLogger(OrderController.class);

  private final OrderService orderService;

  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  @GetMapping("/today")
  public ResponseEntity<Response<List<OrderDTO>>> getTodayOrders(Pageable pageable) {
    Page<OrderDTO> orderDTOS = orderService.getTodayOrders(pageable);
    return ResponseEntity.ok().body(new Response(orderDTOS.getContent(), PageInfo.of(orderDTOS)));
  }

  @GetMapping("/total-sell/today")
  public ResponseEntity<Response<Map<String, Object>>> findTodayTotalSaleAmount() {
    Double totalSell = orderService.findTodayTotalSaleAmount();
    Map<String, Object> response = new HashMap<>();
    response.put("totalSell", totalSell);
    return ResponseEntity.ok().body(new Response(response, PageInfo.of(null)));
  }

  @GetMapping("/customers/{userId}")
  public ResponseEntity<Response<List<OrderDTO>>> getCustomerOrders(@PathVariable("userId") Long userId) {
    List<OrderDTO> orders = orderService.getCustomerOrders(userId);
    return ResponseEntity.ok().body(new Response(orders, PageInfo.of(null)));
  }


}
