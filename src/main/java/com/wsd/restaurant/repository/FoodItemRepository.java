package com.wsd.restaurant.repository;


import com.wsd.restaurant.domain.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the FoodItem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {}
