package com.wsd.restaurant.service.impl;

import com.wsd.restaurant.domain.Order;
import com.wsd.restaurant.dto.SaleDayInfo;
import com.wsd.restaurant.repository.OrderRepository;
import com.wsd.restaurant.service.OrderService;
import com.wsd.restaurant.dto.OrderDTO;
import com.wsd.restaurant.mapper.OrderMapper;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.wsd.domain.Order}.
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;

    private final OrderMapper orderMapper;

    public OrderServiceImpl(OrderRepository orderRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    public OrderDTO save(OrderDTO orderDTO) {
        log.debug("Request to save Order : {}", orderDTO);
        Order order = orderMapper.toEntity(orderDTO);
        order = orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    @Override
    public OrderDTO update(OrderDTO orderDTO) {
        log.debug("Request to update Order : {}", orderDTO);
        Order order = orderMapper.toEntity(orderDTO);
        order = orderRepository.save(order);
        return orderMapper.toDto(order);
    }



    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Orders");
        return orderRepository.findAll(pageable).map(orderMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderDTO> findOne(Long id) {
        log.debug("Request to get Order : {}", id);
        return orderRepository.findById(id).map(orderMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Order : {}", id);
        orderRepository.deleteById(id);
    }

    @Override
    public Page<OrderDTO> getTodayOrders(Pageable pageable) {
        Instant startOfDay = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant();
        return orderRepository.findByOrderTimeAfter(startOfDay, pageable).map(orderMapper::toDto);

    }

    @Override
    public Double getTodayHighestTotalPrice() {
        Instant todayStart = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant todayEnd = todayStart.plus(java.time.Duration.ofDays(1));
        return orderRepository.findTodayHighestTotalPrice(todayStart, todayEnd);
    }

    @Override
    public Double findTodayTotalSaleAmount() {
        Instant todayStart = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant todayEnd = todayStart.plus(java.time.Duration.ofDays(1));
        return orderRepository.findTodayTotalSaleAmount(todayStart, todayEnd);
    }

    @Override
    public List<OrderDTO> getCustomerOrders(Long userId) {
        return orderRepository.findAllByUserId(userId).stream().map(orderMapper::toDto).toList();
    }

    @Override
    public SaleDayInfo getMaxSaleDay(String startDate, String endDate) {
        LocalDate startLocalDate = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        LocalDate endLocalDate = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        Instant startInstant = startLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endInstant = endLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant().plusSeconds(86399); // End of the day
        return orderRepository.findMaxSaleDay(startInstant, endInstant);
    }
}
