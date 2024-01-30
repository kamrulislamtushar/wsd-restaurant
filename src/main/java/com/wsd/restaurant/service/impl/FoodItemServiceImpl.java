package com.wsd.restaurant.service.impl;

import com.wsd.restaurant.domain.FoodItem;
import com.wsd.restaurant.dto.FoodItemDTO;
import com.wsd.restaurant.mapper.FoodItemMapper;
import com.wsd.restaurant.repository.FoodItemRepository;
import com.wsd.restaurant.service.FoodItemService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.wsd.domain.FoodItem}.
 */
@Service
@Transactional
public class FoodItemServiceImpl implements FoodItemService {

    private final Logger log = LoggerFactory.getLogger(FoodItemServiceImpl.class);

    private final FoodItemRepository foodItemRepository;

    private final FoodItemMapper foodItemMapper;

    public FoodItemServiceImpl(FoodItemRepository foodItemRepository, FoodItemMapper foodItemMapper) {
        this.foodItemRepository = foodItemRepository;
        this.foodItemMapper = foodItemMapper;
    }

    @Override
    public FoodItemDTO save(FoodItemDTO foodItemDTO) {
        log.debug("Request to save FoodItem : {}", foodItemDTO);
        FoodItem foodItem = foodItemMapper.toEntity(foodItemDTO);
        foodItem = foodItemRepository.save(foodItem);
        return foodItemMapper.toDto(foodItem);
    }

    @Override
    public FoodItemDTO update(FoodItemDTO foodItemDTO) {
        log.debug("Request to update FoodItem : {}", foodItemDTO);
        FoodItem foodItem = foodItemMapper.toEntity(foodItemDTO);
        foodItem = foodItemRepository.save(foodItem);
        return foodItemMapper.toDto(foodItem);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<FoodItemDTO> findAll(Pageable pageable) {
        log.debug("Request to get all FoodItems");
        return foodItemRepository.findAll(pageable).map(foodItemMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FoodItemDTO> findOne(Long id) {
        log.debug("Request to get FoodItem : {}", id);
        return foodItemRepository.findById(id).map(foodItemMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete FoodItem : {}", id);
        foodItemRepository.deleteById(id);
    }
}
