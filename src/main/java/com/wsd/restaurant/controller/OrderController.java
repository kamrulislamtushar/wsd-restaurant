package com.wsd.restaurant.controller;

import com.wsd.restaurant.dto.OrderDTO;
import com.wsd.restaurant.dto.SaleDayInfo;
import com.wsd.restaurant.dto.response.PageInfo;
import com.wsd.restaurant.dto.response.Response;
import com.wsd.restaurant.service.OrderService;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    log.info("Received HTTP GET request for Current day total orders");
    Page<OrderDTO> orderDTOS = orderService.getTodayOrders(pageable);
    return ResponseEntity.ok().body(new Response(orderDTOS.getContent(), PageInfo.of(orderDTOS)));
  }

  @GetMapping("/total-sell/today")
  public ResponseEntity<Response<Map<String, Object>>> findTodayTotalSaleAmount() {
    log.info("Received HTTP GET request for Current day total sell");
    Double totalSell = orderService.findTodayTotalSaleAmount();
    Map<String, Object> response = new HashMap<>();
    response.put("totalSell", Objects.nonNull(totalSell) ? totalSell : 0.00);
    return ResponseEntity.ok().body(new Response(response, PageInfo.of(null)));
  }

  @GetMapping("/customers/{userId}")
  public ResponseEntity<Response<List<OrderDTO>>> getCustomerOrders(@PathVariable("userId") Long userId) {
    log.info("Received HTTP GET request for Users orders");
    List<OrderDTO> orders = orderService.getCustomerOrders(userId);
    return ResponseEntity.ok().body(new Response(orders, PageInfo.of(null)));
  }

  @GetMapping("/max-sale-day")
  public SaleDayInfo getMaxSaleDay(
      @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") String startDate,
      @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") String endDate) {
    log.info("Received HTTP GET request for max sale day in a date range");
    return orderService.getMaxSaleDay(startDate, endDate);
  }


}
