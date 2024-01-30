package com.wsd.restaurant.service.impl;

import com.wsd.restaurant.domain.OrderItem;
import com.wsd.restaurant.dto.OrderItemDTO;
import com.wsd.restaurant.repository.OrderItemsRepository;
import com.wsd.restaurant.service.OrderItemsService;
import com.wsd.restaurant.mapper.OrderItemMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.wsd.domain.OrderItems}.
 */
@Service
@Transactional
public class OrderItemsServiceImpl implements OrderItemsService {

    private final Logger log = LoggerFactory.getLogger(OrderItemsServiceImpl.class);

    private final OrderItemsRepository orderItemsRepository;

    private final OrderItemMapper orderItemMapper;

    public OrderItemsServiceImpl(OrderItemsRepository orderItemsRepository, OrderItemMapper orderItemMapper) {
        this.orderItemsRepository = orderItemsRepository;
        this.orderItemMapper = orderItemMapper;
    }

    @Override
    public OrderItemDTO save(OrderItemDTO orderItemDTO) {
        log.debug("Request to save OrderItems : {}", orderItemDTO);
        OrderItem orderItem = orderItemMapper.toEntity(orderItemDTO);
        orderItem = orderItemsRepository.save(orderItem);
        return orderItemMapper.toDto(orderItem);
    }

    @Override
    public OrderItemDTO update(OrderItemDTO orderItemsDTO) {
        log.debug("Request to update OrderItems : {}", orderItemsDTO);
        OrderItem orderItems = orderItemMapper.toEntity(orderItemsDTO);
        orderItems = orderItemsRepository.save(orderItems);
        return orderItemMapper.toDto(orderItems);
    }



    @Override
    @Transactional(readOnly = true)
    public Page<OrderItemDTO> findAll(Pageable pageable) {
        log.debug("Request to get all OrderItems");
        return orderItemsRepository.findAll(pageable).map(orderItemMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderItemDTO> findOne(Long id) {
        log.debug("Request to get OrderItems : {}", id);
        return orderItemsRepository.findById(id).map(orderItemMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete OrderItems : {}", id);
        orderItemsRepository.deleteById(id);
    }
}
