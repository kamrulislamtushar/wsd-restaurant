package com.wsd.restaurant.mapper;

import com.wsd.restaurant.domain.FoodItem;
import com.wsd.restaurant.domain.Order;
import com.wsd.restaurant.domain.OrderItem;
import com.wsd.restaurant.dto.FoodItemDTO;
import com.wsd.restaurant.dto.OrderDTO;
import com.wsd.restaurant.dto.OrderItemDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link OrderItem} and its DTO {@link OrderItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface OrderItemMapper extends EntityMapper<OrderItemDTO, OrderItem> {
    @Mapping(target = "foodItem", source = "foodItem", qualifiedByName = "foodItemId")
    OrderItemDTO toDto(OrderItem s);

    @Named("orderId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OrderDTO toDtoOrderId(Order order);

    @Named("foodItemId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    FoodItemDTO toDtoFoodItemId(FoodItem foodItem);
}
