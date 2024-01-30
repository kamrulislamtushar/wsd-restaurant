package com.wsd.restaurant.mapper;

import com.wsd.restaurant.domain.Order;
import com.wsd.restaurant.dto.OrderDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Order} and its DTO {@link OrderDTO}.
 */
@Mapper(componentModel = "spring")
public interface OrderMapper extends EntityMapper<OrderDTO, Order> {}
