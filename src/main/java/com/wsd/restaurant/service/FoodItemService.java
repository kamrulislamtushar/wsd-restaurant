package com.wsd.restaurant.service;

import com.wsd.restaurant.dto.FoodItemDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.wsd.domain.FoodItem}.
 */
public interface FoodItemService {
    /**
     * Save a foodItem.
     *
     * @param foodItemDTO the entity to save.
     * @return the persisted entity.
     */
    FoodItemDTO save(FoodItemDTO foodItemDTO);

    /**
     * Updates a foodItem.
     *
     * @param foodItemDTO the entity to update.
     * @return the persisted entity.
     */
    FoodItemDTO update(FoodItemDTO foodItemDTO);


    /**
     * Get all the foodItems.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<FoodItemDTO> findAll(Pageable pageable);

    /**
     * Get the "id" foodItem.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<FoodItemDTO> findOne(Long id);

    /**
     * Delete the "id" foodItem.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
