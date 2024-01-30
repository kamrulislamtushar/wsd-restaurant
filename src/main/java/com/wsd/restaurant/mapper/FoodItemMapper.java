package com.wsd.restaurant.mapper;

import com.wsd.restaurant.domain.FoodItem;
import com.wsd.restaurant.dto.FoodItemDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link FoodItem} and its DTO {@link FoodItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface FoodItemMapper extends EntityMapper<FoodItemDTO, FoodItem> {}
