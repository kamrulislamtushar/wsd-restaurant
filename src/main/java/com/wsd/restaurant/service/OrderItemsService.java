package com.wsd.restaurant.service;

import com.wsd.restaurant.dto.OrderItemDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.wsd.domain.OrderItems}.
 */
public interface OrderItemsService {
    /**
     * Save a orderItems.
     *
     * @param orderItemDTO the entity to save.
     * @return the persisted entity.
     */
    OrderItemDTO save(OrderItemDTO orderItemDTO);

    /**
     * Updates a orderItems.
     *
     * @param orderItemDTO the entity to update.
     * @return the persisted entity.
     */
    OrderItemDTO update(OrderItemDTO orderItemDTO);


    /**
     * Get all the orderItems.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<OrderItemDTO> findAll(Pageable pageable);

    /**
     * Get the "id" orderItems.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<OrderItemDTO> findOne(Long id);

    /**
     * Delete the "id" orderItems.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
